package com.ruoyi.web.domain.travel;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

public class XlUserFoodLicense extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private Long licenseTemplateId;
    private Date gotAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getLicenseTemplateId() { return licenseTemplateId; }
    public void setLicenseTemplateId(Long licenseTemplateId) { this.licenseTemplateId = licenseTemplateId; }
    public Date getGotAt() { return gotAt; }
    public void setGotAt(Date gotAt) { this.gotAt = gotAt; }
}
