package com.ruoyi.web.mapper.travel;

import java.util.List;
import com.ruoyi.web.domain.travel.XlRewardItem;

/**
 * 兑换商品 数据层
 */
public interface XlRewardItemMapper {
    XlRewardItem selectById(Long id);
    List<XlRewardItem> selectList(String type);
    int decrStock(Long id);
}
