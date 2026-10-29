package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlLike;
import org.apache.ibatis.annotations.Param;

public interface XlLikeMapper {
    int insert(XlLike row);
    int deleteByUserAndTarget(@Param("userId") Long userId, @Param("targetType") String targetType, @Param("targetId") Long targetId);
    int countByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);
    XlLike selectByUserAndTarget(@Param("userId") Long userId, @Param("targetType") String targetType, @Param("targetId") Long targetId);
}
