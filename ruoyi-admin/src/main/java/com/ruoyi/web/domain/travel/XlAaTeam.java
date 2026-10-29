package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AA记账队伍
 */
public class XlAaTeam extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name; // 队伍名称
    private Long creatorId; // 创建者ID
    private String travelName; // 旅行名称
    private Integer status; // 状态：0-创建中，1-旅行中，2-已结束
    private String joinCode; // 加入码

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public String getTravelName() { return travelName; }
    public void setTravelName(String travelName) { this.travelName = travelName; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getJoinCode() { return joinCode; }
    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }
}
