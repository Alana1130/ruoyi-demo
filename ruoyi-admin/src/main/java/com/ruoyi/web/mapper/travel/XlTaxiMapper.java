package com.ruoyi.web.mapper.travel;

import java.util.List;
import com.ruoyi.web.domain.travel.XlTaxi;

/**
 * 打车/出行 数据层
 */
public interface XlTaxiMapper {
    List<XlTaxi> selectByDestinationId(Long destinationId);
}
