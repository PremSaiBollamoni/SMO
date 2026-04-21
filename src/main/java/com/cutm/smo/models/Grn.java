package com.cutm.smo.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "grn")
public class Grn {
    @Id
    @Column(name = "grn_id")
    private Long grnId;
    
    @Column(name = "po_id")
    private Long poId;
    
    @Column(name = "date")
    private LocalDate date;
    
    @Column(name = "status")
    private String status;

    public Long getGrnId() { return grnId; }
    public void setGrnId(Long grnId) { this.grnId = grnId; }
    public Long getPoId() { return poId; }
    public void setPoId(Long poId) { this.poId = poId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}