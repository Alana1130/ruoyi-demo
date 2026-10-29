package com.ruoyi.web.service.travel.impl;

import com.ruoyi.web.domain.travel.*;
import com.ruoyi.web.mapper.travel.*;
import com.ruoyi.web.service.travel.ITripPlanIdleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TripPlanIdleServiceImpl implements ITripPlanIdleService {

    @Autowired private XlTripCountdownMapper countdownMapper;
    @Autowired private XlSemesterPlanMapper planMapper;
    @Autowired private XlSemesterPlanDestMapper planDestMapper;
    @Autowired private XlIdleItemMapper idleItemMapper;
    @Autowired private XlDestinationMapper destinationMapper;
    @Autowired private XlTravelUserMapper travelUserMapper;

    private static Date parseDate(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            if (s.length() <= 10) return new SimpleDateFormat("yyyy-MM-dd").parse(s);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
        } catch (Exception e) { return null; }
    }

    @Override
    public List<Map<String, Object>> listCountdowns(Long userId) {
        List<XlTripCountdown> list = countdownMapper.selectByUserId(userId);
        if (list == null) return Collections.emptyList();
        List<Map<String, Object>> result = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (XlTripCountdown c : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("title", c.getTitle());
            m.put("destinationId", c.getDestinationId());
            m.put("tripDate", c.getTripDate());
            m.put("remindDays", c.getRemindDays());
            m.put("memo", c.getMemo());
            if (c.getTripDate() != null) {
                long t = c.getTripDate().getTime();
                long days = (t - now) / (24 * 3600 * 1000);
                m.put("daysLeft", (int) days);
            }
            if (c.getDestinationId() != null) {
                XlDestination d = destinationMapper.selectById(c.getDestinationId());
                m.put("destinationName", d != null ? d.getName() : null);
            }
            result.add(m);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addCountdown(Long userId, String title, Long destinationId, String tripDate, Integer remindDays, String memo) {
        Date d = parseDate(tripDate);
        if (d == null || title == null || title.isEmpty()) return 0;
        XlTripCountdown c = new XlTripCountdown();
        c.setUserId(userId);
        c.setTitle(title.trim());
        c.setDestinationId(destinationId);
        c.setTripDate(d);
        c.setRemindDays(remindDays != null ? remindDays : 0);
        c.setMemo(memo != null ? memo.trim() : "");
        countdownMapper.insert(c);
        return c.getId();
    }

    @Override
    public boolean updateCountdown(Long userId, Long id, String title, Long destinationId, String tripDate, Integer remindDays, String memo) {
        XlTripCountdown c = countdownMapper.selectById(id);
        if (c == null || !c.getUserId().equals(userId)) return false;
        Date d = parseDate(tripDate);
        if (d != null) c.setTripDate(d);
        if (title != null) c.setTitle(title.trim());
        c.setDestinationId(destinationId);
        c.setRemindDays(remindDays != null ? remindDays : 0);
        c.setMemo(memo != null ? memo.trim() : "");
        return countdownMapper.update(c) > 0;
    }

    @Override
    public boolean deleteCountdown(Long userId, Long id) {
        return countdownMapper.deleteById(id, userId) > 0;
    }

    @Override
    public List<Map<String, Object>> listPlans(Long userId) {
        List<XlSemesterPlan> list = planMapper.selectByUserId(userId);
        if (list == null) return Collections.emptyList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (XlSemesterPlan p : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("name", p.getName());
            m.put("startDate", p.getStartDate());
            m.put("endDate", p.getEndDate());
            m.put("memo", p.getMemo());
            List<Long> destIds = planDestMapper.selectDestinationIdsByPlanId(p.getId());
            m.put("destinationIds", destIds != null ? destIds : Collections.emptyList());
            if (destIds != null && !destIds.isEmpty()) {
                List<String> names = new ArrayList<>();
                for (Long did : destIds) {
                    XlDestination d = destinationMapper.selectById(did);
                    if (d != null) names.add(d.getName());
                }
                m.put("destinationNames", names);
            }
            result.add(m);
        }
        return result;
    }

    @Override
    public Map<String, Object> getPlan(Long planId, Long userId) {
        XlSemesterPlan p = planMapper.selectById(planId);
        if (p == null || !p.getUserId().equals(userId)) return null;
        Map<String, Object> m = new HashMap<>();
        m.put("id", p.getId());
        m.put("name", p.getName());
        m.put("startDate", p.getStartDate());
        m.put("endDate", p.getEndDate());
        m.put("memo", p.getMemo());
        List<Long> destIds = planDestMapper.selectDestinationIdsByPlanId(p.getId());
        m.put("destinationIds", destIds != null ? destIds : Collections.emptyList());
        return m;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addPlan(Long userId, String name, String startDate, String endDate, String memo, List<Long> destinationIds) {
        Date start = parseDate(startDate);
        Date end = parseDate(endDate);
        if (start == null || end == null || name == null || name.isEmpty()) return 0;
        XlSemesterPlan p = new XlSemesterPlan();
        p.setUserId(userId);
        p.setName(name.trim());
        p.setStartDate(start);
        p.setEndDate(end);
        p.setMemo(memo != null ? memo.trim() : "");
        planMapper.insert(p);
        long planId = p.getId();
        if (destinationIds != null && !destinationIds.isEmpty()) {
            int so = 0;
            for (Long destId : destinationIds) {
                planDestMapper.insert(planId, destId, so++);
            }
        }
        return planId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePlan(Long userId, Long id, String name, String startDate, String endDate, String memo, List<Long> destinationIds) {
        XlSemesterPlan p = planMapper.selectById(id);
        if (p == null || !p.getUserId().equals(userId)) return false;
        Date start = parseDate(startDate);
        Date end = parseDate(endDate);
        if (start != null) p.setStartDate(start);
        if (end != null) p.setEndDate(end);
        if (name != null) p.setName(name.trim());
        p.setMemo(memo != null ? memo.trim() : "");
        planMapper.update(p);
        planDestMapper.deleteByPlanId(id);
        if (destinationIds != null && !destinationIds.isEmpty()) {
            int so = 0;
            for (Long destId : destinationIds) {
                planDestMapper.insert(id, destId, so++);
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePlan(Long userId, Long id) {
        planDestMapper.deleteByPlanId(id);
        return planMapper.deleteById(id, userId) > 0;
    }

    @Override
    public List<Map<String, Object>> listIdleOnSale(String category, Integer limit) {
        if (limit == null || limit <= 0) limit = 30;
        List<XlIdleItem> list = idleItemMapper.selectOnSale(category, limit);
        if (list == null) return Collections.emptyList();
        return buildIdleMaps(list, true);
    }

    @Override
    public Map<String, Object> getIdleDetail(Long id) {
        XlIdleItem item = idleItemMapper.selectById(id);
        if (item == null) return null;
        List<XlIdleItem> one = Collections.singletonList(item);
        List<Map<String, Object>> maps = buildIdleMaps(one, true);
        return maps.isEmpty() ? null : maps.get(0);
    }

    @Override
    public List<Map<String, Object>> listMyIdle(Long userId) {
        List<XlIdleItem> list = idleItemMapper.selectByUserId(userId);
        if (list == null) return Collections.emptyList();
        return buildIdleMaps(list, false);
    }

    private List<Map<String, Object>> buildIdleMaps(List<XlIdleItem> list, boolean withSeller) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (XlIdleItem item : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", item.getId());
            m.put("title", item.getTitle());
            m.put("category", item.getCategory());
            m.put("description", item.getDescription());
            m.put("priceStars", item.getPriceStars());
            m.put("images", item.getImages());
            m.put("status", item.getStatus());
            m.put("statusText", "0".equals(item.getStatus()) ? "在售" : "1".equals(item.getStatus()) ? "已预订" : "已售出");
            m.put("createTime", item.getCreateTime());
            if (withSeller) {
                XlTravelUser u = travelUserMapper.selectById(item.getUserId());
                m.put("sellerId", item.getUserId());
                m.put("sellerNickname", u != null && u.getNickname() != null && !u.getNickname().isEmpty() ? u.getNickname() : "用户");
            }
            result.add(m);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long publishIdle(Long userId, String title, String category, String description, Integer priceStars, String images) {
        if (title == null || title.isEmpty() || priceStars == null || priceStars < 0) return 0;
        XlIdleItem item = new XlIdleItem();
        item.setUserId(userId);
        item.setTitle(title.trim());
        item.setCategory(category != null && !category.isEmpty() ? category : "other");
        item.setDescription(description != null ? description.trim() : "");
        item.setPriceStars(priceStars);
        item.setImages(images != null ? images.trim() : "");
        idleItemMapper.insert(item);
        return item.getId();
    }

    @Override
    public boolean updateIdle(Long userId, Long id, String title, String category, String description, Integer priceStars, String images) {
        XlIdleItem item = idleItemMapper.selectById(id);
        if (item == null || !item.getUserId().equals(userId) || !"0".equals(item.getStatus())) return false;
        if (title != null) item.setTitle(title.trim());
        if (category != null) item.setCategory(category);
        if (description != null) item.setDescription(description.trim());
        if (priceStars != null) item.setPriceStars(priceStars);
        if (images != null) item.setImages(images.trim());
        return idleItemMapper.update(item) > 0;
    }

    @Override
    public boolean deleteIdle(Long userId, Long id) {
        return idleItemMapper.deleteByIdAndUser(id, userId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> buyIdle(Long buyerId, Long idleId) {
        Map<String, Object> res = new HashMap<>();
        XlIdleItem item = idleItemMapper.selectById(idleId);
        if (item == null) { res.put("error", "商品不存在"); return res; }
        if (!"0".equals(item.getStatus())) { res.put("error", "已售出或已下架"); return res; }
        if (item.getUserId().equals(buyerId)) { res.put("error", "不能购买自己的闲置"); return res; }
        int price = item.getPriceStars() != null ? item.getPriceStars() : 0;
        if (price <= 0) { res.put("error", "价格异常"); return res; }
        XlTravelUser buyer = travelUserMapper.selectById(buyerId);
        if (buyer == null || buyer.getStars() == null || buyer.getStars() < price) {
            res.put("error", "星星不足");
            return res;
        }
        travelUserMapper.updateStars(buyerId, -price);
        travelUserMapper.updateStars(item.getUserId(), price);
        idleItemMapper.updateStatusAndBuyer(idleId, "2", buyerId);
        res.put("success", true);
        res.put("itemId", idleId);
        return res;
    }
}
