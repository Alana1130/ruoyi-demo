package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlOpportunity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实践机会（兼职/志愿） 数据层
 */
public interface XlOpportunityMapper {
    XlOpportunity selectById(Long id);
    List<XlOpportunity> selectList(@Param("destinationId") Long destinationId, @Param("type") String type);
    List<XlOpportunity> selectByKeyword(@Param("keyword") String keyword);
}
