package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlPetTemplate;
import java.util.List;

public interface XlPetTemplateMapper {
    List<XlPetTemplate> selectByDestinationId(Long destinationId);
    List<XlPetTemplate> selectAll();
    XlPetTemplate selectById(Long id);
}
