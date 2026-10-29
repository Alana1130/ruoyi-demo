package com.ruoyi.web.mapper.travel;

import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface XlSemesterPlanDestMapper {
    int insert(@Param("planId") Long planId, @Param("destinationId") Long destinationId, @Param("sortOrder") Integer sortOrder);
    int deleteByPlanId(Long planId);
    List<Long> selectDestinationIdsByPlanId(Long planId);
}
