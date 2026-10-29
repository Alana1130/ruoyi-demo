package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * AA记账消费明细
 */
public class XlAaExpense extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long billId; // 账单ID
    private Long teamId; // 队伍ID
    private Long userId; // 支付人ID
    private String expenseName; // 消费名称
    private BigDecimal amount; // 金额
    private String category; // 分类：food-餐饮，transport-交通，accommodation-住宿，shopping-购物，other-其他
    private String description; // 描述
    private String paymentMethod; // 支付方式

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getExpenseName() { return expenseName; }
    public void setExpenseName(String expenseName) { this.expenseName = expenseName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
