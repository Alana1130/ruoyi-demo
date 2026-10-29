package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlSchool;
import java.util.List;

public interface XlSchoolMapper {
    List<XlSchool> selectAll();
    XlSchool selectById(Long id);
}
