package com.example.loyalty.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "customer_id")
    private Customer customer;
    @Column(nullable = false, unique = true)
    private String token;
    @Column(nullable = false)
    private OffsetDateTime expiresAt;
    protected RefreshToken() {}
    public RefreshToken(Customer customer, String token, OffsetDateTime expiresAt) { this.customer = customer; this.token = token; this.expiresAt = expiresAt; }
    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public String getToken() { return token; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public boolean isExpired() { return expiresAt.isBefore(OffsetDateTime.now()); }
}
