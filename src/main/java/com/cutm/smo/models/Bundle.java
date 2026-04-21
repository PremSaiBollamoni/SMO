package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "bundle")
public class Bundle {
    @Id
    @Column(name = "bundle_id")
    private Long bundleId;
    
    @Column(name = "style_variant_id")
    private Long styleVariantId;
    
    @Column(name = "routing_id")
    private Long routingId;
    
    @Column(name = "status")
    private String status;

    public Long getBundleId() { return bundleId; }
    public void setBundleId(Long bundleId) { this.bundleId = bundleId; }
    public Long getStyleVariantId() { return styleVariantId; }
    public void setStyleVariantId(Long styleVariantId) { this.styleVariantId = styleVariantId; }
    public Long getRoutingId() { return routingId; }
    public void setRoutingId(Long routingId) { this.routingId = routingId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}