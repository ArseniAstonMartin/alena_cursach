package com.example.loyalty.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "reward_transactions")
public class RewardTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "account_id")
    private LoyaltyAccount account;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private RewardTransactionType type;
    @Column(nullable = false)
    private int points;
    @Column(nullable = false)
    private String reason;
    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
    protected RewardTransaction() {}
    public RewardTransaction(LoyaltyAccount account, RewardTransactionType type, int points, String reason) { this.account = account; this.type = type; this.points = points; this.reason = reason; }
    public Long getId() { return id; }
    public RewardTransactionType getType() { return type; }
    public int getPoints() { return points; }
    public String getReason() { return reason; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
