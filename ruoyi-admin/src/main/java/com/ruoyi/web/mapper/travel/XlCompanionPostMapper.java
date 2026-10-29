package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlCompanionPost;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 约伴帖子 Mapper
 */
public interface XlCompanionPostMapper {
    int insert(XlCompanionPost post);
    XlCompanionPost selectById(Long id);
    List<XlCompanionPost> selectList(@Param("destinationId") Long destinationId,
                                    @Param("keyword") String keyword,
                                    @Param("dateFrom") Date dateFrom,
                                    @Param("dateTo") Date dateTo,
                                    @Param("status") String status);
    int update(XlCompanionPost post);
    int deleteByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);
}
