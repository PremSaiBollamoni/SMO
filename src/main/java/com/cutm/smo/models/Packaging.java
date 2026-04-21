package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "packaging")
public class Packaging {
    @Id
    @Column(name = "packaging_id")
    private Long packagingId;
    
    @Column(name = "garment_id")
    private Long garmentId;
    
    @Column(name = "qty")
    private Integer qty;
    
    @Column(name = "status")
    private String status;

    public Long getPackagingId() { return packagingId; }
    public void setPackagingId(Long packagingId) { this.packagingId = packagingId; }
    public Long getGarmentId() { return garmentId; }
    public void setGarmentId(Long garmentId) { this.garmentId = garmentId; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}