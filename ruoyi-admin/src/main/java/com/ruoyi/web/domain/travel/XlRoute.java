package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 线路
 */
public class XlRoute extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long destinationId;
    private String name;
    private Integer days;
    private BigDecimal totalBudgetMin;
    private BigDecimal totalBudgetMax;
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDestinationId() { return destinationId; }
    public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
    public BigDecimal getTotalBudgetMin() { return totalBudgetMin; }
    public void setTotalBudgetMin(BigDecimal totalBudgetMin) { this.totalBudgetMin = totalBudgetMin; }
    public BigDecimal getTotalBudgetMax() { return totalBudgetMax; }
    public void setTotalBudgetMax(BigDecimal totalBudgetMax) { this.totalBudgetMax = totalBudgetMax; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
