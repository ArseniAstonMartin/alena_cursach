package com.example.loyalty.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "purchase_items")
public class PurchaseItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "purchase_id")
    private Purchase purchase;
    @ManyToOne(optional = false) @JoinColumn(name = "product_id")
    private Product product;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private BigDecimal unitPrice;
    protected PurchaseItem() {}
    public PurchaseItem(Purchase purchase, Product product, int quantity, BigDecimal unitPrice) { this.purchase = purchase; this.product = product; this.quantity = quantity; this.unitPrice = unitPrice; }
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
