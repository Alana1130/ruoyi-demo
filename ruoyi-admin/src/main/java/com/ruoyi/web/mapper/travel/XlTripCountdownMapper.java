package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlTripCountdown;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface XlTripCountdownMapper {
    int insert(XlTripCountdown row);
    int update(XlTripCountdown row);
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
    XlTripCountdown selectById(Long id);
    List<XlTripCountdown> selectByUserId(@Param("userId") Long userId);
}
