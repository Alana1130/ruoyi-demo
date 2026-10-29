package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;

/**
 * 商品兑换/购买记录
 */
public class XlRewardOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private Long rewardItemId;
    private String orderType;
    private Integer starsUsed;
    private BigDecimal amountPaid;
    private String status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getRewardItemId() { return rewardItemId; }
    public void setRewardItemId(Long rewardItemId) { this.rewardItemId = rewardItemId; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public Integer getStarsUsed() { return starsUsed; }
    public void setStarsUsed(Integer starsUsed) { this.starsUsed = starsUsed; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public String getReceiverAddress() { return receiverAddress; }
    public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }
}
