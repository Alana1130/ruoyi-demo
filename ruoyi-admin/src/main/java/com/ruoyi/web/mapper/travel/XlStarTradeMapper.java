package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlStarTrade;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface XlStarTradeMapper {
    int insert(XlStarTrade trade);
    XlStarTrade selectById(Long id);
    List<XlStarTrade> selectListings(@Param("itemType") String itemType, @Param("limit") Integer limit, @Param("excludeSellerId") Long excludeSellerId);
    List<XlStarTrade> selectBySellerIdAndStatus(@Param("sellerId") Long sellerId, @Param("status") String status);
    List<Long> selectListedItemIdsBySeller(@Param("sellerId") Long sellerId, @Param("itemType") String itemType);
    XlStarTrade selectListingByItem(@Param("itemType") String itemType, @Param("itemId") Long itemId);
    int updateToCompleted(@Param("id") Long id, @Param("buyerId") Long buyerId);
    int updateToCancelled(@Param("id") Long id);
}
