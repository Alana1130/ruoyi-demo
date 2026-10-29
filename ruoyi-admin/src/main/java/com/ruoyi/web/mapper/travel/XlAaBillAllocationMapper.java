package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlAaBillAllocation;

import java.util.List;

/**
 * AA记账账单分配Mapper
 */
public interface XlAaBillAllocationMapper {
    int insert(XlAaBillAllocation allocation);
    XlAaBillAllocation selectById(Long id);
    List<XlAaBillAllocation> selectByBillId(Long billId);
    List<XlAaBillAllocation> selectByUserId(Long userId);
    int update(XlAaBillAllocation allocation);
    int deleteById(Long id);
    int deleteByBillId(Long billId);
}
