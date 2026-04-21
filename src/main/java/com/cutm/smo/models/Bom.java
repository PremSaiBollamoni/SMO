package com.cutm.smo.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bom")
public class Bom {
    @Id
    @Column(name = "bom_id")
    private Long bomId;
    
    @Column(name = "style_id")
    private Long styleId;
    
    @Column(name = "style_variant_id")
    private Long styleVariantId;
    
    @Column(name = "item_id")
    private Long itemId;
    
    @Column(name = "qty")
    private BigDecimal qty;

    public Long getBomId() { return bomId; }
    public void setBomId(Long bomId) { this.bomId = bomId; }
    public Long getStyleId() { return styleId; }
    public void setStyleId(Long styleId) { this.styleId = styleId; }
    public Long getStyleVariantId() { return styleVariantId; }
    public void setStyleVariantId(Long styleVariantId) { this.styleVariantId = styleVariantId; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
}