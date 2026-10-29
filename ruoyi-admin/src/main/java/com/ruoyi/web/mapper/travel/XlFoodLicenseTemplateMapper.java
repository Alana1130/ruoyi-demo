package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlFoodLicenseTemplate;
import java.util.List;

public interface XlFoodLicenseTemplateMapper {
    List<XlFoodLicenseTemplate> selectByDestinationId(Long destinationId);
    List<XlFoodLicenseTemplate> selectAll();
    XlFoodLicenseTemplate selectById(Long id);
}
