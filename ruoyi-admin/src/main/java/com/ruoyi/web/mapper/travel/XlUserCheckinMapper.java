package com.ruoyi.web.mapper.travel;

import java.util.List;
import com.ruoyi.web.domain.travel.XlUserCheckin;
import org.apache.ibatis.annotations.Param;

/**
 * 用户点亮记录 数据层
 */
public interface XlUserCheckinMapper {
    XlUserCheckin selectByUserAndDestination(@Param("userId") Long userId, @Param("destinationId") Long destinationId);
    int countByDestinationId(Long destinationId);
    List<XlUserCheckin> selectMyCheckins(Long userId);
    int insert(XlUserCheckin checkin);
}
