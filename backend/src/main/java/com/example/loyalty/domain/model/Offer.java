package com.example.loyalty.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "offers")
public class Offer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, length = 1000)
    private String description;
    @Column(nullable = false)
    private String targetCategory;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private CustomerSegment targetSegment;
    @Column(nullable = false)
    private int bonusPoints;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private OfferStatus status = OfferStatus.ACTIVE;
    @Column(nullable = false)
    private LocalDate validUntil;
    protected Offer() {}
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTargetCategory() { return targetCategory; }
    public CustomerSegment getTargetSegment() { return targetSegment; }
    public int getBonusPoints() { return bonusPoints; }
    public OfferStatus getStatus() { return status; }
    public LocalDate getValidUntil() { return validUntil; }
}
