package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 用户点亮记录
 */
public class XlUserCheckin extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private Long destinationId;
    private Integer starsEarned;
    private Date checkinAt;
    private String destinationName;
    private String region;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getDestinationId() { return destinationId; }
    public void setDestinationId(Long destinationId) { this.destinationId = destinationId; }
    public Integer getStarsEarned() { return starsEarned; }
    public void setStarsEarned(Integer starsEarned) { this.starsEarned = starsEarned; }
    public Date getCheckinAt() { return checkinAt; }
    public void setCheckinAt(Date checkinAt) { this.checkinAt = checkinAt; }
    public String getDestinationName() { return destinationName; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}
