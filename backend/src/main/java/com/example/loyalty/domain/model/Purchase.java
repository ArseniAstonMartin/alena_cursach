package com.example.loyalty.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchases")
public class Purchase {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne(optional = false) @JoinColumn(name = "merchant_id")
    private Merchant merchant;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private PurchaseStatus status = PurchaseStatus.PAID;
    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @Column(nullable = false)
    private OffsetDateTime purchasedAt = OffsetDateTime.now();
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> items = new ArrayList<>();
    protected Purchase() {}
    public Purchase(Customer customer, Merchant merchant) { this.customer = customer; this.merchant = merchant; }
    public void addItem(Product product, int quantity, BigDecimal unitPrice) { items.add(new PurchaseItem(this, product, quantity, unitPrice)); totalAmount = totalAmount.add(unitPrice.multiply(BigDecimal.valueOf(quantity))); }
    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Merchant getMerchant() { return merchant; }
    public PurchaseStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public OffsetDateTime getPurchasedAt() { return purchasedAt; }
    public List<PurchaseItem> getItems() { return items; }
}
