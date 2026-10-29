package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 美食
 */
public class XlFood extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long destinationId;
    private String name;
    private String priceRange;
    private String address;
    private String description;
    private String image;
    private Integer aiVerified;
    private Double latitude;
    private Double longitude;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDestinationId() { return destinationId; }
    public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPriceRange() { return priceRange; }
    public void setPriceRange(String priceRange) { this.priceRange = priceRange; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Integer getAiVerified() { return aiVerified; }
    public void setAiVerified(Integer aiVerified) { this.aiVerified = aiVerified; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
