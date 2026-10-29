package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlUserExperience;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户出行体验 数据层
 */
public interface XlUserExperienceMapper {
    int insert(XlUserExperience exp);
    XlUserExperience selectById(Long id);
    List<XlUserExperience> selectVerifiedByTarget(String targetType, Long targetId);
    List<Map<String, Object>> selectGuides(@Param("destinationId") Long destinationId, @Param("tag") String tag);
    int updateAiVerify(@Param("id") Long id, @Param("aiVerified") int aiVerified, @Param("aiScore") BigDecimal aiScore);
}
