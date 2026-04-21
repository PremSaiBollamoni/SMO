package com.cutm.smo.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "routing")
public class Routing {
    @Id
    @Column(name = "routing_id") private Long routingId;
    @Column(name = "product_id") private Long productId;
    @Column(name = "version") private Integer version;
    @Column(name = "status") private String status;
    @Column(name = "approval_status") private String approvalStatus;
    @Column(name = "approved_by") private Long approvedBy;
    @Column(name = "approved_at") private LocalDateTime approvedAt;
    @Column(name = "previous_routing_id") private Long previousRoutingId;

    public Long getRoutingId() { return routingId; }
    public void setRoutingId(Long routingId) { this.routingId = routingId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public Long getPreviousRoutingId() { return previousRoutingId; }
    public void setPreviousRoutingId(Long previousRoutingId) { this.previousRoutingId = previousRoutingId; }
}
