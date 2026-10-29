package com.ruoyi.web.service.travel.impl;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.web.service.travel.ITravelTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TravelTokenServiceImpl implements ITravelTokenService {

    private static final int DEFAULT_EXPIRE_DAYS = 7;

    @Autowired
    private RedisCache redisCache;

    @Override
    public String createToken(Long userId, int expireDays) {
        if (userId == null) return null;
        String token = IdUtils.fastUUID();
        String key = CacheConstants.TRAVEL_TOKEN_KEY + token;
        redisCache.setCacheObject(key, userId, expireDays <= 0 ? DEFAULT_EXPIRE_DAYS : expireDays, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getUserIdByToken(String token) {
        if (StringUtils.isEmpty(token)) return null;
        String key = CacheConstants.TRAVEL_TOKEN_KEY + token;
        return redisCache.getCacheObject(key);
    }

    @Override
    public void removeToken(String token) {
        if (StringUtils.isEmpty(token)) return;
        String key = CacheConstants.TRAVEL_TOKEN_KEY + token;
        redisCache.deleteObject(key);
    }
}
