package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * AA记账账单分配
 */
public class XlAaBillAllocation extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long billId; // 账单ID
    private Long userId; // 用户ID
    private BigDecimal amount; // 应付金额
    private BigDecimal paidAmount; // 已付金额
    private Integer status; // 状态：0-未支付，1-已支付

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
