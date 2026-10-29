package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 星星兑换/购买商品（文旅公益可星星换也可直接买）
 */
public class XlRewardItem extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String type;
    private Integer starsCost;
    private BigDecimal price;
    private String description;
    private String image;
    private Integer stock;
    private Integer allowPurchase;
    private String partnerName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getStarsCost() { return starsCost; }
    public void setStarsCost(Integer starsCost) { this.starsCost = starsCost; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getAllowPurchase() { return allowPurchase; }
    public void setAllowPurchase(Integer allowPurchase) { this.allowPurchase = allowPurchase; }
    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
}
