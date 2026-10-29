package com.ruoyi.web.mapper.travel;

import java.util.List;
import com.ruoyi.web.domain.travel.XlAccommodation;

/**
 * 住宿 数据层
 */
public interface XlAccommodationMapper {
    XlAccommodation selectById(Long id);
    List<XlAccommodation> selectByDestinationId(Long destinationId);
}
