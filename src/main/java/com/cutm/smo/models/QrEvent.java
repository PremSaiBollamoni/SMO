package com.cutm.smo.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_event")
public class QrEvent {
    @Id
    @Column(name = "event_id")
    private Long eventId;
    
    @Column(name = "entity_type")
    private String entityType;
    
    @Column(name = "entity_id")
    private Long entityId;
    
    @Column(name = "qr_code")
    private String qrCode;
    
    @Column(name = "style_id")
    private Long styleId;
    
    @Column(name = "operation_id")
    private Long operationId;
    
    @Column(name = "machine_id")
    private Long machineId;
    
    @Column(name = "operator_id")
    private Long operatorId;
    
    @Column(name = "event_type")
    private String eventType;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public Long getStyleId() { return styleId; }
    public void setStyleId(Long styleId) { this.styleId = styleId; }
    public Long getOperationId() { return operationId; }
    public void setOperationId(Long operationId) { this.operationId = operationId; }
    public Long getMachineId() { return machineId; }
    public void setMachineId(Long machineId) { this.machineId = machineId; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}