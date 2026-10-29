package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 学校圈帖子
 */
public class XlSchoolPost extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long schoolId;
    private Long userId;
    private String content;
    private String images;
    private Date createTime;
    private String nickname;
    private String avatar;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
