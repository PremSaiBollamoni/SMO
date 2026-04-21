package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "routingstep")
public class RoutingStep {
    @Id
    @Column(name = "routing_step_id") private Long routingStepId;
    @Column(name = "routing_id") private Long routingId;
    @Column(name = "operation_id") private Long operationId;
    @Column(name = "stage_group") private Integer stageGroup;

    public Long getRoutingStepId() { return routingStepId; }
    public void setRoutingStepId(Long routingStepId) { this.routingStepId = routingStepId; }
    public Long getRoutingId() { return routingId; }
    public void setRoutingId(Long routingId) { this.routingId = routingId; }
    public Long getOperationId() { return operationId; }
    public void setOperationId(Long operationId) { this.operationId = operationId; }
    public Integer getStageGroup() { return stageGroup; }
    public void setStageGroup(Integer stageGroup) { this.stageGroup = stageGroup; }
}
