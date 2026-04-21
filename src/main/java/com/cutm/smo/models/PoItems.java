package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "poitems")
public class PoItems {
    @Id
    @Column(name = "po_item_id")
    private Long poItemId;
    
    @Column(name = "po_id")
    private Long poId;
    
    @Column(name = "item_id")
    private Long itemId;
    
    @Column(name = "style_variant_id")
    private Long styleVariantId;
    
    @Column(name = "qty")
    private Integer qty;

    public Long getPoItemId() { return poItemId; }
    public void setPoItemId(Long poItemId) { this.poItemId = poItemId; }
    public Long getPoId() { return poId; }
    public void setPoId(Long poId) { this.poId = poId; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getStyleVariantId() { return styleVariantId; }
    public void setStyleVariantId(Long styleVariantId) { this.styleVariantId = styleVariantId; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
}