package com.example.loyalty.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "loyalty_accounts")
public class LoyaltyAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false) @JoinColumn(name = "customer_id", unique = true)
    private Customer customer;
    @Column(nullable = false)
    private int pointsBalance;
    protected LoyaltyAccount() {}
    public LoyaltyAccount(Customer customer) { this.customer = customer; }
    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public int getPointsBalance() { return pointsBalance; }
    public void earn(int points) { pointsBalance += points; }
    public void redeem(int points) { pointsBalance -= points; }
}
