package com.cutm.smo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class ProcessPlanResponse {
    @JsonProperty("routing_id")
    private Long routingId;
    @JsonProperty("product_id")
    private Long productId;
    private Integer version;
    private String status;
    @JsonProperty("approval_status")
    private String approvalStatus;
    @JsonProperty("approved_by")
    private Long approvedBy;
    @JsonProperty("approved_at")
    private LocalDateTime approvedAt;
    @JsonProperty("previous_routing_id")
    private Long previousRoutingId;
    private List<OperationResponse> operations;

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
    public List<OperationResponse> getOperations() { return operations; }
    public void setOperations(List<OperationResponse> operations) { this.operations = operations; }

    public static class OperationResponse {
        @JsonProperty("operation_id")
        private Long operationId;
        private String name;
        private String description;
        private Integer sequence;
        @JsonProperty("is_parallel")
        private Boolean isParallel;
        @JsonProperty("merge_point")
        private Boolean mergePoint;
        @JsonProperty("stage_group")
        private Integer stageGroup;
        @JsonProperty("standard_time")
        private Integer standardTime;

        public Long getOperationId() { return operationId; }
        public void setOperationId(Long operationId) { this.operationId = operationId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getSequence() { return sequence; }
        public void setSequence(Integer sequence) { this.sequence = sequence; }
        public Boolean getIsParallel() { return isParallel; }
        public void setIsParallel(Boolean isParallel) { this.isParallel = isParallel; }
        public Boolean getMergePoint() { return mergePoint; }
        public void setMergePoint(Boolean mergePoint) { this.mergePoint = mergePoint; }
        public Integer getStageGroup() { return stageGroup; }
        public void setStageGroup(Integer stageGroup) { this.stageGroup = stageGroup; }
        public Integer getStandardTime() { return standardTime; }
        public void setStandardTime(Integer standardTime) { this.standardTime = standardTime; }
    }
}
