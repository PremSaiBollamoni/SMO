package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "product_id") private Long productId;
    @Column(name = "name") private String name;
    @Column(name = "category") private String category;
    @Column(name = "status") private String status;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
