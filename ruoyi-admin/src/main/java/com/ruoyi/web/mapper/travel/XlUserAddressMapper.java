package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlUserAddress;
import java.util.List;

/**
 * 用户收货地址 数据层
 */
public interface XlUserAddressMapper {
    List<XlUserAddress> selectByUserId(Long userId);
    XlUserAddress selectById(Long id);
    int insert(XlUserAddress address);
    int update(XlUserAddress address);
    int deleteById(Long id);
    int clearDefaultByUserId(Long userId);
}
