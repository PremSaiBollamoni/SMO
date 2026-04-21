package com.cutm.smo.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stockmovement")
public class StockMovement {
    @Id
    @Column(name = "movement_id")
    private Long movementId;
    
    @Column(name = "item_id")
    private Long itemId;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "qty")
    private Integer qty;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    public Long getMovementId() { return movementId; }
    public void setMovementId(Long movementId) { this.movementId = movementId; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}