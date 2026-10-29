package com.ruoyi.web.service.travel;

/**
 * 旅游小程序 token：生成、校验、删除
 */
public interface ITravelTokenService {
    /** 生成 token 并缓存 userId，有效期为 expireDays 天 */
    String createToken(Long userId, int expireDays);

    /** 根据 token 获取 userId，无效或过期返回 null */
    Long getUserIdByToken(String token);

    /** 删除 token（登出） */
    void removeToken(String token);
}
