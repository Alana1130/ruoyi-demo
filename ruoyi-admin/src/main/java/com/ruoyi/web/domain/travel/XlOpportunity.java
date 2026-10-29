package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 实践机会（兼职 / 志愿），可与目的地关联
 */
public class XlOpportunity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    /** 关联目的地，可选 */
    private Long destinationId;
    /** 地区文字（如 北京、杭州），用于筛选与展示 */
    private String region;
    /** part_time=兼职, volunteer=志愿 */
    private String type;
    private String title;
    private String description;
    /** 报名/联系方式（文案或链接） */
    private String contactInfo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDestinationId() { return destinationId; }
    public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
}
