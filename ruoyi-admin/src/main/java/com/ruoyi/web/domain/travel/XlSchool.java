package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 学校
 */
public class XlSchool extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String region;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
