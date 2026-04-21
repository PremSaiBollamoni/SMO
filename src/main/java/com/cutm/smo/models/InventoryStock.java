package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "inventorystock")
public class InventoryStock {
    @Id
    @Column(name = "stock_id")
    private Long stockId;
    
    @Column(name = "item_id")
    private Long itemId;
    
    @Column(name = "style_variant_id")
    private Long styleVariantId;
    
    @Column(name = "qty")
    private Integer qty;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "batch")
    private String batch;

    public Long getStockId() { return stockId; }
    public void setStockId(Long stockId) { this.stockId = stockId; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getStyleVariantId() { return styleVariantId; }
    public void setStyleVariantId(Long styleVariantId) { this.styleVariantId = styleVariantId; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }
}