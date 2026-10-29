package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlNotification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XlNotificationMapper {
    List<XlNotification> selectByUserId(@Param("userId") Long userId);
    int insert(XlNotification n);
    int updateRead(@Param("id") Long id, @Param("userId") Long userId);
    int markAllRead(@Param("userId") Long userId);
    int countUnreadByUserId(@Param("userId") Long userId);
}
