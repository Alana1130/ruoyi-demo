package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 约伴/组队帖子
 */
public class XlCompanionPost extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private Long destinationId;
    private String title;
    private String content;
    private Date travelDateStart;
    private Date travelDateEnd;
    private String budgetRange;
    private String contactRemark;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getDestinationId() { return destinationId; }
    public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getTravelDateStart() { return travelDateStart; }
    public void setTravelDateStart(Date travelDateStart) { this.travelDateStart = travelDateStart; }
    public Date getTravelDateEnd() { return travelDateEnd; }
    public void setTravelDateEnd(Date travelDateEnd) { this.travelDateEnd = travelDateEnd; }
    public String getBudgetRange() { return budgetRange; }
    public void setBudgetRange(String budgetRange) { this.budgetRange = budgetRange; }
    public String getContactRemark() { return contactRemark; }
    public void setContactRemark(String contactRemark) { this.contactRemark = contactRemark; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
