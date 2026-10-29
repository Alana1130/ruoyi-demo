package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

public class XlPetTemplate extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long destinationId;
    private String name;
    private String icon;
    private String description;
    private Integer sortOrder;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDestinationId() { return destinationId; }
    public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
