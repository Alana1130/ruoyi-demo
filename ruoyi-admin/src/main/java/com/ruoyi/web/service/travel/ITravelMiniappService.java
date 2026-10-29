package com.ruoyi.web.service.travel;

import com.ruoyi.web.domain.travel.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 旅游小程序 服务层
 */
public interface ITravelMiniappService {

    List<XlBanner> listBanners();

    List<XlDestination> listDestinations();

    Map<String, Object> getDestinationDetail(Long id);

    List<XlRoute> listRoutesByDestination(Long destinationId);

    Map<String, Object> getRouteDetail(Long routeId);

    List<Map<String, Object>> budgetPlan(Integer maxBudget, Integer days, Long destinationId);

    XlTravelUser getOrCreateUser(String openidOrUserId);

    /** 注册：手机号+密码，返回用户（手机号已存在返回 null） */
    XlTravelUser register(String phone, String password, String nickname);

    /** 手机号+密码登录，返回用户（失败返回 null） */
    XlTravelUser loginByPhone(String phone, String password);

    /** 根据 id 获取用户（我的页面） */
    XlTravelUser getUserById(Long userId);

    /** 更新用户资料（昵称、头像） */
    boolean updateProfile(Long userId, String nickname, String avatar);

    /** 修改密码（仅已注册手机号账号可用）：校验旧密码后更新 */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /** 绑定手机号并设置密码（仅当前无手机号的用户可用，绑定后可用手机号+密码登录） */
    boolean bindPhone(Long userId, String phone, String password);

    /** 提交出行体验（可选 title、tags 用于攻略） */
    long submitExperience(Long userId, String targetType, Long targetId, String content);
    long submitExperience(Long userId, String targetType, Long targetId, String content, String title, String tags);

    /** 某景点/美食的已核验体验列表 */
    List<XlUserExperience> listExperiences(String targetType, Long targetId);

    /** AI 核验单条体验（可接入大模型，此处模拟） */
    boolean verifyExperience(Long experienceId);

    Map<String, Object> lightDestination(Long userId, Long destinationId);

    List<XlUserCheckin> myCheckins(Long userId);

    List<XlRewardItem> listRewardItems(String type);

    /** 商品详情（按 id 查询） */
    XlRewardItem getRewardItemById(Long id);

    Map<String, Object> exchange(Long userId, Long rewardItemId);

    /** 直接购买，可选传收货人信息 */
    Map<String, Object> purchase(Long userId, Long rewardItemId, String receiverName, String receiverPhone, String receiverAddress);

    List<XlUserExchange> myExchanges(Long userId);

    /** 我的兑换+购买记录（合并列表，按时间倒序） */
    List<Map<String, Object>> myRecords(Long userId);

    List<XlTaxi> listTaxis(Long destinationId);

    List<XlTicket> listTickets(Long destinationId);

    /** 收货地址列表 */
    List<XlUserAddress> listAddresses(Long userId);

    /** 新增收货地址 */
    long addAddress(Long userId, String receiverName, String receiverPhone, String receiverAddress, Integer isDefault);

    /** 修改收货地址 */
    boolean updateAddress(Long userId, Long id, String receiverName, String receiverPhone, String receiverAddress, Integer isDefault);

    /** 删除收货地址 */
    boolean deleteAddress(Long userId, Long id);

    /** 设为默认地址 */
    boolean setDefaultAddress(Long userId, Long id);

    /** 实践机会列表：destinationId 可选，type 可选（part_time/volunteer） */
    List<XlOpportunity> listOpportunities(Long destinationId, String type);

    /** 实践机会详情 */
    XlOpportunity getOpportunityById(Long id);

    /** 讨论留言列表 */
    List<XlChatMessage> listChatMessages(String topicType, Long topicId);

    /** 发表讨论留言，parentId 为回复的留言 id，可选 */
    long postChatMessage(Long userId, String topicType, Long topicId, String content, Long parentId);

    /** 通知列表 */
    List<XlNotification> listNotifications(Long userId);

    /** 未读数量 */
    int getNotificationUnreadCount(Long userId);

    /** 标记已读 */
    boolean markNotificationRead(Long userId, Long id);

    /** 全部已读 */
    boolean markAllNotificationsRead(Long userId);

    /** 领取演示消息（当前无消息时插入几条示例） */
    List<XlNotification> seedDemoNotifications(Long userId);

    /** 全局搜索：目的地 + 实践机会 */
    Map<String, Object> search(String keyword);

    /** 约伴：列表（可选目的地、关键词、日期范围） */
    List<Map<String, Object>> listCompanions(Long destinationId, String keyword, java.util.Date dateFrom, java.util.Date dateTo);

    /** 约伴：详情（含发布者昵称、目的地名） */
    Map<String, Object> getCompanionById(Long id);

    /** 约伴：发布 */
    long createCompanion(Long userId, Long destinationId, String title, String content, java.util.Date travelDateStart, java.util.Date travelDateEnd, String budgetRange, String contactRemark);

    /** 约伴：更新（仅本人） */
    boolean updateCompanion(Long userId, Long id, String title, String content, java.util.Date travelDateStart, java.util.Date travelDateEnd, String budgetRange, String contactRemark, String status);

    /** 约伴：删除（仅本人） */
    boolean deleteCompanion(Long userId, Long id);

    /** 攻略/游记：列表（destinationId、tag 可选），仅 targetType=destination 且已核验 */
    List<Map<String, Object>> listGuides(Long destinationId, String tag);

    /** 攻略/游记：单条详情（含昵称、目的地名），公开 */
    Map<String, Object> getExperienceById(Long experienceId);

}
