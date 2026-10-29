package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 轮播图
 */
public class XlBanner extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String image;
    private String link;
    private Integer sortOrder;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
