package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 线路节点
 */
public class XlRouteItem extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long routeId;
    private Integer dayNo;
    private String itemType;
    private Long itemId;
    private Integer sortOrder;
    private String transportApprox;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public Integer getDayNo() { return dayNo; }
    public void setDayNo(Integer dayNo) { this.dayNo = dayNo; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getTransportApprox() { return transportApprox; }
    public void setTransportApprox(String transportApprox) { this.transportApprox = transportApprox; }
}
