package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "grnitems")
public class GrnItems {
    @Id
    @Column(name = "grn_item_id")
    private Long grnItemId;
    
    @Column(name = "grn_id")
    private Long grnId;
    
    @Column(name = "item_id")
    private Long itemId;
    
    @Column(name = "style_variant_id")
    private Long styleVariantId;
    
    @Column(name = "qty")
    private Integer qty;

    public Long getGrnItemId() { return grnItemId; }
    public void setGrnItemId(Long grnItemId) { this.grnItemId = grnItemId; }
    public Long getGrnId() { return grnId; }
    public void setGrnId(Long grnId) { this.grnId = grnId; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getStyleVariantId() { return styleVariantId; }
    public void setStyleVariantId(Long styleVariantId) { this.styleVariantId = styleVariantId; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
}