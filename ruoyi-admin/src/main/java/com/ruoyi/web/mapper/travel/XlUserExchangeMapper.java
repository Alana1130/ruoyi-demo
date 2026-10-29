package com.ruoyi.web.mapper.travel;

import java.util.List;
import com.ruoyi.web.domain.travel.XlUserExchange;

/**
 * 用户兑换记录 数据层
 */
public interface XlUserExchangeMapper {
    int insert(XlUserExchange exchange);
    List<XlUserExchange> selectByUserId(Long userId);
}
