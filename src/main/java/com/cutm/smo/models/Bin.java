package com.cutm.smo.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bin")
public class Bin {
    @Id
    @Column(name = "bin_id")
    private Long binId;
    
    @Column(name = "qr_code")
    private String qrCode;
    
    @Column(name = "style_id")
    private Long styleId;
    
    @Column(name = "style_variant_id")
    private Long styleVariantId;
    
    @Column(name = "size")
    private String size;
    
    @Column(name = "sleeve_type")
    private String sleeveType;
    
    @Column(name = "qty")
    private Integer qty;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "parent_bin_id")
    private Long parentBinId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Long getBinId() { return binId; }
    public void setBinId(Long binId) { this.binId = binId; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public Long getStyleId() { return styleId; }
    public void setStyleId(Long styleId) { this.styleId = styleId; }
    public Long getStyleVariantId() { return styleVariantId; }
    public void setStyleVariantId(Long styleVariantId) { this.styleVariantId = styleVariantId; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getSleeveType() { return sleeveType; }
    public void setSleeveType(String sleeveType) { this.sleeveType = sleeveType; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getParentBinId() { return parentBinId; }
    public void setParentBinId(Long parentBinId) { this.parentBinId = parentBinId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}