package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlUserFoodLicense;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface XlUserFoodLicenseMapper {
    int insert(XlUserFoodLicense license);
    XlUserFoodLicense selectById(Long id);
    List<XlUserFoodLicense> selectByUserId(Long userId);
    int updateOwner(@Param("id") Long id, @Param("userId") Long userId);
    int countByUserAndDestination(@Param("userId") Long userId, @Param("destinationId") Long destinationId);
}
