package com.ruoyi.web.mapper.travel;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.web.domain.travel.XlDestination;

/**
 * 目的地 数据层
 */
public interface XlDestinationMapper {
    XlDestination selectById(Long id);
    List<XlDestination> selectList(XlDestination q);
    List<XlDestination> selectByKeyword(@Param("keyword") String keyword);
}
