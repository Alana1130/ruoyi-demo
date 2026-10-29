package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AA记账队伍成员
 */
public class XlAaTeamMember extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long teamId; // 队伍ID
    private Long userId; // 用户ID
    private String nickname; // 成员昵称
    private String avatar; // 成员头像
    private Integer role; // 角色：0-普通成员，1-队长
    private Integer status; // 状态：0-待确认，1-已加入

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
