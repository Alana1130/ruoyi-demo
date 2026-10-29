package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

/** 二手/闲置 */
public class XlIdleItem extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private String title;
    private String category;
    private String description;
    private Integer priceStars;
    private String images;
    private String status;
    private Long buyerId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPriceStars() { return priceStars; }
    public void setPriceStars(Integer priceStars) { this.priceStars = priceStars; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
}
