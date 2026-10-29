package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/** 出行倒计时 */
public class XlTripCountdown extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private String title;
    private Long destinationId;
    private Date tripDate;
    private Integer remindDays;
    private String memo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getDestinationId() { return destinationId; }
    public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }
    public Date getTripDate() { return tripDate; }
    public void setTripDate(Date tripDate) { this.tripDate = tripDate; }
    public Integer getRemindDays() { return remindDays; }
    public void setRemindDays(Integer remindDays) { this.remindDays = remindDays; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
