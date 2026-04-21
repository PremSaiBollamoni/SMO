package com.cutm.smo.models;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "style_variant")
public class StyleVariant {
    @Id
    @Column(name = "style_variant_id") private Long styleVariantId;
    
    @Column(name = "style_id") private Long styleId;
    @Column(name = "button_id") private Long buttonId;
    @Column(name = "thread_id") private Long threadId;
    
    @Column(name = "gtg_id") private String gtgId;
    @Column(name = "size") private String size;
    @Column(name = "sleeve_type") private String sleeveType;
    @Column(name = "color") private String color;
    @Column(name = "consumption_per_shirt") private BigDecimal consumptionPerShirt;
    @Column(name = "no_of_shirts_target") private Integer noOfShirtsTarget;
    @Column(name = "status") private String status;

    public Long getStyleVariantId() { return styleVariantId; }
    public void setStyleVariantId(Long styleVariantId) { this.styleVariantId = styleVariantId; }
    public Long getStyleId() { return styleId; }
    public void setStyleId(Long styleId) { this.styleId = styleId; }
    public Long getButtonId() { return buttonId; }
    public void setButtonId(Long buttonId) { this.buttonId = buttonId; }
    public Long getThreadId() { return threadId; }
    public void setThreadId(Long threadId) { this.threadId = threadId; }
    public String getGtgId() { return gtgId; }
    public void setGtgId(String gtgId) { this.gtgId = gtgId; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getSleeveType() { return sleeveType; }
    public void setSleeveType(String sleeveType) { this.sleeveType = sleeveType; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public BigDecimal getConsumptionPerShirt() { return consumptionPerShirt; }
    public void setConsumptionPerShirt(BigDecimal consumptionPerShirt) { this.consumptionPerShirt = consumptionPerShirt; }
    public Integer getNoOfShirtsTarget() { return noOfShirtsTarget; }
    public void setNoOfShirtsTarget(Integer noOfShirtsTarget) { this.noOfShirtsTarget = noOfShirtsTarget; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}