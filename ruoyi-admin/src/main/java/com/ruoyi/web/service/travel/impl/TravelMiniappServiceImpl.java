package com.ruoyi.web.service.travel.impl;

import com.ruoyi.web.domain.travel.*;
import com.ruoyi.web.mapper.travel.*;
import com.ruoyi.web.service.travel.ITravelMiniappService;
import com.ruoyi.web.service.travel.ISmallWorldService;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 旅游小程序 服务实现
 */
@Service
public class TravelMiniappServiceImpl implements ITravelMiniappService {

    @Autowired private XlDestinationMapper destinationMapper;
    @Autowired private XlAttractionMapper attractionMapper;
    @Autowired private XlFoodMapper foodMapper;
    @Autowired private XlAccommodationMapper accommodationMapper;
    @Autowired private XlRouteMapper routeMapper;
    @Autowired private XlRouteItemMapper routeItemMapper;
    @Autowired private XlTravelUserMapper travelUserMapper;
    @Autowired private XlUserCheckinMapper userCheckinMapper;
    @Autowired private XlRewardItemMapper rewardItemMapper;
    @Autowired private XlUserExchangeMapper userExchangeMapper;
    @Autowired private XlBannerMapper bannerMapper;
    @Autowired private XlTaxiMapper taxiMapper;
    @Autowired private XlTicketMapper ticketMapper;
    @Autowired private XlRewardOrderMapper rewardOrderMapper;
    @Autowired private XlUserExperienceMapper userExperienceMapper;
    @Autowired private XlUserAddressMapper userAddressMapper;
    @Autowired private XlOpportunityMapper opportunityMapper;
    @Autowired private XlChatMessageMapper chatMessageMapper;
    @Autowired private XlNotificationMapper notificationMapper;
    @Autowired private XlCompanionPostMapper companionPostMapper;
    @Autowired private ISmallWorldService smallWorldService;

    @Override
    public List<XlBanner> listBanners() {
        return bannerMapper.selectList();
    }

    @Override
    public List<XlDestination> listDestinations() {
        return destinationMapper.selectList(new XlDestination());
    }

    @Override
    public Map<String, Object> getDestinationDetail(Long id) {
        XlDestination dest = destinationMapper.selectById(id);
        if (dest == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("id", dest.getId());
        map.put("name", dest.getName());
        map.put("region", dest.getRegion());
        map.put("description", dest.getDescription());
        map.put("coverImage", dest.getCoverImage());
        map.put("starReward", dest.getStarReward() != null ? dest.getStarReward() : 1);
        map.put("attractions", attractionMapper.selectByDestinationId(id));
        map.put("foods", foodMapper.selectByDestinationId(id));
        map.put("accommodations", accommodationMapper.selectByDestinationId(id));
        map.put("opportunities", listOpportunities(id, null));
        return map;
    }

    @Override
    public List<XlRoute> listRoutesByDestination(Long destinationId) {
        return routeMapper.selectByDestinationId(destinationId);
    }

    @Override
    public Map<String, Object> getRouteDetail(Long routeId) {
        XlRoute route = routeMapper.selectById(routeId);
        if (route == null) return null;
        List<XlRouteItem> items = routeItemMapper.selectByRouteId(routeId);
        Map<Integer, List<Map<String, Object>>> daysDetail = new TreeMap<>();
        for (XlRouteItem item : items) {
            Map<String, Object> node = new HashMap<>();
            node.put("itemType", item.getItemType());
            node.put("itemId", item.getItemId());
            node.put("transportApprox", item.getTransportApprox());
            String detailName = null;
            String detailExtra = null;
            if ("attraction".equals(item.getItemType())) {
                XlAttraction a = attractionMapper.selectById(item.getItemId());
                if (a != null) {
                    detailName = a.getName();
                    detailExtra = a.getTicketPrice() != null && a.getTicketPrice().signum() > 0 ? "¥" + a.getTicketPrice() : "免费";
                    if (a.getLatitude() != null) node.put("latitude", a.getLatitude());
                    if (a.getLongitude() != null) node.put("longitude", a.getLongitude());
                }
            } else if ("food".equals(item.getItemType())) {
                XlFood f = foodMapper.selectById(item.getItemId());
                if (f != null) {
                    detailName = f.getName();
                    detailExtra = f.getPriceRange();
                    if (f.getLatitude() != null) node.put("latitude", f.getLatitude());
                    if (f.getLongitude() != null) node.put("longitude", f.getLongitude());
                }
            } else if ("accommodation".equals(item.getItemType())) {
                XlAccommodation a = accommodationMapper.selectById(item.getItemId());
                if (a != null) {
                    detailName = a.getName();
                    detailExtra = a.getPricePerNight() != null ? "¥" + a.getPricePerNight() + "/晚" : null;
                    if (a.getLatitude() != null) node.put("latitude", a.getLatitude());
                    if (a.getLongitude() != null) node.put("longitude", a.getLongitude());
                }
            }
            node.put("detailName", detailName);
            node.put("detailExtra", detailExtra);
            daysDetail.computeIfAbsent(item.getDayNo(), k -> new ArrayList<>()).add(node);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", route.getId());
        map.put("destinationId", route.getDestinationId());
        map.put("name", route.getName());
        map.put("days", route.getDays());
        map.put("totalBudgetMin", route.getTotalBudgetMin());
        map.put("totalBudgetMax", route.getTotalBudgetMax());
        map.put("description", route.getDescription());
        map.put("daysDetail", daysDetail);
        return map;
    }

    @Override
    public List<Map<String, Object>> budgetPlan(Integer maxBudget, Integer days, Long destinationId) {
        if (maxBudget == null) maxBudget = 1000;
        if (days == null) days = 2;
        BigDecimal max = BigDecimal.valueOf(maxBudget);
        List<XlDestination> destList = destinationId != null ? Arrays.asList(destinationMapper.selectById(destinationId)) : destinationMapper.selectList(new XlDestination());
        List<Map<String, Object>> result = new ArrayList<>();
        for (XlDestination d : destList) {
            if (d == null) continue;
            List<XlRoute> routes = routeMapper.selectByBudget(d.getId(), days, max);
            for (XlRoute r : routes) {
                Map<String, Object> plan = new HashMap<>();
                plan.put("destination", d);
                plan.put("route", r);
                List<XlAccommodation> accs = accommodationMapper.selectByDestinationId(d.getId());
                List<XlAccommodation> fit = new ArrayList<>();
                for (XlAccommodation a : accs) {
                    if (a.getPricePerNight() != null && a.getPricePerNight().multiply(BigDecimal.valueOf(days)).compareTo(max.multiply(BigDecimal.valueOf(0.5))) <= 0)
                        fit.add(a);
                }
                plan.put("accommodations", fit);
                result.add(plan);
            }
        }
        return result.size() > 20 ? result.subList(0, 20) : result;
    }

    @Override
    public XlTravelUser getUserById(Long userId) {
        return userId != null ? travelUserMapper.selectById(userId) : null;
    }

    @Override
    public boolean updateProfile(Long userId, String nickname, String avatar) {
        if (userId == null) return false;
        XlTravelUser u = new XlTravelUser();
        u.setId(userId);
        u.setNickname(nickname);
        u.setAvatar(avatar);
        return travelUserMapper.updateProfile(u) > 0;
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword)) return false;
        XlTravelUser user = travelUserMapper.selectById(userId);
        if (user == null || user.getPassword() == null) return false;
        if (!SecurityUtils.matchesPassword(oldPassword, user.getPassword())) return false;
        return travelUserMapper.updatePassword(userId, SecurityUtils.encryptPassword(newPassword)) > 0;
    }

    @Override
    public boolean bindPhone(Long userId, String phone, String password) {
        if (userId == null || StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) return false;
        phone = phone.trim();
        if (travelUserMapper.selectByPhone(phone) != null) return false;
        XlTravelUser user = travelUserMapper.selectById(userId);
        if (user == null || (user.getPhone() != null && !user.getPhone().isEmpty())) return false;
        return travelUserMapper.updatePhoneAndPassword(userId, phone, SecurityUtils.encryptPassword(password)) > 0;
    }

    @Override
    public long submitExperience(Long userId, String targetType, Long targetId, String content) {
        return submitExperience(userId, targetType, targetId, content, null, null);
    }

    @Override
    public long submitExperience(Long userId, String targetType, Long targetId, String content, String title, String tags) {
        if (userId == null || content == null || content.trim().isEmpty()) return 0;
        XlUserExperience exp = new XlUserExperience();
        exp.setUserId(userId);
        exp.setTargetType(targetType);
        exp.setTargetId(targetId);
        exp.setContent(content.trim());
        exp.setTitle(title != null && !title.isEmpty() ? title.trim() : null);
        exp.setTags(tags != null && !tags.isEmpty() ? tags.trim() : null);
        userExperienceMapper.insert(exp);
        // 攻略（目的地级游记）提交后直接通过，便于在攻略列表展示；其余类型仍待 AI 核验
        if ("destination".equals(targetType) && exp.getId() != null) {
            userExperienceMapper.updateAiVerify(exp.getId(), 1, BigDecimal.valueOf(1.0));
        }
        return exp.getId();
    }

    @Override
    public List<XlUserExperience> listExperiences(String targetType, Long targetId) {
        if (targetType == null || targetId == null) return Collections.emptyList();
        return userExperienceMapper.selectVerifiedByTarget(targetType, targetId);
    }

    @Override
    public boolean verifyExperience(Long experienceId) {
        if (experienceId == null) return false;
        XlUserExperience exp = userExperienceMapper.selectById(experienceId);
        if (exp == null) return false;
        // 模拟 AI 真实性得分（实际可接入大模型 API）
        double score = 0.82 + Math.random() * 0.16;
        BigDecimal aiScore = BigDecimal.valueOf(Math.round(score * 100) / 100.0);
        return userExperienceMapper.updateAiVerify(experienceId, 1, aiScore) > 0;
    }

    @Override
    public XlTravelUser getOrCreateUser(String openidOrUserId) {
        if (openidOrUserId == null || openidOrUserId.isEmpty()) {
            openidOrUserId = "guest_" + System.currentTimeMillis();
        }
        XlTravelUser user = travelUserMapper.selectByOpenid(openidOrUserId);
        if (user == null) {
            user = new XlTravelUser();
            user.setOpenid(openidOrUserId);
            user.setStars(0);
            travelUserMapper.insert(user);
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public XlTravelUser register(String phone, String password, String nickname) {
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) return null;
        phone = phone.trim();
        if (travelUserMapper.selectByPhone(phone) != null) return null;
        XlTravelUser user = new XlTravelUser();
        user.setOpenid("phone_" + phone);
        user.setPhone(phone);
        user.setPassword(SecurityUtils.encryptPassword(password));
        user.setNickname(StringUtils.isNotEmpty(nickname) ? nickname.trim() : "用户" + phone.substring(7));
        user.setStars(0);
        user.setSchoolVerified(0);
        travelUserMapper.insert(user);
        return user;
    }

    @Override
    public XlTravelUser loginByPhone(String phone, String password) {
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) return null;
        XlTravelUser user = travelUserMapper.selectByPhone(phone.trim());
        if (user == null || user.getPassword() == null) return null;
        if (!SecurityUtils.matchesPassword(password, user.getPassword())) return null;
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> lightDestination(Long userId, Long destinationId) {
        Map<String, Object> res = new HashMap<>();
        XlDestination dest = destinationMapper.selectById(destinationId);
        if (dest == null) {
            res.put("error", "目的地不存在");
            return res;
        }
        XlUserCheckin exist = userCheckinMapper.selectByUserAndDestination(userId, destinationId);
        if (exist != null) {
            res.put("already", true);
            res.put("starsEarned", exist.getStarsEarned());
            XlTravelUser u = travelUserMapper.selectById(userId);
            res.put("userStars", u != null ? u.getStars() : 0);
            return res;
        }
        int starsEarned = dest.getStarReward() != null ? dest.getStarReward() : 1;
        XlUserCheckin checkin = new XlUserCheckin();
        checkin.setUserId(userId);
        checkin.setDestinationId(destinationId);
        checkin.setStarsEarned(starsEarned);
        userCheckinMapper.insert(checkin);
        travelUserMapper.updateStars(userId, starsEarned);
        try { smallWorldService.grantRewardForDestination(userId, destinationId); } catch (Exception ignored) {}
        XlTravelUser u = travelUserMapper.selectById(userId);
        res.put("success", true);
        res.put("starsEarned", starsEarned);
        res.put("userStars", u != null ? u.getStars() : starsEarned);
        return res;
    }

    @Override
    public List<XlUserCheckin> myCheckins(Long userId) {
        return userCheckinMapper.selectMyCheckins(userId);
    }

    @Override
    public List<XlRewardItem> listRewardItems(String type) {
        return rewardItemMapper.selectList(type);
    }

    @Override
    public XlRewardItem getRewardItemById(Long id) {
        return id != null ? rewardItemMapper.selectById(id) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> exchange(Long userId, Long rewardItemId) {
        Map<String, Object> res = new HashMap<>();
        XlTravelUser user = travelUserMapper.selectById(userId);
        if (user == null) {
            res.put("error", "用户不存在");
            return res;
        }
        XlRewardItem item = rewardItemMapper.selectById(rewardItemId);
        if (item == null) {
            res.put("error", "商品不存在");
            return res;
        }
        if (item.getStock() != null && item.getStock() == 0) {
            res.put("error", "已兑完");
            return res;
        }
        if (user.getStars() == null || user.getStars() < item.getStarsCost()) {
            res.put("error", "星星不足");
            return res;
        }
        travelUserMapper.updateStars(userId, -item.getStarsCost());
        if (item.getStock() != null && item.getStock() > 0) {
            rewardItemMapper.decrStock(rewardItemId);
        }
        XlUserExchange ex = new XlUserExchange();
        ex.setUserId(userId);
        ex.setRewardItemId(rewardItemId);
        userExchangeMapper.insert(ex);
        XlTravelUser updated = travelUserMapper.selectById(userId);
        res.put("success", true);
        res.put("userStars", updated != null ? updated.getStars() : 0);
        res.put("item", item.getName());
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> purchase(Long userId, Long rewardItemId, String receiverName, String receiverPhone, String receiverAddress) {
        Map<String, Object> res = new HashMap<>();
        XlTravelUser user = travelUserMapper.selectById(userId);
        if (user == null) {
            res.put("error", "用户不存在");
            return res;
        }
        XlRewardItem item = rewardItemMapper.selectById(rewardItemId);
        if (item == null) {
            res.put("error", "商品不存在");
            return res;
        }
        if (item.getAllowPurchase() == null || item.getAllowPurchase() != 1) {
            res.put("error", "该商品不支持直接购买");
            return res;
        }
        if (item.getPrice() == null || item.getPrice().signum() <= 0) {
            res.put("error", "商品价格异常");
            return res;
        }
        if (item.getStock() != null && item.getStock() == 0) {
            res.put("error", "已售完");
            return res;
        }
        XlRewardOrder order = new XlRewardOrder();
        order.setUserId(userId);
        order.setRewardItemId(rewardItemId);
        order.setOrderType("purchase");
        order.setAmountPaid(item.getPrice());
        order.setStarsUsed(0);
        order.setStatus("1");
        order.setReceiverName(receiverName != null && !receiverName.isEmpty() ? receiverName : null);
        order.setReceiverPhone(receiverPhone != null && !receiverPhone.isEmpty() ? receiverPhone : null);
        order.setReceiverAddress(receiverAddress != null && !receiverAddress.isEmpty() ? receiverAddress : null);
        rewardOrderMapper.insert(order);
        if (item.getStock() != null && item.getStock() > 0) {
            rewardItemMapper.decrStock(rewardItemId);
        }
        res.put("success", true);
        res.put("item", item.getName());
        res.put("amount", item.getPrice());
        res.put("message", "下单成功，请按页面提示完成支付");
        return res;
    }

    @Override
    public List<XlUserExchange> myExchanges(Long userId) {
        return userExchangeMapper.selectByUserId(userId);
    }

    @Override
    public List<Map<String, Object>> myRecords(Long userId) {
        if (userId == null) return new ArrayList<>();
        List<XlUserExchange> exchanges = userExchangeMapper.selectByUserId(userId);
        List<Map<String, Object>> orders = rewardOrderMapper.selectOrdersByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        if (exchanges != null) {
            for (XlUserExchange e : exchanges) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", e.getId());
                m.put("itemName", e.getItemName() != null ? e.getItemName() : "兑换商品");
                m.put("type", "exchange");
                m.put("starsCost", e.getStarsCost());
                m.put("amountPaid", null);
                m.put("createTime", e.getExchangedAt() != null ? e.getExchangedAt() : e.getCreateTime());
                result.add(m);
            }
        }
        if (orders != null) {
            for (Map<String, Object> o : orders) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", "o_" + o.get("id"));
                m.put("itemName", o.get("itemName") != null ? o.get("itemName") : "购买商品");
                m.put("type", "purchase".equals(o.get("orderType")) ? "purchase" : "exchange");
                m.put("starsCost", o.get("starsUsed"));
                m.put("amountPaid", o.get("amountPaid"));
                m.put("createTime", o.get("createTime"));
                result.add(m);
            }
        }
        result.sort((a, b) -> {
            Object at = a.get("createTime");
            Object bt = b.get("createTime");
            if (at == null && bt == null) return 0;
            if (at == null) return 1;
            if (bt == null) return -1;
            long am = toEpochMilli(at);
            long bm = toEpochMilli(bt);
            return Long.compare(bm, am);
        });
        return result;
    }

    /** 将 Date 或 LocalDateTime 转为毫秒时间戳，用于统一排序 */
    private static long toEpochMilli(Object time) {
        if (time instanceof java.util.Date) {
            return ((java.util.Date) time).getTime();
        }
        if (time instanceof LocalDateTime) {
            return ((LocalDateTime) time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        return 0L;
    }

    @Override
    public List<XlTaxi> listTaxis(Long destinationId) {
        return destinationId != null ? taxiMapper.selectByDestinationId(destinationId) : Collections.emptyList();
    }

    @Override
    public List<XlTicket> listTickets(Long destinationId) {
        return destinationId != null ? ticketMapper.selectByDestinationId(destinationId) : Collections.emptyList();
    }

    @Override
    public List<XlUserAddress> listAddresses(Long userId) {
        if (userId == null) return Collections.emptyList();
        List<XlUserAddress> list = userAddressMapper.selectByUserId(userId);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addAddress(Long userId, String receiverName, String receiverPhone, String receiverAddress, Integer isDefault) {
        if (userId == null || receiverName == null || receiverPhone == null || receiverAddress == null) return 0;
        if (Integer.valueOf(1).equals(isDefault)) userAddressMapper.clearDefaultByUserId(userId);
        XlUserAddress a = new XlUserAddress();
        a.setUserId(userId);
        a.setReceiverName(receiverName.trim());
        a.setReceiverPhone(receiverPhone.trim());
        a.setReceiverAddress(receiverAddress.trim());
        a.setIsDefault(isDefault != null ? isDefault : 0);
        userAddressMapper.insert(a);
        return a.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAddress(Long userId, Long id, String receiverName, String receiverPhone, String receiverAddress, Integer isDefault) {
        if (userId == null || id == null) return false;
        XlUserAddress a = userAddressMapper.selectById(id);
        if (a == null || !userId.equals(a.getUserId())) return false;
        if (receiverName != null) a.setReceiverName(receiverName.trim());
        if (receiverPhone != null) a.setReceiverPhone(receiverPhone.trim());
        if (receiverAddress != null) a.setReceiverAddress(receiverAddress.trim());
        if (isDefault != null) {
            a.setIsDefault(isDefault);
            if (isDefault == 1) userAddressMapper.clearDefaultByUserId(userId);
        }
        return userAddressMapper.update(a) > 0;
    }

    @Override
    public boolean deleteAddress(Long userId, Long id) {
        if (userId == null || id == null) return false;
        XlUserAddress a = userAddressMapper.selectById(id);
        if (a == null || !userId.equals(a.getUserId())) return false;
        return userAddressMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultAddress(Long userId, Long id) {
        if (userId == null || id == null) return false;
        XlUserAddress a = userAddressMapper.selectById(id);
        if (a == null || !userId.equals(a.getUserId())) return false;
        userAddressMapper.clearDefaultByUserId(userId);
        a.setIsDefault(1);
        return userAddressMapper.update(a) > 0;
    }

    @Override
    public List<XlOpportunity> listOpportunities(Long destinationId, String type) {
        List<XlOpportunity> list = opportunityMapper.selectList(destinationId, type);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public XlOpportunity getOpportunityById(Long id) {
        return id != null ? opportunityMapper.selectById(id) : null;
    }

    @Override
    public List<XlChatMessage> listChatMessages(String topicType, Long topicId) {
        if (topicType == null || topicId == null) return Collections.emptyList();
        List<XlChatMessage> list = chatMessageMapper.selectByTopic(topicType, topicId);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public long postChatMessage(Long userId, String topicType, Long topicId, String content, Long parentId) {
        if (userId == null || topicType == null || topicId == null || content == null || content.trim().isEmpty())
            return 0;
        XlTravelUser user = travelUserMapper.selectById(userId);
        String nickname = user != null && user.getNickname() != null ? user.getNickname() : "旅行者";
        XlChatMessage msg = new XlChatMessage();
        msg.setTopicType(topicType);
        msg.setTopicId(topicId);
        msg.setUserId(userId);
        msg.setParentId(parentId);
        msg.setNickname(nickname);
        msg.setContent(content.trim());
        msg.setCreateTime(new Date());
        chatMessageMapper.insert(msg);
        if (parentId != null) {
            XlChatMessage parent = chatMessageMapper.selectById(parentId);
            if (parent != null && !userId.equals(parent.getUserId())) {
                String snippet = content.length() > 30 ? content.substring(0, 30) + "…" : content;
                createNotification(parent.getUserId(), "discussion_reply", "有人回复了你的讨论", snippet, msg.getId(), "chat");
            }
        }
        return msg.getId();
    }

    @Override
    public List<XlNotification> listNotifications(Long userId) {
        if (userId == null) return Collections.emptyList();
        List<XlNotification> list = notificationMapper.selectByUserId(userId);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public int getNotificationUnreadCount(Long userId) {
        return userId != null ? notificationMapper.countUnreadByUserId(userId) : 0;
    }

    @Override
    public boolean markNotificationRead(Long userId, Long id) {
        return userId != null && id != null && notificationMapper.updateRead(id, userId) > 0;
    }

    @Override
    public boolean markAllNotificationsRead(Long userId) {
        return userId != null && notificationMapper.markAllRead(userId) >= 0;
    }

    @Override
    public List<XlNotification> seedDemoNotifications(Long userId) {
        if (userId == null) return Collections.emptyList();
        List<XlNotification> existing = notificationMapper.selectByUserId(userId);
        if (existing != null && !existing.isEmpty()) return existing;
        createNotification(userId, "system", "欢迎使用星旅", "在这里你会收到讨论回复、点赞评论等消息通知。", null, null);
        createNotification(userId, "discussion_reply", "有人回复了你的讨论", "「这条是演示消息，方便你熟悉消息中心～」", null, "chat");
        return notificationMapper.selectByUserId(userId);
    }

    private void createNotification(Long userId, String type, String title, String content, Long relatedId, String relatedType) {
        XlNotification n = new XlNotification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setContent(content);
        n.setRelatedId(relatedId);
        n.setRelatedType(relatedType);
        n.setIsRead(0);
        n.setCreateTime(new Date());
        notificationMapper.insert(n);
    }

    @Override
    public Map<String, Object> search(String keyword) {
        Map<String, Object> result = new HashMap<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            result.put("destinations", Collections.emptyList());
            result.put("opportunities", Collections.emptyList());
            return result;
        }
        String k = keyword.trim();
        List<XlDestination> dest = destinationMapper.selectByKeyword(k);
        List<XlOpportunity> opp = opportunityMapper.selectByKeyword(k);
        result.put("destinations", dest != null ? dest : Collections.emptyList());
        result.put("opportunities", opp != null ? opp : Collections.emptyList());
        return result;
    }

    @Override
    public List<Map<String, Object>> listCompanions(Long destinationId, String keyword, Date dateFrom, Date dateTo) {
        List<XlCompanionPost> list = companionPostMapper.selectList(destinationId, keyword, dateFrom, dateTo, "0");
        if (list == null) return Collections.emptyList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (XlCompanionPost p : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("userId", p.getUserId());
            m.put("destinationId", p.getDestinationId());
            m.put("title", p.getTitle());
            m.put("content", p.getContent());
            m.put("travelDateStart", p.getTravelDateStart());
            m.put("travelDateEnd", p.getTravelDateEnd());
            m.put("budgetRange", p.getBudgetRange());
            m.put("contactRemark", p.getContactRemark());
            m.put("status", p.getStatus());
            m.put("createTime", p.getCreateTime());
            XlTravelUser u = travelUserMapper.selectById(p.getUserId());
            m.put("nickname", u != null && u.getNickname() != null && !u.getNickname().isEmpty() ? u.getNickname() : "旅行者");
            if (p.getDestinationId() != null) {
                XlDestination d = destinationMapper.selectById(p.getDestinationId());
                m.put("destinationName", d != null ? d.getName() : null);
            } else {
                m.put("destinationName", null);
            }
            result.add(m);
        }
        return result;
    }

    @Override
    public Map<String, Object> getCompanionById(Long id) {
        if (id == null) return null;
        XlCompanionPost p = companionPostMapper.selectById(id);
        if (p == null) return null;
        Map<String, Object> m = new HashMap<>();
        m.put("id", p.getId());
        m.put("userId", p.getUserId());
        m.put("destinationId", p.getDestinationId());
        m.put("title", p.getTitle());
        m.put("content", p.getContent());
        m.put("travelDateStart", p.getTravelDateStart());
        m.put("travelDateEnd", p.getTravelDateEnd());
        m.put("budgetRange", p.getBudgetRange());
        m.put("contactRemark", p.getContactRemark());
        m.put("status", p.getStatus());
        m.put("createTime", p.getCreateTime());
        XlTravelUser u = travelUserMapper.selectById(p.getUserId());
        m.put("nickname", u != null && u.getNickname() != null && !u.getNickname().isEmpty() ? u.getNickname() : "旅行者");
        if (p.getDestinationId() != null) {
            XlDestination d = destinationMapper.selectById(p.getDestinationId());
            m.put("destinationName", d != null ? d.getName() : null);
        } else {
            m.put("destinationName", null);
        }
        return m;
    }

    @Override
    public long createCompanion(Long userId, Long destinationId, String title, String content, Date travelDateStart, Date travelDateEnd, String budgetRange, String contactRemark) {
        if (userId == null || title == null || title.trim().isEmpty()) return 0;
        XlCompanionPost post = new XlCompanionPost();
        post.setUserId(userId);
        post.setDestinationId(destinationId);
        post.setTitle(title.trim());
        post.setContent(content != null ? content.trim() : "");
        post.setTravelDateStart(travelDateStart);
        post.setTravelDateEnd(travelDateEnd);
        post.setBudgetRange(budgetRange != null ? budgetRange.trim() : "");
        post.setContactRemark(contactRemark != null ? contactRemark.trim() : "");
        post.setStatus("0");
        companionPostMapper.insert(post);
        return post.getId();
    }

    @Override
    public boolean updateCompanion(Long userId, Long id, String title, String content, Date travelDateStart, Date travelDateEnd, String budgetRange, String contactRemark, String status) {
        if (userId == null || id == null) return false;
        XlCompanionPost p = companionPostMapper.selectById(id);
        if (p == null || !p.getUserId().equals(userId)) return false;
        if (title != null) p.setTitle(title.trim());
        if (content != null) p.setContent(content.trim());
        p.setTravelDateStart(travelDateStart);
        p.setTravelDateEnd(travelDateEnd);
        if (budgetRange != null) p.setBudgetRange(budgetRange.trim());
        if (contactRemark != null) p.setContactRemark(contactRemark.trim());
        if (status != null) p.setStatus(status);
        return companionPostMapper.update(p) > 0;
    }

    @Override
    public boolean deleteCompanion(Long userId, Long id) {
        return userId != null && id != null && companionPostMapper.deleteByIdAndUser(id, userId) > 0;
    }

    @Override
    public List<Map<String, Object>> listGuides(Long destinationId, String tag) {
        List<Map<String, Object>> list = userExperienceMapper.selectGuides(destinationId, tag);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public Map<String, Object> getExperienceById(Long experienceId) {
        if (experienceId == null) return null;
        XlUserExperience e = userExperienceMapper.selectById(experienceId);
        if (e == null) return null;
        Map<String, Object> m = new HashMap<>();
        m.put("id", e.getId());
        m.put("userId", e.getUserId());
        m.put("targetType", e.getTargetType());
        m.put("targetId", e.getTargetId());
        m.put("content", e.getContent());
        m.put("title", e.getTitle());
        m.put("tags", e.getTags());
        m.put("createTime", e.getCreateTime());
        XlTravelUser u = travelUserMapper.selectById(e.getUserId());
        m.put("nickname", u != null && u.getNickname() != null && !u.getNickname().isEmpty() ? u.getNickname() : "旅行者");
        if ("destination".equals(e.getTargetType()) && e.getTargetId() != null) {
            XlDestination d = destinationMapper.selectById(e.getTargetId());
            m.put("destinationName", d != null ? d.getName() : null);
        } else {
            m.put("destinationName", null);
        }
        return m;
    }
}
