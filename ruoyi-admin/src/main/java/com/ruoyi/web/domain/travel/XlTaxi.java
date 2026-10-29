package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 打车/出行服务
 */
public class XlTaxi extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long destinationId;
    private String name;
    private String appType;
    private String description;
    private String phone;
    private String link;
    private String priceApprox;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDestinationId() { return destinationId; }
    public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAppType() { return appType; }
    public void setAppType(String appType) { this.appType = appType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public String getPriceApprox() { return priceApprox; }
    public void setPriceApprox(String priceApprox) { this.priceApprox = priceApprox; }
}
