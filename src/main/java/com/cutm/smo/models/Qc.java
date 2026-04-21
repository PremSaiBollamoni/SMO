package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "qc")
public class Qc {
    @Id
    @Column(name = "qc_id")
    private Long qcId;
    
    @Column(name = "garment_id")
    private Long garmentId;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "defects", columnDefinition = "TEXT")
    private String defects;

    public Long getQcId() { return qcId; }
    public void setQcId(Long qcId) { this.qcId = qcId; }
    public Long getGarmentId() { return garmentId; }
    public void setGarmentId(Long garmentId) { this.garmentId = garmentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDefects() { return defects; }
    public void setDefects(String defects) { this.defects = defects; }
}