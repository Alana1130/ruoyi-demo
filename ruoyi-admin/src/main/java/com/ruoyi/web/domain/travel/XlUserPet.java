package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

public class XlUserPet extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private Long petTemplateId;
    private String nickname;
    private Integer hunger;
    private Integer mood;
    private Integer level;
    private Integer exp;
    private Integer evolutionStage;
    private Date lastFeedAt;
    private Date lastPlayAt;
    private Date gotAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getPetTemplateId() { return petTemplateId; }
    public void setPetTemplateId(Long petTemplateId) { this.petTemplateId = petTemplateId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Integer getHunger() { return hunger; }
    public void setHunger(Integer hunger) { this.hunger = hunger; }
    public Integer getMood() { return mood; }
    public void setMood(Integer mood) { this.mood = mood; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Integer getExp() { return exp; }
    public void setExp(Integer exp) { this.exp = exp; }
    public Integer getEvolutionStage() { return evolutionStage; }
    public void setEvolutionStage(Integer evolutionStage) { this.evolutionStage = evolutionStage; }
    public Date getLastFeedAt() { return lastFeedAt; }
    public void setLastFeedAt(Date lastFeedAt) { this.lastFeedAt = lastFeedAt; }
    public Date getLastPlayAt() { return lastPlayAt; }
    public void setLastPlayAt(Date lastPlayAt) { this.lastPlayAt = lastPlayAt; }
    public Date getGotAt() { return gotAt; }
    public void setGotAt(Date gotAt) { this.gotAt = gotAt; }
}
