package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlSemesterPlan;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface XlSemesterPlanMapper {
    int insert(XlSemesterPlan row);
    int update(XlSemesterPlan row);
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
    XlSemesterPlan selectById(Long id);
    List<XlSemesterPlan> selectByUserId(@Param("userId") Long userId);
}
