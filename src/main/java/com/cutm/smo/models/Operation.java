package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "operation")
public class Operation {
    @Id
    @Column(name = "operation_id") private Long operationId;
    @Column(name = "name") private String name;
    @Column(name = "description") private String description;
    @Column(name = "sequence") private Integer sequence;
    @Column(name = "is_parallel") private Boolean isParallel;
    @Column(name = "merge_point") private Boolean mergePoint;
    @Column(name = "stage_group") private Integer stageGroup;
    @Column(name = "standard_time") private Integer standardTime;

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
