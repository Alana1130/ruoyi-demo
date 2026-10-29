package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlComment;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface XlCommentMapper {
    int insert(XlComment row);
    List<XlComment> selectByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);
}
