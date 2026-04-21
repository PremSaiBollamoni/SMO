package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "garment")
public class Garment {
    @Id
    @Column(name = "garment_id")
    private Long garmentId;
    
    @Column(name = "qr_code")
    private String qrCode;
    
    @Column(name = "merge_bin_id")
    private Long mergeBinId;
    
    @Column(name = "bundle_id")
    private Long bundleId;
    
    @Column(name = "bin_id")
    private Long binId;
    
    @Column(name = "style_variant_id")
    private Long styleVariantId;
    
    @Column(name = "status")
    private String status;

    public Long getGarmentId() { return garmentId; }
    public void setGarmentId(Long garmentId) { this.garmentId = garmentId; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public Long getMergeBinId() { return mergeBinId; }
    public void setMergeBinId(Long mergeBinId) { this.mergeBinId = mergeBinId; }
    public Long getBundleId() { return bundleId; }
    public void setBundleId(Long bundleId) { this.bundleId = bundleId; }
    public Long getBinId() { return binId; }
    public void setBinId(Long binId) { this.binId = binId; }
    public Long getStyleVariantId() { return styleVariantId; }
    public void setStyleVariantId(Long styleVariantId) { this.styleVariantId = styleVariantId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}