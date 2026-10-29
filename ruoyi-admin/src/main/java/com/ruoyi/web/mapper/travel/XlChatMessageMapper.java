package com.ruoyi.web.mapper.travel;

import com.ruoyi.web.domain.travel.XlChatMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XlChatMessageMapper {
    List<XlChatMessage> selectByTopic(@Param("topicType") String topicType, @Param("topicId") Long topicId);
    XlChatMessage selectById(Long id);
    int insert(XlChatMessage msg);
}
