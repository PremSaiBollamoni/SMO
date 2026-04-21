package com.cutm.smo.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wiptracking")
public class WipTracking {
    @Id
    @Column(name = "wip_id")
    private Long wipId;
    
    @Column(name = "bin_id")
    private Long binId;
    
    @Column(name = "bundle_id")
    private Long bundleId;
    
    @Column(name = "operation_id")
    private Long operationId;
    
    @Column(name = "operator_id")
    private Long operatorId;
    
    @Column(name = "machine_id")
    private Long machineId;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "qty")
    private Integer qty;
    
    @Column(name = "status")
    private String status;

    public Long getWipId() { return wipId; }
    public void setWipId(Long wipId) { this.wipId = wipId; }
    public Long getBinId() { return binId; }
    public void setBinId(Long binId) { this.binId = binId; }
    public Long getBundleId() { return bundleId; }
    public void setBundleId(Long bundleId) { this.bundleId = bundleId; }
    public Long getOperationId() { return operationId; }
    public void setOperationId(Long operationId) { this.operationId = operationId; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public Long getMachineId() { return machineId; }
    public void setMachineId(Long machineId) { this.machineId = machineId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}