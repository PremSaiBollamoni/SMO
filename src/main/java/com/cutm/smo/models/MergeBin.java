package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "mergebin")
public class MergeBin {
    @Id
    @Column(name = "merge_bin_id")
    private Long mergeBinId;
    
    @Column(name = "bundle_id")
    private Long bundleId;

    public Long getMergeBinId() { return mergeBinId; }
    public void setMergeBinId(Long mergeBinId) { this.mergeBinId = mergeBinId; }
    public Long getBundleId() { return bundleId; }
    public void setBundleId(Long bundleId) { this.bundleId = bundleId; }
}