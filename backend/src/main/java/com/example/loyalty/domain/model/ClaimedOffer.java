package com.example.loyalty.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "claimed_offers")
public class ClaimedOffer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne(optional = false) @JoinColumn(name = "offer_id")
    private Offer offer;
    @Column(nullable = false)
    private OffsetDateTime claimedAt = OffsetDateTime.now();
    protected ClaimedOffer() {}
    public ClaimedOffer(Customer customer, Offer offer) { this.customer = customer; this.offer = offer; }
    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Offer getOffer() { return offer; }
    public OffsetDateTime getClaimedAt() { return claimedAt; }
}
