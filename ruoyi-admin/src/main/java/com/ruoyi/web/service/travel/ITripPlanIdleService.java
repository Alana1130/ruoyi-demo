package com.ruoyi.web.service.travel;

import java.util.List;
import java.util.Map;

/** 出行倒计时、学期计划、二手闲置 */
public interface ITripPlanIdleService {

    // ---- 出行倒计时 ----
    List<Map<String, Object>> listCountdowns(Long userId);
    long addCountdown(Long userId, String title, Long destinationId, String tripDate, Integer remindDays, String memo);
    boolean updateCountdown(Long userId, Long id, String title, Long destinationId, String tripDate, Integer remindDays, String memo);
    boolean deleteCountdown(Long userId, Long id);

    // ---- 学期/假期计划 ----
    List<Map<String, Object>> listPlans(Long userId);
    Map<String, Object> getPlan(Long planId, Long userId);
    long addPlan(Long userId, String name, String startDate, String endDate, String memo, List<Long> destinationIds);
    boolean updatePlan(Long userId, Long id, String name, String startDate, String endDate, String memo, List<Long> destinationIds);
    boolean deletePlan(Long userId, Long id);

    // ---- 二手闲置 ----
    List<Map<String, Object>> listIdleOnSale(String category, Integer limit);
    Map<String, Object> getIdleDetail(Long id);
    List<Map<String, Object>> listMyIdle(Long userId);
    long publishIdle(Long userId, String title, String category, String description, Integer priceStars, String images);
    boolean updateIdle(Long userId, Long id, String title, String category, String description, Integer priceStars, String images);
    boolean deleteIdle(Long userId, Long id);
    Map<String, Object> buyIdle(Long buyerId, Long idleId);
}
