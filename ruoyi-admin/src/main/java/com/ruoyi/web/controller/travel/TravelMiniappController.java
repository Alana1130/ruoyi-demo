package com.ruoyi.web.controller.travel;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.web.domain.travel.*;
import com.ruoyi.web.service.travel.ITravelMiniappService;
import com.ruoyi.web.service.travel.ISmallWorldService;
import com.ruoyi.web.service.travel.ITripPlanIdleService;
import com.ruoyi.web.service.travel.IInteractionService;
import com.ruoyi.web.service.travel.ITravelTokenService;
import com.ruoyi.web.service.travel.ISchoolCircleService;
import com.ruoyi.web.service.travel.ITravelAaService;
import java.math.BigDecimal;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 旅游小程序 API（匿名访问）
 */
@Anonymous
@RestController
@RequestMapping("/travel/miniapp")
public class TravelMiniappController extends BaseController {

    @Autowired
    private ITravelMiniappService travelMiniappService;
    @Autowired
    private ISmallWorldService smallWorldService;
    @Autowired
    private ITripPlanIdleService tripPlanIdleService;
    @Autowired
    private IInteractionService interactionService;
    @Autowired
    private ITravelTokenService travelTokenService;
    @Autowired
    private ISchoolCircleService schoolCircleService;

    /** 注册：手机号+密码+昵称，返回 token 与用户信息 */
    @Anonymous
    @PostMapping("/auth/register")
    public AjaxResult register(@RequestBody Map<String, Object> body) {
        String phone = body != null && body.get("phone") != null ? body.get("phone").toString().trim() : null;
        String password = body != null && body.get("password") != null ? body.get("password").toString() : null;
        String nickname = body != null && body.get("nickname") != null ? body.get("nickname").toString().trim() : null;
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) return error("请填写手机号和密码");
        XlTravelUser user = travelMiniappService.register(phone, password, nickname);
        if (user == null) return error("该手机号已注册");
        String token = travelTokenService.createToken(user.getId(), 7);
        Map<String, Object> userMap = buildUserMap(user);
        Map<String, Object> res = new HashMap<>();
        res.put("token", token);
        res.put("user", userMap);
        return success(res);
    }

    /** 登录：body 可为 { phone, password } 或 { openid }，不传则游客 */
    @Anonymous
    @PostMapping("/auth/login")
    public AjaxResult login(@RequestBody(required = false) Map<String, Object> body) {
        if (body != null && body.get("phone") != null && body.get("password") != null) {
            String phone = body.get("phone").toString().trim();
            String password = body.get("password").toString();
            XlTravelUser user = travelMiniappService.loginByPhone(phone, password);
            if (user == null) return error("手机号或密码错误");
            String token = travelTokenService.createToken(user.getId(), 7);
            Map<String, Object> res = new HashMap<>();
            res.put("token", token);
            res.put("user", buildUserMap(user));
            return success(res);
        }
        String openid = null;
        if (body != null && body.get("openid") != null) openid = body.get("openid").toString().trim();
        if (StringUtils.isEmpty(openid)) openid = "guest_" + System.currentTimeMillis();
        XlTravelUser user = travelMiniappService.getOrCreateUser(openid);
        String token = travelTokenService.createToken(user.getId(), 7);
        Map<String, Object> res = new HashMap<>();
        res.put("token", token);
        res.put("user", buildUserMap(user));
        return success(res);
    }

    private Map<String, Object> buildUserMap(XlTravelUser user) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", user.getId());
        m.put("nickname", user.getNickname() != null && !user.getNickname().isEmpty() ? user.getNickname() : "旅行者");
        m.put("avatar", user.getAvatar());
        m.put("stars", user.getStars() != null ? user.getStars() : 0);
        m.put("schoolVerified", user.getSchoolVerified() != null ? user.getSchoolVerified() : 0);
        m.put("schoolId", user.getSchoolId());
        String phone = user.getPhone();
        m.put("phone", (phone != null && phone.length() >= 11) ? phone.substring(0, 3) + "****" + phone.substring(7) : phone);
        return m;
    }

    /** 登出：使当前 token 失效 */
    @PostMapping("/auth/logout")
    public AjaxResult logout() {
        String token = getTravelToken();
        if (token != null) travelTokenService.removeToken(token);
        return success();
    }

    private String getTravelToken() {
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) return null;
        String auth = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(auth) && auth.startsWith(Constants.TOKEN_PREFIX))
            return auth.substring(Constants.TOKEN_PREFIX.length()).trim();
        return null;
    }

    /** 轮播图列表 */
    @GetMapping("/banners")
    public AjaxResult listBanners() {
        return success(travelMiniappService.listBanners());
    }

    /** 我的 - 当前用户信息（含学校认证状态） */
    @GetMapping("/user/me")
    public AjaxResult userMe() {
        Long userId = getTravelUserId();
        XlTravelUser user = travelMiniappService.getUserById(userId);
        if (user == null) return error("用户不存在");
        Map<String, Object> m = buildUserMap(user);
        Map<String, Object> school = schoolCircleService.getMySchool(userId);
        m.put("schoolName", school.get("schoolName"));
        return success(m);
    }

    /** 学校列表（用于选择认证与学校圈） */
    @GetMapping("/schools")
    public AjaxResult listSchools() {
        return success(schoolCircleService.listSchools());
    }

    /** 学校认证：当前用户认证为某学校（可选功能，认证后可在该学校圈发帖） */
    @PostMapping("/user/school-verify")
    public AjaxResult schoolVerify(@RequestBody Map<String, Object> body) {
        Long schoolId = body != null && body.get("schoolId") != null ? Long.valueOf(body.get("schoolId").toString()) : null;
        if (schoolId == null) return error("请选择学校");
        boolean ok = schoolCircleService.verifySchool(getTravelUserId(), schoolId);
        return ok ? success() : error("认证失败");
    }

    /** 当前用户学校认证信息 */
    @GetMapping("/user/my-school")
    public AjaxResult mySchool() {
        return success(schoolCircleService.getMySchool(getTravelUserId()));
    }

    /** 学校圈帖子列表 */
    @GetMapping("/school-circle/posts")
    public AjaxResult schoolCirclePosts(@RequestParam Long schoolId, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "20") Integer pageSize) {
        return success(schoolCircleService.listPosts(schoolId, page != null ? page : 0, pageSize != null ? pageSize : 20));
    }

    /** 学校圈发帖（仅认证用户且学校一致可发） */
    @PostMapping("/school-circle/post")
    public AjaxResult schoolCirclePost(@RequestBody Map<String, Object> body) {
        Long schoolId = body != null && body.get("schoolId") != null ? Long.valueOf(body.get("schoolId").toString()) : null;
        String content = body != null && body.get("content") != null ? body.get("content").toString() : null;
        String images = body != null && body.get("images") != null ? body.get("images").toString() : null;
        if (schoolId == null || StringUtils.isEmpty(content)) return error("请选择学校并填写内容");
        long id = schoolCircleService.addPost(getTravelUserId(), schoolId, content, images);
        if (id <= 0) return error("仅认证该校的用户可发帖，请先完成学校认证");
        return success(Collections.singletonMap("id", id));
    }

    /** 更新用户资料（昵称、头像） */
    @PutMapping("/user/profile")
    public AjaxResult updateProfile(@RequestBody Map<String, Object> body) {
        Long userId = getTravelUserId();
        String nickname = body.get("nickname") != null ? body.get("nickname").toString().trim() : null;
        String avatar = body.get("avatar") != null ? body.get("avatar").toString().trim() : null;
        boolean ok = travelMiniappService.updateProfile(userId, nickname, avatar);
        return ok ? success() : error("更新失败");
    }

    /** 修改密码（旧密码、新密码） */
    @PutMapping("/user/change-password")
    public AjaxResult changePassword(@RequestBody Map<String, Object> body) {
        String oldPassword = body != null && body.get("oldPassword") != null ? body.get("oldPassword").toString() : null;
        String newPassword = body != null && body.get("newPassword") != null ? body.get("newPassword").toString() : null;
        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword)) return error("请填写原密码和新密码");
        boolean ok = travelMiniappService.changePassword(getTravelUserId(), oldPassword, newPassword);
        return ok ? success() : error("原密码错误或未注册账号");
    }

    /** 绑定手机号并设置密码（仅当前无手机号的用户可用，绑定后可用手机号+密码登录） */
    @PostMapping("/user/bind-phone")
    public AjaxResult bindPhone(@RequestBody Map<String, Object> body) {
        String phone = body != null && body.get("phone") != null ? body.get("phone").toString().trim() : null;
        String password = body != null && body.get("password") != null ? body.get("password").toString() : null;
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) return error("请填写手机号和密码");
        boolean ok = travelMiniappService.bindPhone(getTravelUserId(), phone, password);
        return ok ? success() : error("该手机号已被注册或当前账号已绑定手机");
    }

    /** 提交出行体验（待 AI 核验后展示），可选 title、tags 用于攻略 */
    @PostMapping("/experiences")
    public AjaxResult submitExperience(@RequestBody Map<String, Object> body) {
        Long userId = getTravelUserId();
        String targetType = body.get("targetType") != null ? body.get("targetType").toString() : null;
        Long targetId = body.get("targetId") != null ? Long.valueOf(body.get("targetId").toString()) : null;
        String content = body.get("content") != null ? body.get("content").toString() : null;
        String title = body.get("title") != null ? body.get("title").toString().trim() : null;
        String tags = body.get("tags") != null ? body.get("tags").toString().trim() : null;
        if (targetType == null || targetId == null || content == null || content.isEmpty())
            return error("缺少 targetType / targetId / content");
        long id = travelMiniappService.submitExperience(userId, targetType, targetId, content, title, tags);
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("message", "提交成功，核验通过后将展示");
        return success(m);
    }

    /** 某景点/美食的已核验体验列表 */
    @GetMapping("/experiences/list")
    public AjaxResult listExperiences(@RequestParam String targetType, @RequestParam Long targetId) {
        return success(travelMiniappService.listExperiences(targetType, targetId));
    }

    /** AI 核验单条体验（管理/定时调用，可接入大模型） */
    @PostMapping("/ai/verify-experience")
    public AjaxResult verifyExperience(@RequestBody Map<String, Object> body) {
        Long experienceId = body.get("experienceId") != null ? Long.valueOf(body.get("experienceId").toString()) : null;
        if (experienceId == null) return error("缺少 experienceId");
        boolean ok = travelMiniappService.verifyExperience(experienceId);
        return ok ? success() : error("核验失败");
    }

    /** 解析小程序用户 id：优先从 Authorization Bearer token 取，否则回退 X-User-Id / getOrCreateUser */
    private Long getTravelUserId() {
        String token = getTravelToken();
        if (StringUtils.isNotEmpty(token)) {
            Long userId = travelTokenService.getUserIdByToken(token);
            if (userId != null) return userId;
        }
        HttpServletRequest request = ServletUtils.getRequest();
        String id = request != null ? request.getHeader("X-User-Id") : null;
        if (id == null || id.isEmpty()) id = request != null ? request.getParameter("userId") : null;
        XlTravelUser user = travelMiniappService.getOrCreateUser(id);
        return user.getId();
    }

    /** 目的地列表 */
    @GetMapping("/destinations")
    public AjaxResult listDestinations() {
        List<XlDestination> list = travelMiniappService.listDestinations();
        return success(list);
    }

    /** 目的地详情（含景点、美食、住宿、想去/去过统计） */
    @GetMapping("/destinations/{id}")
    public AjaxResult getDestination(@PathVariable Long id) {
        Map<String, Object> data = travelMiniappService.getDestinationDetail(id);
        if (data == null) return error("目的地不存在");
        data.putAll(interactionService.destinationStats(getTravelUserId(), id));
        return success(data);
    }

    /** 某目的地的推荐线路列表 */
    @GetMapping("/routes/by-destination/{destinationId}")
    public AjaxResult listRoutes(@PathVariable Long destinationId) {
        return success(travelMiniappService.listRoutesByDestination(destinationId));
    }

    /** 线路详情（含按天节点） */
    @GetMapping("/routes/{id}")
    public AjaxResult getRoute(@PathVariable Long id) {
        Map<String, Object> data = travelMiniappService.getRouteDetail(id);
        if (data == null) return error("线路不存在");
        return success(data);
    }

    /** 预算规划：maxBudget、days、destinationId(可选) */
    @GetMapping("/budget/plan")
    public AjaxResult budgetPlan(
            @RequestParam(defaultValue = "1000") Integer maxBudget,
            @RequestParam(defaultValue = "2") Integer days,
            @RequestParam(required = false) Long destinationId) {
        List<Map<String, Object>> list = travelMiniappService.budgetPlan(maxBudget, days, destinationId);
        return success(list);
    }

    /** 点亮目的地 */
    @PostMapping("/checkin/light")
    public AjaxResult light(@RequestBody Map<String, Object> body) {
        Long destinationId = body.get("destinationId") != null ? Long.valueOf(body.get("destinationId").toString()) : null;
        if (destinationId == null) return error("缺少 destinationId");
        Long userId = getTravelUserId();
        Map<String, Object> res = travelMiniappService.lightDestination(userId, destinationId);
        if (res.get("error") != null) return error(res.get("error").toString());
        return success(res);
    }

    /** 我的点亮列表 */
    @GetMapping("/checkin/my")
    public AjaxResult myCheckins() {
        Long userId = getTravelUserId();
        List<XlUserCheckin> list = travelMiniappService.myCheckins(userId);
        return success(list);
    }

    /** 打车/出行服务（按目的地） */
    @GetMapping("/taxi")
    public AjaxResult listTaxis(@RequestParam Long destinationId) {
        return success(travelMiniappService.listTaxis(destinationId));
    }

    /** 门票列表（按目的地） */
    @GetMapping("/tickets")
    public AjaxResult listTickets(@RequestParam Long destinationId) {
        return success(travelMiniappService.listTickets(destinationId));
    }

    /** 可兑换商品列表 type 可选：culture_tourism / public_welfare / self_operated */
    @GetMapping("/rewards/items")
    public AjaxResult rewardItems(@RequestParam(required = false) String type) {
        return success(travelMiniappService.listRewardItems(type));
    }

    /** 商品详情（按 id） */
    @GetMapping("/rewards/items/{id}")
    public AjaxResult rewardItemDetail(@PathVariable Long id) {
        Object item = travelMiniappService.getRewardItemById(id);
        if (item == null) return error("商品不存在");
        return success(item);
    }

    /** 直接购买（文旅/公益商品支持星星换或直接买），可传收货人信息 */
    @PostMapping("/rewards/purchase")
    public AjaxResult purchase(@RequestBody Map<String, Object> body) {
        Long rewardItemId = body.get("rewardItemId") != null ? Long.valueOf(body.get("rewardItemId").toString()) : null;
        if (rewardItemId == null) return error("缺少 rewardItemId");
        String receiverName = body.get("receiverName") != null ? body.get("receiverName").toString().trim() : null;
        String receiverPhone = body.get("receiverPhone") != null ? body.get("receiverPhone").toString().trim() : null;
        String receiverAddress = body.get("receiverAddress") != null ? body.get("receiverAddress").toString().trim() : null;
        Long userId = getTravelUserId();
        Map<String, Object> res = travelMiniappService.purchase(userId, rewardItemId, receiverName, receiverPhone, receiverAddress);
        if (res.get("error") != null) return error(res.get("error").toString());
        return success(res);
    }

    /** 星星兑换 */
    @PostMapping("/rewards/exchange")
    public AjaxResult exchange(@RequestBody Map<String, Object> body) {
        Long rewardItemId = body.get("rewardItemId") != null ? Long.valueOf(body.get("rewardItemId").toString()) : null;
        if (rewardItemId == null) return error("缺少 rewardItemId");
        Long userId = getTravelUserId();
        Map<String, Object> res = travelMiniappService.exchange(userId, rewardItemId);
        if (res.get("error") != null) return error(res.get("error").toString());
        return success(res);
    }

    /** 我的兑换记录 */
    @GetMapping("/rewards/my-exchanges")
    public AjaxResult myExchanges() {
        return success(travelMiniappService.myExchanges(getTravelUserId()));
    }

    /** 我的兑换+购买记录（合并） */
    @GetMapping("/rewards/my-records")
    public AjaxResult myRecords() {
        return success(travelMiniappService.myRecords(getTravelUserId()));
    }

    /** 收货地址列表 */
    @GetMapping("/addresses")
    public AjaxResult listAddresses() {
        return success(travelMiniappService.listAddresses(getTravelUserId()));
    }

    /** 新增收货地址 */
    @PostMapping("/addresses")
    public AjaxResult addAddress(@RequestBody Map<String, Object> body) {
        Long userId = getTravelUserId();
        String receiverName = body.get("receiverName") != null ? body.get("receiverName").toString().trim() : null;
        String receiverPhone = body.get("receiverPhone") != null ? body.get("receiverPhone").toString().trim() : null;
        String receiverAddress = body.get("receiverAddress") != null ? body.get("receiverAddress").toString().trim() : null;
        Integer isDefault = body.get("isDefault") != null ? Integer.valueOf(body.get("isDefault").toString()) : 0;
        if (receiverName == null || receiverPhone == null || receiverAddress == null)
            return error("请填写收货人、电话、地址");
        long id = travelMiniappService.addAddress(userId, receiverName, receiverPhone, receiverAddress, isDefault);
        return success(id > 0 ? java.util.Collections.singletonMap("id", id) : null);
    }

    /** 修改收货地址 */
    @PutMapping("/addresses/{id}")
    public AjaxResult updateAddress(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = getTravelUserId();
        String receiverName = body.get("receiverName") != null ? body.get("receiverName").toString().trim() : null;
        String receiverPhone = body.get("receiverPhone") != null ? body.get("receiverPhone").toString().trim() : null;
        String receiverAddress = body.get("receiverAddress") != null ? body.get("receiverAddress").toString().trim() : null;
        Integer isDefault = body.get("isDefault") != null ? Integer.valueOf(body.get("isDefault").toString()) : null;
        boolean ok = travelMiniappService.updateAddress(userId, id, receiverName, receiverPhone, receiverAddress, isDefault);
        return ok ? success() : error("修改失败");
    }

    /** 删除收货地址 */
    @DeleteMapping("/addresses/{id}")
    public AjaxResult deleteAddress(@PathVariable Long id) {
        boolean ok = travelMiniappService.deleteAddress(getTravelUserId(), id);
        return ok ? success() : error("删除失败");
    }

    /** 设为默认地址 */
    @PutMapping("/addresses/{id}/default")
    public AjaxResult setDefaultAddress(@PathVariable Long id) {
        boolean ok = travelMiniappService.setDefaultAddress(getTravelUserId(), id);
        return ok ? success() : error("设置失败");
    }

    /** 实践机会列表：destinationId、type(part_time/volunteer) 可选 */
    @GetMapping("/opportunities")
    public AjaxResult listOpportunities(
            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) String type) {
        return success(travelMiniappService.listOpportunities(destinationId, type));
    }

    /** 实践机会详情 */
    @GetMapping("/opportunities/{id}")
    public AjaxResult getOpportunity(@PathVariable Long id) {
        XlOpportunity o = travelMiniappService.getOpportunityById(id);
        if (o == null) return error("不存在");
        return success(o);
    }

    /** 讨论留言列表 topicType=destination|route, topicId=目的地id或线路id */
    @GetMapping("/chat/messages")
    public AjaxResult listChatMessages(
            @RequestParam String topicType,
            @RequestParam Long topicId) {
        return success(travelMiniappService.listChatMessages(topicType, topicId));
    }

    /** 发表讨论留言，可选 body.parentId 回复某条留言 */
    @PostMapping("/chat/messages")
    public AjaxResult postChatMessage(@RequestBody Map<String, Object> body) {
        String topicType = body.get("topicType") != null ? body.get("topicType").toString() : null;
        Long topicId = body.get("topicId") != null ? Long.valueOf(body.get("topicId").toString()) : null;
        String content = body.get("content") != null ? body.get("content").toString() : null;
        Long parentId = body.get("parentId") != null ? Long.valueOf(body.get("parentId").toString()) : null;
        if (topicType == null || topicId == null || content == null || content.trim().isEmpty())
            return error("缺少 topicType / topicId / content");
        long id = travelMiniappService.postChatMessage(getTravelUserId(), topicType, topicId, content.trim(), parentId);
        return success(Collections.singletonMap("id", id));
    }

    /** 通知列表 */
    @GetMapping("/notifications")
    public AjaxResult listNotifications() {
        return success(travelMiniappService.listNotifications(getTravelUserId()));
    }

    /** 未读数量 */
    @GetMapping("/notifications/unread-count")
    public AjaxResult unreadCount() {
        return success(Collections.singletonMap("count", travelMiniappService.getNotificationUnreadCount(getTravelUserId())));
    }

    /** 标记已读 */
    @PutMapping("/notifications/{id}/read")
    public AjaxResult markNotificationRead(@PathVariable Long id) {
        return travelMiniappService.markNotificationRead(getTravelUserId(), id) ? success() : error("失败");
    }

    /** 全部已读 */
    @PutMapping("/notifications/read-all")
    public AjaxResult markAllNotificationsRead() {
        return travelMiniappService.markAllNotificationsRead(getTravelUserId()) ? success() : error("失败");
    }

    /** 领取演示消息（无消息时插入示例） */
    @PostMapping("/notifications/seed-demo")
    public AjaxResult seedDemoNotifications() {
        return success(travelMiniappService.seedDemoNotifications(getTravelUserId()));
    }

    /** 全局搜索：keyword 搜目的地、实践机会 */
    @GetMapping("/search")
    public AjaxResult search(@RequestParam(required = false) String keyword) {
        return success(travelMiniappService.search(keyword));
    }

    /** 约伴：列表，destinationId/keyword/dateFrom/dateTo 可选 */
    @GetMapping("/companions")
    public AjaxResult listCompanions(
            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        Date from = parseDate(dateFrom);
        Date to = parseDate(dateTo);
        return success(travelMiniappService.listCompanions(destinationId, keyword, from, to));
    }

    /** 约伴：详情 */
    @GetMapping("/companions/{id}")
    public AjaxResult getCompanion(@PathVariable Long id) {
        Map<String, Object> data = travelMiniappService.getCompanionById(id);
        if (data == null) return error("帖子不存在或已删除");
        return success(data);
    }

    /** 约伴：发布 */
    @PostMapping("/companions")
    public AjaxResult createCompanion(@RequestBody Map<String, Object> body) {
        Long userId = getTravelUserId();
        String title = body.get("title") != null ? body.get("title").toString().trim() : null;
        String content = body.get("content") != null ? body.get("content").toString().trim() : "";
        Long destinationId = body.get("destinationId") != null ? Long.valueOf(body.get("destinationId").toString()) : null;
        String travelDateStart = body.get("travelDateStart") != null ? body.get("travelDateStart").toString() : null;
        String travelDateEnd = body.get("travelDateEnd") != null ? body.get("travelDateEnd").toString() : null;
        String budgetRange = body.get("budgetRange") != null ? body.get("budgetRange").toString().trim() : "";
        String contactRemark = body.get("contactRemark") != null ? body.get("contactRemark").toString().trim() : "";
        if (title == null || title.isEmpty()) return error("请填写标题");
        Date start = parseDate(travelDateStart);
        Date end = parseDate(travelDateEnd);
        long id = travelMiniappService.createCompanion(userId, destinationId, title, content, start, end, budgetRange, contactRemark);
        return success(Collections.singletonMap("id", id));
    }

    /** 约伴：更新（仅本人） */
    @PutMapping("/companions/{id}")
    public AjaxResult updateCompanion(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = getTravelUserId();
        String title = body.get("title") != null ? body.get("title").toString().trim() : null;
        String content = body.get("content") != null ? body.get("content").toString().trim() : null;
        String travelDateStart = body.get("travelDateStart") != null ? body.get("travelDateStart").toString() : null;
        String travelDateEnd = body.get("travelDateEnd") != null ? body.get("travelDateEnd").toString() : null;
        String budgetRange = body.get("budgetRange") != null ? body.get("budgetRange").toString().trim() : null;
        String contactRemark = body.get("contactRemark") != null ? body.get("contactRemark").toString().trim() : null;
        String status = body.get("status") != null ? body.get("status").toString() : null;
        boolean ok = travelMiniappService.updateCompanion(userId, id, title, content, parseDate(travelDateStart), parseDate(travelDateEnd), budgetRange, contactRemark, status);
        return ok ? success() : error("更新失败");
    }

    /** 约伴：删除（仅本人） */
    @DeleteMapping("/companions/{id}")
    public AjaxResult deleteCompanion(@PathVariable Long id) {
        boolean ok = travelMiniappService.deleteCompanion(getTravelUserId(), id);
        return ok ? success() : error("删除失败");
    }

    /** 攻略/游记列表：destinationId、tag 可选 */
    @GetMapping("/guides")
    public AjaxResult listGuides(
            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) String tag) {
        return success(travelMiniappService.listGuides(destinationId, tag));
    }

    /** 攻略/体验单条详情（公开） */
    @GetMapping("/experiences/{id}")
    public AjaxResult getExperience(@PathVariable Long id) {
        Map<String, Object> data = travelMiniappService.getExperienceById(id);
        if (data == null) return error("不存在");
        return success(data);
    }

    /** 小世界概览 */
    @GetMapping("/small-world/overview")
    public AjaxResult smallWorldOverview() {
        return success(smallWorldService.getWorldOverview(getTravelUserId()));
    }

    /** 领取演示数据：为当前用户随机发放若干宠物与经营权（便于无数据时体验） */
    @PostMapping("/small-world/seed-demo")
    public AjaxResult seedDemo() {
        return success(smallWorldService.seedDemoData(getTravelUserId()));
    }

    /** 我的宠物 */
    @GetMapping("/small-world/pets")
    public AjaxResult myPets() {
        return success(smallWorldService.listMyPets(getTravelUserId()));
    }

    /** 我的美食经营权 */
    @GetMapping("/small-world/licenses")
    public AjaxResult myLicenses() {
        return success(smallWorldService.listMyLicenses(getTravelUserId()));
    }

    /** 喂食宠物 */
    @PostMapping("/small-world/feed-pet")
    public AjaxResult feedPet(@RequestBody Map<String, Object> body) {
        Long userPetId = body.get("userPetId") != null ? Long.valueOf(body.get("userPetId").toString()) : null;
        if (userPetId == null) return error("缺少 userPetId");
        Map<String, Object> res = smallWorldService.feedPet(getTravelUserId(), userPetId);
        if (res.get("error") != null) return error(res.get("error").toString());
        return success(res);
    }

    /** 互动宠物 */
    @PostMapping("/small-world/play-pet")
    public AjaxResult playPet(@RequestBody Map<String, Object> body) {
        Long userPetId = body.get("userPetId") != null ? Long.valueOf(body.get("userPetId").toString()) : null;
        if (userPetId == null) return error("缺少 userPetId");
        Map<String, Object> res = smallWorldService.playPet(getTravelUserId(), userPetId);
        if (res.get("error") != null) return error(res.get("error").toString());
        return success(res);
    }

    /** 修改宠物昵称 */
    @PutMapping("/small-world/pet-nickname")
    public AjaxResult updatePetNickname(@RequestBody Map<String, Object> body) {
        Long userPetId = body.get("userPetId") != null ? Long.valueOf(body.get("userPetId").toString()) : null;
        String nickname = body.get("nickname") != null ? body.get("nickname").toString().trim() : "";
        if (userPetId == null) return error("缺少 userPetId");
        return smallWorldService.updatePetNickname(getTravelUserId(), userPetId, nickname) ? success() : error("失败");
    }

    /** 交易市场挂单列表 itemType 可选 pet/food_license */
    @GetMapping("/small-world/trade-listings")
    public AjaxResult tradeListings(@RequestParam(required = false) String itemType, @RequestParam(defaultValue = "20") Integer limit) {
        return success(smallWorldService.listTradeListings(itemType, limit, getTravelUserId()));
    }

    /** 我的挂单（可下架） */
    @GetMapping("/small-world/my-listings")
    public AjaxResult myListings() {
        return success(smallWorldService.listMyListings(getTravelUserId()));
    }

    /** 上架 */
    @PostMapping("/small-world/list")
    public AjaxResult createListing(@RequestBody Map<String, Object> body) {
        String itemType = body.get("itemType") != null ? body.get("itemType").toString() : null;
        Long itemId = body.get("itemId") != null ? Long.valueOf(body.get("itemId").toString()) : null;
        Integer starsAmount = body.get("starsAmount") != null ? Integer.valueOf(body.get("starsAmount").toString()) : null;
        if (itemType == null || itemId == null) return error("缺少 itemType / itemId");
        Map<String, Object> res = smallWorldService.createListing(getTravelUserId(), itemType, itemId, starsAmount);
        if (res.get("error") != null) return error(res.get("error").toString());
        return success(res);
    }

    /** 购买 */
    @PostMapping("/small-world/buy")
    public AjaxResult buyListing(@RequestBody Map<String, Object> body) {
        Long tradeId = body.get("tradeId") != null ? Long.valueOf(body.get("tradeId").toString()) : null;
        if (tradeId == null) return error("缺少 tradeId");
        Map<String, Object> res = smallWorldService.buyListing(getTravelUserId(), tradeId);
        if (res.get("error") != null) return error(res.get("error").toString());
        return success(res);
    }

    /** 取消挂单 */
    @PostMapping("/small-world/cancel-listing")
    public AjaxResult cancelListing(@RequestBody Map<String, Object> body) {
        Long tradeId = body.get("tradeId") != null ? Long.valueOf(body.get("tradeId").toString()) : null;
        if (tradeId == null) return error("缺少 tradeId");
        return smallWorldService.cancelListing(getTravelUserId(), tradeId) ? success() : error("取消失败");
    }

    // ---------- 出行倒计时 ----------
    @GetMapping("/countdowns")
    public AjaxResult listCountdowns() {
        return success(tripPlanIdleService.listCountdowns(getTravelUserId()));
    }
    @PostMapping("/countdowns")
    public AjaxResult addCountdown(@RequestBody Map<String, Object> body) {
        String title = body.get("title") != null ? body.get("title").toString().trim() : null;
        Long destinationId = body.get("destinationId") != null ? Long.valueOf(body.get("destinationId").toString()) : null;
        String tripDate = body.get("tripDate") != null ? body.get("tripDate").toString() : null;
        Integer remindDays = body.get("remindDays") != null ? Integer.valueOf(body.get("remindDays").toString()) : 0;
        String memo = body.get("memo") != null ? body.get("memo").toString().trim() : "";
        long id = tripPlanIdleService.addCountdown(getTravelUserId(), title, destinationId, tripDate, remindDays, memo);
        return id > 0 ? success(Collections.singletonMap("id", id)) : error("参数错误");
    }
    @PutMapping("/countdowns/{id}")
    public AjaxResult updateCountdown(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String title = body.get("title") != null ? body.get("title").toString().trim() : null;
        Long destinationId = body.get("destinationId") != null ? Long.valueOf(body.get("destinationId").toString()) : null;
        String tripDate = body.get("tripDate") != null ? body.get("tripDate").toString() : null;
        Integer remindDays = body.get("remindDays") != null ? Integer.valueOf(body.get("remindDays").toString()) : 0;
        String memo = body.get("memo") != null ? body.get("memo").toString().trim() : "";
        return tripPlanIdleService.updateCountdown(getTravelUserId(), id, title, destinationId, tripDate, remindDays, memo) ? success() : error("更新失败");
    }
    @DeleteMapping("/countdowns/{id}")
    public AjaxResult deleteCountdown(@PathVariable Long id) {
        return tripPlanIdleService.deleteCountdown(getTravelUserId(), id) ? success() : error("删除失败");
    }

    // ---------- 学期/假期计划 ----------
    @GetMapping("/plans")
    public AjaxResult listPlans() {
        return success(tripPlanIdleService.listPlans(getTravelUserId()));
    }
    @GetMapping("/plans/{id}")
    public AjaxResult getPlan(@PathVariable Long id) {
        Map<String, Object> m = tripPlanIdleService.getPlan(id, getTravelUserId());
        return m != null ? success(m) : error("不存在");
    }
    @PostMapping("/plans")
    public AjaxResult addPlan(@RequestBody Map<String, Object> body) {
        String name = body.get("name") != null ? body.get("name").toString().trim() : null;
        String startDate = body.get("startDate") != null ? body.get("startDate").toString() : null;
        String endDate = body.get("endDate") != null ? body.get("endDate").toString() : null;
        String memo = body.get("memo") != null ? body.get("memo").toString().trim() : "";
        List<Long> destinationIds = null;
        if (body.get("destinationIds") instanceof List) {
            destinationIds = new java.util.ArrayList<>();
            for (Object o : (List<?>) body.get("destinationIds")) {
                if (o instanceof Number) destinationIds.add(((Number) o).longValue());
            }
        }
        long id = tripPlanIdleService.addPlan(getTravelUserId(), name, startDate, endDate, memo, destinationIds);
        return id > 0 ? success(Collections.singletonMap("id", id)) : error("参数错误");
    }
    @PutMapping("/plans/{id}")
    public AjaxResult updatePlan(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String name = body.get("name") != null ? body.get("name").toString().trim() : null;
        String startDate = body.get("startDate") != null ? body.get("startDate").toString() : null;
        String endDate = body.get("endDate") != null ? body.get("endDate").toString() : null;
        String memo = body.get("memo") != null ? body.get("memo").toString().trim() : "";
        List<Long> destinationIds = null;
        if (body.get("destinationIds") instanceof List) {
            destinationIds = new java.util.ArrayList<>();
            for (Object o : (List<?>) body.get("destinationIds")) {
                if (o instanceof Number) destinationIds.add(((Number) o).longValue());
            }
        }
        return tripPlanIdleService.updatePlan(getTravelUserId(), id, name, startDate, endDate, memo, destinationIds) ? success() : error("更新失败");
    }
    @DeleteMapping("/plans/{id}")
    public AjaxResult deletePlan(@PathVariable Long id) {
        return tripPlanIdleService.deletePlan(getTravelUserId(), id) ? success() : error("删除失败");
    }

    // ---------- 二手闲置 ----------
    @GetMapping("/idle")
    public AjaxResult listIdle(@RequestParam(required = false) String category, @RequestParam(defaultValue = "30") Integer limit) {
        return success(tripPlanIdleService.listIdleOnSale(category, limit));
    }
    @GetMapping("/idle/my")
    public AjaxResult listMyIdle() {
        return success(tripPlanIdleService.listMyIdle(getTravelUserId()));
    }
    @GetMapping("/idle/{id}")
    public AjaxResult getIdleDetail(@PathVariable Long id) {
        Map<String, Object> m = tripPlanIdleService.getIdleDetail(id);
        return m != null ? success(m) : error("不存在");
    }
    @PostMapping("/idle")
    public AjaxResult publishIdle(@RequestBody Map<String, Object> body) {
        String title = body.get("title") != null ? body.get("title").toString().trim() : null;
        String category = body.get("category") != null ? body.get("category").toString() : "other";
        String description = body.get("description") != null ? body.get("description").toString().trim() : "";
        Integer priceStars = body.get("priceStars") != null ? Integer.valueOf(body.get("priceStars").toString()) : null;
        String images = body.get("images") != null ? body.get("images").toString().trim() : "";
        long id = tripPlanIdleService.publishIdle(getTravelUserId(), title, category, description, priceStars, images);
        return id > 0 ? success(Collections.singletonMap("id", id)) : error("参数错误");
    }
    @PutMapping("/idle/{id}")
    public AjaxResult updateIdle(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String title = body.get("title") != null ? body.get("title").toString().trim() : null;
        String category = body.get("category") != null ? body.get("category").toString() : null;
        String description = body.get("description") != null ? body.get("description").toString().trim() : null;
        Integer priceStars = body.get("priceStars") != null ? Integer.valueOf(body.get("priceStars").toString()) : null;
        String images = body.get("images") != null ? body.get("images").toString().trim() : null;
        return tripPlanIdleService.updateIdle(getTravelUserId(), id, title, category, description, priceStars, images) ? success() : error("更新失败");
    }
    @DeleteMapping("/idle/{id}")
    public AjaxResult deleteIdle(@PathVariable Long id) {
        return tripPlanIdleService.deleteIdle(getTravelUserId(), id) ? success() : error("删除失败");
    }
    @PostMapping("/idle/buy")
    public AjaxResult buyIdle(@RequestBody Map<String, Object> body) {
        Long idleId = body.get("idleId") != null ? Long.valueOf(body.get("idleId").toString()) : null;
        if (idleId == null) return error("缺少 idleId");
        Map<String, Object> res = tripPlanIdleService.buyIdle(getTravelUserId(), idleId);
        if (res.get("error") != null) return error(res.get("error").toString());
        return success(res);
    }

    // ---------- 互动：点赞、评论、想去打卡 ----------
    /** 点赞/取消点赞 targetType: guide/companion/idle */
    @PostMapping("/interact/like")
    public AjaxResult likeToggle(@RequestBody Map<String, Object> body) {
        String targetType = body.get("targetType") != null ? body.get("targetType").toString() : null;
        Long targetId = body.get("targetId") != null ? Long.valueOf(body.get("targetId").toString()) : null;
        if (targetType == null || targetId == null) return error("缺少 targetType / targetId");
        return success(interactionService.likeToggle(getTravelUserId(), targetType, targetId));
    }
    /** 点赞信息（数量+当前用户是否已赞） */
    @GetMapping("/interact/like-info")
    public AjaxResult likeInfo(@RequestParam String targetType, @RequestParam Long targetId) {
        return success(interactionService.likeInfo(getTravelUserId(), targetType, targetId));
    }
    /** 评论列表 */
    @GetMapping("/interact/comments")
    public AjaxResult listComments(@RequestParam String targetType, @RequestParam Long targetId) {
        return success(interactionService.listComments(targetType, targetId));
    }
    /** 发表评论 */
    @PostMapping("/interact/comment")
    public AjaxResult addComment(@RequestBody Map<String, Object> body) {
        String targetType = body.get("targetType") != null ? body.get("targetType").toString() : null;
        Long targetId = body.get("targetId") != null ? Long.valueOf(body.get("targetId").toString()) : null;
        String content = body.get("content") != null ? body.get("content").toString().trim() : null;
        Long parentId = body.get("parentId") != null ? Long.valueOf(body.get("parentId").toString()) : null;
        if (targetType == null || targetId == null || content == null || content.isEmpty()) return error("缺少 targetType / targetId / content");
        long id = interactionService.addComment(getTravelUserId(), targetType, targetId, content, parentId);
        return id > 0 ? success(Collections.singletonMap("id", id)) : error("发表失败");
    }
    // ---------- 大学生优惠 ----------
    /** 热门优惠列表 */
    @GetMapping("/discounts/hot")
    public AjaxResult listHotDiscounts() {
        List<Map<String, Object>> discounts = new ArrayList<>();
        // 模拟数据
        Map<String, Object> discount1 = new HashMap<>();
        discount1.put("icon", "🎡");
        discount1.put("name", "故宫博物院");
        discount1.put("desc", "学生票：20元（原价60元）");
        discount1.put("tips", "需携带学生证");
        discount1.put("tag", "热门");
        discounts.add(discount1);

        Map<String, Object> discount2 = new HashMap<>();
        discount2.put("icon", "🚄");
        discount2.put("name", "火车票");
        discount2.put("desc", "学生票：硬座5折，硬卧7.5折");
        discount2.put("tips", "需办理学生优惠卡");
        discount2.put("tag", "省钱");
        discounts.add(discount2);

        Map<String, Object> discount3 = new HashMap<>();
        discount3.put("icon", "🏰");
        discount3.put("name", "迪士尼乐园");
        discount3.put("desc", "学生票：399元（原价575元）");
        discount3.put("tips", "需提前预约");
        discount3.put("tag", "限时");
        discounts.add(discount3);

        return success(discounts);
    }

    /** 分类优惠列表 */
    @GetMapping("/discounts/category")
    public AjaxResult listDiscountsByCategory(@RequestParam String category) {
        List<Map<String, Object>> discounts = new ArrayList<>();
        // 模拟数据
        if ("attractions".equals(category)) {
            Map<String, Object> discount = new HashMap<>();
            discount.put("name", "景区门票");
            discount.put("desc", "凭学生证享受5-8折优惠");
            discounts.add(discount);
        } else if ("transportation".equals(category)) {
            Map<String, Object> discount = new HashMap<>();
            discount.put("name", "公共交通");
            discount.put("desc", "部分城市公交地铁学生卡5折");
            discounts.add(discount);
        } else if ("accommodation".equals(category)) {
            Map<String, Object> discount = new HashMap<>();
            discount.put("name", "学生宿舍");
            discount.put("desc", "部分青年旅舍提供学生优惠价");
            discounts.add(discount);
        } else if ("food".equals(category)) {
            Map<String, Object> discount = new HashMap<>();
            discount.put("name", "餐饮优惠");
            discount.put("desc", "部分餐厅凭学生证享受9折优惠");
            discounts.add(discount);
        }
        return success(discounts);
    }

    /** 优惠日历 */
    @GetMapping("/discounts/calendar")
    public AjaxResult getDiscountCalendar() {
        List<Map<String, Object>> calendar = new ArrayList<>();
        // 模拟数据
        Map<String, Object> event1 = new HashMap<>();
        event1.put("date", "5月1日");
        event1.put("event", "五一假期优惠");
        calendar.add(event1);

        Map<String, Object> event2 = new HashMap<>();
        event2.put("date", "6月1日");
        event2.put("event", "儿童节特惠");
        calendar.add(event2);

        Map<String, Object> event3 = new HashMap<>();
        event3.put("date", "9月1日");
        event3.put("event", "开学季优惠");
        calendar.add(event3);

        return success(calendar);
    }

    // ---------- AA记账 ----------
    @Autowired private ITravelAaService travelAaService;

    /** 创建队伍 */
    @PostMapping("/aa/team/create")
    public AjaxResult createTeam(@RequestBody Map<String, Object> body) {
        String teamName = body.get("teamName") != null ? body.get("teamName").toString() : null;
        String travelName = body.get("travelName") != null ? body.get("travelName").toString() : null;
        if (teamName == null || travelName == null) {
            return error("缺少必要参数");
        }
        Long userId = getTravelUserId();
        Object team = travelAaService.createTeam(userId, teamName, travelName);
        if (team == null) return error("创建队伍失败");
        return success(team);
    }

    /** 加入队伍 */
    @PostMapping("/aa/team/join")
    public AjaxResult joinTeam(@RequestBody Map<String, Object> body) {
        String joinCode = body.get("joinCode") != null ? body.get("joinCode").toString() : null;
        if (joinCode == null) {
            return error("缺少加入码");
        }
        Long userId = getTravelUserId();
        Object team = travelAaService.joinTeam(userId, joinCode);
        if (team == null) return error("加入队伍失败");
        return success(team);
    }

    /** 我的队伍列表 */
    @GetMapping("/aa/teams")
    public AjaxResult myTeams() {
        Long userId = getTravelUserId();
        return success(travelAaService.getMyTeams(userId));
    }

    /** 队伍详情 */
    @GetMapping("/aa/team/{id}")
    public AjaxResult getTeamDetail(@PathVariable Long id) {
        Object team = travelAaService.getTeamById(id);
        if (team == null) return error("队伍不存在");
        return success(team);
    }

    /** 队伍成员列表 */
    @GetMapping("/aa/team/{id}/members")
    public AjaxResult getTeamMembers(@PathVariable Long id) {
        return success(travelAaService.getTeamMembers(id));
    }

    /** 更新队伍状态 */
    @PutMapping("/aa/team/{id}/status")
    public AjaxResult updateTeamStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer status = body.get("status") != null ? Integer.valueOf(body.get("status").toString()) : null;
        if (status == null) return error("缺少状态参数");
        boolean result = travelAaService.updateTeamStatus(id, status);
        return success(result);
    }

    /** 离开队伍 */
    @PostMapping("/aa/team/{id}/leave")
    public AjaxResult leaveTeam(@PathVariable Long id) {
        Long userId = getTravelUserId();
        boolean result = travelAaService.leaveTeam(id, userId);
        return success(result);
    }

    /** 创建账单 */
    @PostMapping("/aa/bill/create")
    public AjaxResult createBill(@RequestBody Map<String, Object> body) {
        Long teamId = body.get("teamId") != null ? Long.valueOf(body.get("teamId").toString()) : null;
        String billName = body.get("billName") != null ? body.get("billName").toString() : null;
        if (teamId == null || billName == null) {
            return error("缺少必要参数");
        }
        Long userId = getTravelUserId();
        Object bill = travelAaService.createBill(teamId, userId, billName);
        if (bill == null) return error("创建账单失败");
        return success(bill);
    }

    /** 队伍账单列表 */
    @GetMapping("/aa/team/{id}/bills")
    public AjaxResult getTeamBills(@PathVariable Long id) {
        return success(travelAaService.getTeamBills(id));
    }

    /** 账单详情 */
    @GetMapping("/aa/bill/{id}")
    public AjaxResult getBillDetail(@PathVariable Long id) {
        Object bill = travelAaService.getBillById(id);
        if (bill == null) return error("账单不存在");
        return success(bill);
    }

    /** 添加消费明细 */
    @PostMapping("/aa/expense/add")
    public AjaxResult addExpense(@RequestBody Map<String, Object> body) {
        Long billId = body.get("billId") != null ? Long.valueOf(body.get("billId").toString()) : null;
        Long teamId = body.get("teamId") != null ? Long.valueOf(body.get("teamId").toString()) : null;
        String expenseName = body.get("expenseName") != null ? body.get("expenseName").toString() : null;
        Double amount = body.get("amount") != null ? Double.valueOf(body.get("amount").toString()) : null;
        String category = body.get("category") != null ? body.get("category").toString() : null;
        String description = body.get("description") != null ? body.get("description").toString() : null;
        String paymentMethod = body.get("paymentMethod") != null ? body.get("paymentMethod").toString() : null;

        if (billId == null || teamId == null || expenseName == null || amount == null) {
            return error("缺少必要参数");
        }

        Long userId = getTravelUserId();
        Object expense = travelAaService.addExpense(billId, teamId, userId, expenseName, BigDecimal.valueOf(amount), category, description, paymentMethod);
        if (expense == null) return error("添加消费明细失败");
        return success(expense);
    }

    /** 账单消费明细 */
    @GetMapping("/aa/bill/{id}/expenses")
    public AjaxResult getBillExpenses(@PathVariable Long id) {
        return success(travelAaService.getBillExpenses(id));
    }

    /** 队伍消费明细 */
    @GetMapping("/aa/team/{id}/expenses")
    public AjaxResult getTeamExpenses(@PathVariable Long id) {
        return success(travelAaService.getTeamExpenses(id));
    }

    /** 计算账单 */
    @GetMapping("/aa/bill/{id}/calculate")
    public AjaxResult calculateBill(@PathVariable Long id) {
        Object result = travelAaService.calculateBill(id);
        if (result == null) return error("计算账单失败");
        return success(result);
    }

    /** 生成账单分配 */
    @PostMapping("/aa/bill/{id}/allocate")
    public AjaxResult generateBillAllocation(@PathVariable Long id) {
        Object result = travelAaService.generateBillAllocation(id);
        if (result == null) return error("生成账单分配失败");
        return success(result);
    }

    /** 支付账单分配 */
    @PostMapping("/aa/allocation/{id}/pay")
    public AjaxResult payBillAllocation(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Double amount = body.get("amount") != null ? Double.valueOf(body.get("amount").toString()) : null;
        if (amount == null) return error("缺少金额参数");
        boolean result = travelAaService.payBillAllocation(id, BigDecimal.valueOf(amount));
        return success(result);
    }

    /** 队伍统计 */
    @GetMapping("/aa/team/{id}/statistics")
    public AjaxResult getTeamStatistics(@PathVariable Long id) {
        Object result = travelAaService.getTeamStatistics(id);
        if (result == null) return error("获取统计信息失败");
        return success(result);
    }

    /** 导出账单 */
    @PostMapping("/aa/bill/{id}/export")
    public AjaxResult exportBill(@PathVariable Long id) {
        Object result = travelAaService.exportBill(id);
        if (result == null) return error("导出账单失败");
        return success(result);
    }

    /** 想去/取消想去（目的地） */
    @PostMapping("/interact/want-go")
    public AjaxResult wantGoToggle(@RequestBody Map<String, Object> body) {
        Long destinationId = body.get("destinationId") != null ? Long.valueOf(body.get("destinationId").toString()) : null;
        if (destinationId == null) return error("缺少 destinationId");
        return success(interactionService.wantGoToggle(getTravelUserId(), destinationId));
    }
    /** 目的地互动统计（想去/去过人数及当前用户状态） */
    @GetMapping("/interact/destination-stats")
    public AjaxResult destinationStats(@RequestParam Long destinationId) {
        return success(interactionService.destinationStats(getTravelUserId(), destinationId));
    }

    private static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            if (dateStr.length() <= 10) return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}
