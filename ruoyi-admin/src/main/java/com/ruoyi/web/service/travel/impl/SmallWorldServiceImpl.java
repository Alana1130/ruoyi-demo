package com.ruoyi.web.service.travel.impl;

import com.ruoyi.web.domain.travel.*;
import com.ruoyi.web.mapper.travel.*;
import com.ruoyi.web.service.travel.ISmallWorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SmallWorldServiceImpl implements ISmallWorldService {

    private static final int FEED_COOLDOWN_HOURS = 6;
    private static final int PLAY_COOLDOWN_HOURS = 2;
    private static final int FEED_HUNGER_DELTA = 25;
    private static final int PLAY_MOOD_DELTA = 20;
    private static final int FEED_EXP = 5;
    private static final int PLAY_EXP = 3;
    private static final int MAX_LEVEL = 10;
    /** 升到下一级所需累计经验：L1→2=10, L2→3=28, ... L9→10=378 */
    private static final int[] EXP_THRESHOLDS = { 0, 10, 28, 54, 88, 130, 180, 238, 304, 378 };

    @Autowired private XlPetTemplateMapper petTemplateMapper;
    @Autowired private XlFoodLicenseTemplateMapper foodLicenseTemplateMapper;
    @Autowired private XlUserPetMapper userPetMapper;
    @Autowired private XlUserFoodLicenseMapper userFoodLicenseMapper;
    @Autowired private XlStarTradeMapper starTradeMapper;
    @Autowired private XlTravelUserMapper travelUserMapper;
    @Autowired private XlDestinationMapper destinationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grantRewardForDestination(Long userId, Long destinationId) {
        if (userId == null || destinationId == null) return;
        int hasPet = userPetMapper.countByUserAndDestination(userId, destinationId);
        int hasLicense = userFoodLicenseMapper.countByUserAndDestination(userId, destinationId);
        if (hasPet > 0 && hasLicense > 0) return;

        List<XlPetTemplate> pets = petTemplateMapper.selectByDestinationId(destinationId);
        List<XlFoodLicenseTemplate> licenses = foodLicenseTemplateMapper.selectByDestinationId(destinationId);
        Random r = new Random();

        if (hasPet == 0 && !pets.isEmpty() && (hasLicense > 0 || licenses.isEmpty() || r.nextBoolean())) {
            XlPetTemplate t = pets.get(r.nextInt(pets.size()));
            XlUserPet up = new XlUserPet();
            up.setUserId(userId);
            up.setPetTemplateId(t.getId());
            up.setHunger(80);
            up.setMood(80);
            up.setLevel(1);
            up.setExp(0);
            up.setEvolutionStage(0);
            userPetMapper.insert(up);
            return;
        }
        if (hasLicense == 0 && !licenses.isEmpty()) {
            XlFoodLicenseTemplate t = licenses.get(r.nextInt(licenses.size()));
            XlUserFoodLicense ul = new XlUserFoodLicense();
            ul.setUserId(userId);
            ul.setLicenseTemplateId(t.getId());
            userFoodLicenseMapper.insert(ul);
        }
    }

    @Override
    public Map<String, Object> getWorldOverview(Long userId) {
        Map<String, Object> m = new HashMap<>();
        List<XlUserPet> pets = userPetMapper.selectByUserId(userId);
        List<XlUserFoodLicense> licenses = userFoodLicenseMapper.selectByUserId(userId);
        XlTravelUser u = travelUserMapper.selectById(userId);
        m.put("petCount", pets != null ? pets.size() : 0);
        m.put("licenseCount", licenses != null ? licenses.size() : 0);
        m.put("stars", u != null && u.getStars() != null ? u.getStars() : 0);
        return m;
    }

    /** 建议售价星星数：等级与进化越高越高 */
    private static int suggestedStarsForPet(int level, int evolutionStage) {
        return 5 + (level > 0 ? level * 2 : 0) + (evolutionStage > 0 ? evolutionStage * 8 : 0);
    }

    private static final String[] EVOLUTION_NAMES = { "幼年", "成长", "成熟", "完全体" };

    @Override
    public List<Map<String, Object>> listMyPets(Long userId) {
        List<XlUserPet> list = userPetMapper.selectByUserId(userId);
        if (list == null) return Collections.emptyList();
        List<Long> listed = starTradeMapper.selectListedItemIdsBySeller(userId, "pet");
        Set<Long> listedIds = listed != null ? new HashSet<>(listed) : new HashSet<>();
        List<Map<String, Object>> result = new ArrayList<>();
        for (XlUserPet up : list) {
            if (listedIds.contains(up.getId())) continue;
            Map<String, Object> m = new HashMap<>();
            int level = up.getLevel() != null ? up.getLevel() : 1;
            int exp = up.getExp() != null ? up.getExp() : 0;
            int evolutionStage = up.getEvolutionStage() != null ? up.getEvolutionStage() : 0;
            m.put("id", up.getId());
            m.put("petTemplateId", up.getPetTemplateId());
            m.put("nickname", up.getNickname());
            m.put("hunger", up.getHunger());
            m.put("mood", up.getMood());
            m.put("level", level);
            m.put("exp", exp);
            m.put("evolutionStage", evolutionStage);
            m.put("evolutionStageName", evolutionStage < EVOLUTION_NAMES.length ? EVOLUTION_NAMES[evolutionStage] : "完全体");
            int expThresholdCurrent = (level > 1 && level <= EXP_THRESHOLDS.length) ? EXP_THRESHOLDS[level - 1] : 0;
            int expThresholdNext = (level < MAX_LEVEL && level < EXP_THRESHOLDS.length) ? EXP_THRESHOLDS[level] : EXP_THRESHOLDS[EXP_THRESHOLDS.length - 1];
            m.put("expToNextLevel", level >= MAX_LEVEL ? 0 : Math.max(0, expThresholdNext - exp));
            m.put("expThresholdNext", expThresholdNext);
            m.put("expThresholdCurrent", expThresholdCurrent);
            m.put("suggestedStars", suggestedStarsForPet(level, evolutionStage));
            m.put("lastFeedAt", up.getLastFeedAt());
            m.put("lastPlayAt", up.getLastPlayAt());
            m.put("gotAt", up.getGotAt());
            XlPetTemplate t = petTemplateMapper.selectById(up.getPetTemplateId());
            if (t != null) {
                m.put("name", t.getName());
                m.put("icon", t.getIcon());
                m.put("description", t.getDescription());
                if (t.getDestinationId() != null) {
                    XlDestination d = destinationMapper.selectById(t.getDestinationId());
                    m.put("destinationName", d != null ? d.getName() : null);
                }
            }
            result.add(m);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> listMyLicenses(Long userId) {
        List<XlUserFoodLicense> list = userFoodLicenseMapper.selectByUserId(userId);
        if (list == null) return Collections.emptyList();
        List<Long> listedL = starTradeMapper.selectListedItemIdsBySeller(userId, "food_license");
        Set<Long> listedIds = listedL != null ? new HashSet<>(listedL) : new HashSet<>();
        List<Map<String, Object>> result = new ArrayList<>();
        for (XlUserFoodLicense ul : list) {
            if (listedIds.contains(ul.getId())) continue;
            Map<String, Object> m = new HashMap<>();
            m.put("id", ul.getId());
            m.put("licenseTemplateId", ul.getLicenseTemplateId());
            m.put("gotAt", ul.getGotAt());
            XlFoodLicenseTemplate t = foodLicenseTemplateMapper.selectById(ul.getLicenseTemplateId());
            if (t != null) {
                m.put("name", t.getName());
                m.put("icon", t.getIcon());
                m.put("description", t.getDescription());
                if (t.getDestinationId() != null) {
                    XlDestination d = destinationMapper.selectById(t.getDestinationId());
                    m.put("destinationName", d != null ? d.getName() : null);
                }
            }
            result.add(m);
        }
        return result;
    }

    /** 根据等级计算进化阶段：0幼年 1成长 2成熟 3完全体 */
    private static int evolutionStageFromLevel(int level) {
        if (level <= 2) return 0;
        if (level <= 5) return 1;
        if (level <= 8) return 2;
        return 3;
    }

    /** 经验升级：返回 new int[]{ exp, level, evolutionStage } */
    private int[] addExpAndLevelUp(int currentExp, int currentLevel) {
        int exp = currentExp;
        int level = Math.min(MAX_LEVEL, currentLevel);
        int idx = level - 1;
        if (idx < 0) idx = 0;
        while (level < MAX_LEVEL && idx < EXP_THRESHOLDS.length - 1 && exp >= EXP_THRESHOLDS[idx + 1]) {
            level++;
            idx++;
        }
        int stage = evolutionStageFromLevel(level);
        return new int[] { exp, level, stage };
    }

    @Override
    public Map<String, Object> feedPet(Long userId, Long userPetId) {
        Map<String, Object> res = new HashMap<>();
        XlUserPet up = userPetMapper.selectById(userPetId);
        if (up == null || !up.getUserId().equals(userId)) {
            res.put("error", "宠物不存在或不属于你");
            return res;
        }
        long cooldown = FEED_COOLDOWN_HOURS * 3600_000L;
        if (up.getLastFeedAt() != null && System.currentTimeMillis() - up.getLastFeedAt().getTime() < cooldown) {
            res.put("error", "喂食冷却中，请稍后再试");
            return res;
        }
        int hunger = Math.min(100, (up.getHunger() != null ? up.getHunger() : 80) + FEED_HUNGER_DELTA);
        int exp = (up.getExp() != null ? up.getExp() : 0) + FEED_EXP;
        int level = up.getLevel() != null ? up.getLevel() : 1;
        int[] after = addExpAndLevelUp(exp, level);
        exp = after[0];
        level = after[1];
        int evolutionStage = after[2];
        userPetMapper.updateFeed(userPetId, hunger, exp, level, evolutionStage);
        res.put("success", true);
        res.put("hunger", hunger);
        res.put("exp", exp);
        res.put("level", level);
        res.put("evolutionStage", evolutionStage);
        return res;
    }

    @Override
    public Map<String, Object> playPet(Long userId, Long userPetId) {
        Map<String, Object> res = new HashMap<>();
        XlUserPet up = userPetMapper.selectById(userPetId);
        if (up == null || !up.getUserId().equals(userId)) {
            res.put("error", "宠物不存在或不属于你");
            return res;
        }
        long cooldown = PLAY_COOLDOWN_HOURS * 3600_000L;
        if (up.getLastPlayAt() != null && System.currentTimeMillis() - up.getLastPlayAt().getTime() < cooldown) {
            res.put("error", "互动冷却中，请稍后再试");
            return res;
        }
        int mood = Math.min(100, (up.getMood() != null ? up.getMood() : 80) + PLAY_MOOD_DELTA);
        int exp = (up.getExp() != null ? up.getExp() : 0) + PLAY_EXP;
        int level = up.getLevel() != null ? up.getLevel() : 1;
        int[] after = addExpAndLevelUp(exp, level);
        exp = after[0];
        level = after[1];
        int evolutionStage = after[2];
        userPetMapper.updatePlay(userPetId, mood, exp, level, evolutionStage);
        res.put("success", true);
        res.put("mood", mood);
        res.put("exp", exp);
        res.put("level", level);
        res.put("evolutionStage", evolutionStage);
        return res;
    }

    @Override
    public boolean updatePetNickname(Long userId, Long userPetId, String nickname) {
        XlUserPet up = userPetMapper.selectById(userPetId);
        if (up == null || !up.getUserId().equals(userId)) return false;
        return userPetMapper.updateNickname(userPetId, nickname != null ? nickname.trim() : "") > 0;
    }

    @Override
    public List<Map<String, Object>> listTradeListings(String itemType, Integer limit, Long excludeSellerId) {
        if (limit == null || limit <= 0) limit = 20;
        List<XlStarTrade> list = starTradeMapper.selectListings(itemType, limit, excludeSellerId);
        if (list == null) return Collections.emptyList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (XlStarTrade t : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", t.getId());
            m.put("sellerId", t.getSellerId());
            m.put("itemType", t.getItemType());
            m.put("itemId", t.getItemId());
            m.put("starsAmount", t.getStarsAmount());
            m.put("createdAt", t.getCreateTime());
            XlTravelUser seller = travelUserMapper.selectById(t.getSellerId());
            m.put("sellerNickname", seller != null && seller.getNickname() != null ? seller.getNickname() : "旅行者");
            if ("pet".equals(t.getItemType())) {
                XlUserPet up = userPetMapper.selectById(t.getItemId());
                if (up != null) {
                    XlPetTemplate pt = petTemplateMapper.selectById(up.getPetTemplateId());
                    m.put("itemName", pt != null ? pt.getName() : "宠物");
                    m.put("itemIcon", pt != null ? pt.getIcon() : "🐾");
                    int lv = up.getLevel() != null ? up.getLevel() : 1;
                    int es = up.getEvolutionStage() != null ? up.getEvolutionStage() : 0;
                    m.put("itemLevel", lv);
                    m.put("itemEvolutionStage", es);
                    m.put("itemEvolutionStageName", es < EVOLUTION_NAMES.length ? EVOLUTION_NAMES[es] : "完全体");
                    m.put("suggestedStars", suggestedStarsForPet(lv, es));
                }
            } else if ("food_license".equals(t.getItemType())) {
                XlUserFoodLicense ul = userFoodLicenseMapper.selectById(t.getItemId());
                if (ul != null) {
                    XlFoodLicenseTemplate fl = foodLicenseTemplateMapper.selectById(ul.getLicenseTemplateId());
                    m.put("itemName", fl != null ? fl.getName() : "经营权");
                    m.put("itemIcon", fl != null ? fl.getIcon() : "🍜");
                }
            }
            result.add(m);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> listMyListings(Long userId) {
        List<XlStarTrade> list = starTradeMapper.selectBySellerIdAndStatus(userId, "0");
        if (list == null) return Collections.emptyList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (XlStarTrade t : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", t.getId());
            m.put("itemType", t.getItemType());
            m.put("itemId", t.getItemId());
            m.put("starsAmount", t.getStarsAmount());
            m.put("createdAt", t.getCreateTime());
            if ("pet".equals(t.getItemType())) {
                XlUserPet up = userPetMapper.selectById(t.getItemId());
                if (up != null) {
                    XlPetTemplate pt = petTemplateMapper.selectById(up.getPetTemplateId());
                    m.put("itemName", pt != null ? pt.getName() : "宠物");
                    m.put("itemIcon", pt != null ? pt.getIcon() : "🐾");
                    int lv = up.getLevel() != null ? up.getLevel() : 1;
                    int es = up.getEvolutionStage() != null ? up.getEvolutionStage() : 0;
                    m.put("itemLevel", lv);
                    m.put("itemEvolutionStageName", es < EVOLUTION_NAMES.length ? EVOLUTION_NAMES[es] : "完全体");
                    m.put("suggestedStars", suggestedStarsForPet(lv, es));
                }
            } else if ("food_license".equals(t.getItemType())) {
                XlUserFoodLicense ul = userFoodLicenseMapper.selectById(t.getItemId());
                if (ul != null) {
                    XlFoodLicenseTemplate fl = foodLicenseTemplateMapper.selectById(ul.getLicenseTemplateId());
                    m.put("itemName", fl != null ? fl.getName() : "经营权");
                    m.put("itemIcon", fl != null ? fl.getIcon() : "🍜");
                }
            }
            result.add(m);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createListing(Long userId, String itemType, Long itemId, Integer starsAmount) {
        Map<String, Object> res = new HashMap<>();
        if (starsAmount == null || starsAmount <= 0) {
            res.put("error", "请设置星星数量");
            return res;
        }
        if ("pet".equals(itemType)) {
            XlUserPet up = userPetMapper.selectById(itemId);
            if (up == null || !up.getUserId().equals(userId)) {
                res.put("error", "宠物不存在或不属于你");
                return res;
            }
            if (starTradeMapper.selectListingByItem("pet", itemId) != null) {
                res.put("error", "该宠物已在挂单中");
                return res;
            }
        } else if ("food_license".equals(itemType)) {
            XlUserFoodLicense ul = userFoodLicenseMapper.selectById(itemId);
            if (ul == null || !ul.getUserId().equals(userId)) {
                res.put("error", "经营权不存在或不属于你");
                return res;
            }
            if (starTradeMapper.selectListingByItem("food_license", itemId) != null) {
                res.put("error", "该经营权已在挂单中");
                return res;
            }
        } else {
            res.put("error", "类型错误");
            return res;
        }
        XlStarTrade trade = new XlStarTrade();
        trade.setSellerId(userId);
        trade.setItemType(itemType);
        trade.setItemId(itemId);
        trade.setStarsAmount(starsAmount);
        trade.setStatus("0");
        starTradeMapper.insert(trade);
        res.put("success", true);
        res.put("tradeId", trade.getId());
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> buyListing(Long buyerId, Long tradeId) {
        Map<String, Object> res = new HashMap<>();
        XlStarTrade trade = starTradeMapper.selectById(tradeId);
        if (trade == null || !"0".equals(trade.getStatus())) {
            res.put("error", "交易不存在或已关闭");
            return res;
        }
        if (trade.getSellerId().equals(buyerId)) {
            res.put("error", "不能购买自己的挂单");
            return res;
        }
        XlTravelUser buyer = travelUserMapper.selectById(buyerId);
        if (buyer == null || buyer.getStars() == null || buyer.getStars() < trade.getStarsAmount()) {
            res.put("error", "星星不足");
            return res;
        }
        travelUserMapper.updateStars(buyerId, -trade.getStarsAmount());
        travelUserMapper.updateStars(trade.getSellerId(), trade.getStarsAmount());
        starTradeMapper.updateToCompleted(tradeId, buyerId);
        if ("pet".equals(trade.getItemType())) {
            userPetMapper.updateOwner(trade.getItemId(), buyerId);
        } else {
            userFoodLicenseMapper.updateOwner(trade.getItemId(), buyerId);
        }
        res.put("success", true);
        res.put("itemType", trade.getItemType());
        res.put("itemId", trade.getItemId());
        return res;
    }

    @Override
    public boolean cancelListing(Long userId, Long tradeId) {
        XlStarTrade trade = starTradeMapper.selectById(tradeId);
        if (trade == null || !"0".equals(trade.getStatus()) || !trade.getSellerId().equals(userId)) return false;
        return starTradeMapper.updateToCancelled(tradeId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> seedDemoData(Long userId) {
        Map<String, Object> res = new HashMap<>();
        res.put("petCountAdded", 0);
        res.put("licenseCountAdded", 0);
        if (userId == null) return res;

        List<XlPetTemplate> allPets = petTemplateMapper.selectAll();
        List<XlFoodLicenseTemplate> allLicenses = foodLicenseTemplateMapper.selectAll();
        if (allPets == null) allPets = new ArrayList<>();
        if (allLicenses == null) allLicenses = new ArrayList<>();
        Random r = new Random();
        int petAdd = 0, licenseAdd = 0;
        int wantPets = Math.min(2, allPets.size());
        int wantLicenses = Math.min(2, allLicenses.size());
        Set<Long> usedPetIds = new HashSet<>();
        Set<Long> usedLicenseIds = new HashSet<>();
        while (petAdd < wantPets && !allPets.isEmpty()) {
            XlPetTemplate t = allPets.get(r.nextInt(allPets.size()));
            if (usedPetIds.contains(t.getId())) continue;
            usedPetIds.add(t.getId());
            XlUserPet up = new XlUserPet();
            up.setUserId(userId);
            up.setPetTemplateId(t.getId());
            up.setHunger(80);
            up.setMood(80);
            up.setLevel(1);
            up.setExp(0);
            up.setEvolutionStage(0);
            userPetMapper.insert(up);
            petAdd++;
        }
        while (licenseAdd < wantLicenses && !allLicenses.isEmpty()) {
            XlFoodLicenseTemplate t = allLicenses.get(r.nextInt(allLicenses.size()));
            if (usedLicenseIds.contains(t.getId())) continue;
            usedLicenseIds.add(t.getId());
            XlUserFoodLicense ul = new XlUserFoodLicense();
            ul.setUserId(userId);
            ul.setLicenseTemplateId(t.getId());
            userFoodLicenseMapper.insert(ul);
            licenseAdd++;
        }
        res.put("petCountAdded", petAdd);
        res.put("licenseCountAdded", licenseAdd);
        return res;
    }
}
