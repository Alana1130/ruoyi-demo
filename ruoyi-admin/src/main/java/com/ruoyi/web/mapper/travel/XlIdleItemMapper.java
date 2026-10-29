package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlIdleItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface XlIdleItemMapper {
    int insert(XlIdleItem row);
    int update(XlIdleItem row);
    int updateStatusAndBuyer(@Param("id") Long id, @Param("status") String status, @Param("buyerId") Long buyerId);
    int deleteByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);
    XlIdleItem selectById(Long id);
    List<XlIdleItem> selectByUserId(@Param("userId") Long userId);
    List<XlIdleItem> selectOnSale(@Param("category") String category, @Param("limit") Integer limit);
}
