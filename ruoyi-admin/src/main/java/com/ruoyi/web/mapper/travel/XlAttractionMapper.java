package com.ruoyi.web.mapper.travel;

import java.util.List;
import com.ruoyi.web.domain.travel.XlAttraction;

/**
 * 景点 数据层
 */
public interface XlAttractionMapper {
    XlAttraction selectById(Long id);
    List<XlAttraction> selectByDestinationId(Long destinationId);
}
