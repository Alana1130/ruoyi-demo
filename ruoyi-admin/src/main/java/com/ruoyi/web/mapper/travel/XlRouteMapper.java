package com.ruoyi.web.mapper.travel;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.web.domain.travel.XlRoute;

/**
 * 线路 数据层
 */
public interface XlRouteMapper {
    XlRoute selectById(Long id);
    List<XlRoute> selectByDestinationId(Long destinationId);
    List<XlRoute> selectByBudget(@Param("destinationId") Long destinationId, @Param("days") Integer days, @Param("maxBudget") java.math.BigDecimal maxBudget);
}
