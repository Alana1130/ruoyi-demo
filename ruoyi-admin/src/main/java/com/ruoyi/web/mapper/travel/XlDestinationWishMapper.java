package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlDestinationWish;
import org.apache.ibatis.annotations.Param;

public interface XlDestinationWishMapper {
    int insert(XlDestinationWish row);
    int deleteByUserAndDestination(@Param("userId") Long userId, @Param("destinationId") Long destinationId);
    int countByDestinationId(Long destinationId);
    XlDestinationWish selectByUserAndDestination(@Param("userId") Long userId, @Param("destinationId") Long destinationId);
}
