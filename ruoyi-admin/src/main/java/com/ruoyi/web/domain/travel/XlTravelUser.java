package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 旅游小程序用户
 */
public class XlTravelUser extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String openid;
    private String nickname;
    private String avatar;
    private Integer stars;
    private String phone;
    private String password;
    private Long schoolId;
    private Integer schoolVerified;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    public Integer getSchoolVerified() { return schoolVerified; }
    public void setSchoolVerified(Integer schoolVerified) { this.schoolVerified = schoolVerified; }
}
