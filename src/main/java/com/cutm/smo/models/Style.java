package com.cutm.smo.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "style")
public class Style {
    @Id
    @Column(name = "style_id") private Long styleId;
    @Column(name = "style_no") private String styleNo;
    @Column(name = "concept") private String concept;
    @Column(name = "main_label") private String mainLabel;
    @Column(name = "branding_label") private String brandingLabel;
    @Column(name = "pattern_image", columnDefinition = "TEXT") private String patternImage;
    @Column(name = "description", columnDefinition = "TEXT") private String description;
    @Column(name = "status") private String status;
    @Column(name = "created_at") private LocalDateTime createdAt;

    public Long getStyleId() { return styleId; }
    public void setStyleId(Long styleId) { this.styleId = styleId; }
    public String getStyleNo() { return styleNo; }
    public void setStyleNo(String styleNo) { this.styleNo = styleNo; }
    public String getConcept() { return concept; }
    public void setConcept(String concept) { this.concept = concept; }
    public String getMainLabel() { return mainLabel; }
    public void setMainLabel(String mainLabel) { this.mainLabel = mainLabel; }
    public String getBrandingLabel() { return brandingLabel; }
    public void setBrandingLabel(String brandingLabel) { this.brandingLabel = brandingLabel; }
    public String getPatternImage() { return patternImage; }
    public void setPatternImage(String patternImage) { this.patternImage = patternImage; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}