package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 目的地
 */
public class XlDestination extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String region;
    private String description;
    private String coverImage;
    private Integer starReward;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public Integer getStarReward() { return starReward; }
    public void setStarReward(Integer starReward) { this.starReward = starReward; }
}
