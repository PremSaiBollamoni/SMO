package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "vendor")
public class Vendor {
    @Id
    @Column(name = "vendor_id")
    private Long vendorId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "status")
    private String status;

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}