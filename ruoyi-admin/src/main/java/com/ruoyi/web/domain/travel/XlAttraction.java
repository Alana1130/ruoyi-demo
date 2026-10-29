package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 景点
 */
public class XlAttraction extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long destinationId;
    private String name;
    private BigDecimal ticketPrice;
    private String openingHours;
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
    public BigDecimal getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }
    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
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
