package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 门票
 */
public class XlTicket extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long destinationId;
    private Long attractionId;
    private String name;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String buyLink;
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDestinationId() { return destinationId; }
    public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }
    public Long getAttractionId() { return attractionId; }
    public void setAttractionId(Long attractionId) { this.attractionId = attractionId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public String getBuyLink() { return buyLink; }
    public void setBuyLink(String buyLink) { this.buyLink = buyLink; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
