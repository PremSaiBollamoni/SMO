package com.cutm.smo.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "purchaseorder")
public class PurchaseOrder {
    @Id
    @Column(name = "po_id")
    private Long poId;
    
    @Column(name = "vendor_id")
    private Long vendorId;
    
    @Column(name = "date")
    private LocalDate date;
    
    @Column(name = "status")
    private String status;

    public Long getPoId() { return poId; }
    public void setPoId(Long poId) { this.poId = poId; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}