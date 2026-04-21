package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "item")
public class Item {
    @Id
    @Column(name = "item_id")
    private Long itemId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "unit")
    private String unit;
    
    @Column(name = "sourcing_type")
    private String sourcingType;
    
    @Column(name = "status")
    private String status;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getSourcingType() { return sourcingType; }
    public void setSourcingType(String sourcingType) { this.sourcingType = sourcingType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}