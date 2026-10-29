package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlSchoolPost;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface XlSchoolPostMapper {
    int insert(XlSchoolPost post);
    List<XlSchoolPost> selectBySchoolId(@Param("schoolId") Long schoolId, @Param("limit") int limit, @Param("offset") int offset);
    int countBySchoolId(Long schoolId);
}
