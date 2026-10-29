package com.ruoyi.web.mapper.travel;

import java.util.List;
import com.ruoyi.web.domain.travel.XlTicket;

/**
 * 门票 数据层
 */
public interface XlTicketMapper {
    List<XlTicket> selectByDestinationId(Long destinationId);
}
