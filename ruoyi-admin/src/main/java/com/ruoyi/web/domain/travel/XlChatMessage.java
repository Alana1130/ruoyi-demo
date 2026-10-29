package com.ruoyi.web.domain.travel;

import java.util.Date;

/**
 * 讨论留言（目的地/线路下）
 */
public class XlChatMessage {
    private Long id;
    private String topicType;
    private Long topicId;
    private Long userId;
    private Long parentId;
    private String nickname;
    private String content;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTopicType() { return topicType; }
    public void setTopicType(String topicType) { this.topicType = topicType; }
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
