package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 用户兑换记录
 */
public class XlUserExchange extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private Long rewardItemId;
    private Date exchangedAt;
    private String itemName;
    private Integer starsCost;
    private String type;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getRewardItemId() { return rewardItemId; }
    public void setRewardItemId(Long rewardItemId) { this.rewardItemId = rewardItemId; }
    public Date getExchangedAt() { return exchangedAt; }
    public void setExchangedAt(Date exchangedAt) { this.exchangedAt = exchangedAt; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public Integer getStarsCost() { return starsCost; }
    public void setStarsCost(Integer starsCost) { this.starsCost = starsCost; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
