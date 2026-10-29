package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * AA记账账单
 */
public class XlAaBill extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long teamId; // 队伍ID
    private String name; // 账单名称
    private BigDecimal totalAmount; // 总金额
    private Integer memberCount; // 成员数量
    private BigDecimal perPersonAmount; // 人均金额
    private Integer status; // 状态：0-未结算，1-已结算
    private Long creatorId; // 创建者ID

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Integer getMemberCount() { return memberCount; }
    public void setMemberCount(Integer memberCount) { this.memberCount = memberCount; }
    public BigDecimal getPerPersonAmount() { return perPersonAmount; }
    public void setPerPersonAmount(BigDecimal perPersonAmount) { this.perPersonAmount = perPersonAmount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
}
