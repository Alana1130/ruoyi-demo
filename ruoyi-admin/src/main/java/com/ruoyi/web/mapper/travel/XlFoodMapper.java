package com.ruoyi.web.mapper.travel;

import java.util.List;
import com.ruoyi.web.domain.travel.XlFood;

/**
 * 美食 数据层
 */
public interface XlFoodMapper {
    XlFood selectById(Long id);
    List<XlFood> selectByDestinationId(Long destinationId);
}
