package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlRewardOrder;

import java.util.List;
import java.util.Map;

/**
 * 商品兑换/购买记录 数据层
 */
public interface XlRewardOrderMapper {
    int insert(XlRewardOrder order);
    List<Map<String, Object>> selectOrdersByUserId(Long userId);
}
