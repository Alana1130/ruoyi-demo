package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 用户出行体验（供 AI 真实性核验）
 */
public class XlUserExperience extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private String targetType;
    private Long targetId;
    private String content;
    private String title;
    private String tags;
    private Integer aiVerified;
    private BigDecimal aiScore;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Integer getAiVerified() { return aiVerified; }
    public void setAiVerified(Integer aiVerified) { this.aiVerified = aiVerified; }
    public BigDecimal getAiScore() { return aiScore; }
    public void setAiScore(BigDecimal aiScore) { this.aiScore = aiScore; }
}
