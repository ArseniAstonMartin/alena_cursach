package com.example.loyalty.application.dto;

import com.example.loyalty.domain.model.CustomerSegment;
import com.example.loyalty.domain.model.RewardTransactionType;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public final class LoyaltyDtos {
    private LoyaltyDtos() {}
    public record BalanceDto(Long customerId, int pointsBalance, CustomerSegment segment) {}
    public record RewardTransactionDto(Long id, RewardTransactionType type, int points, String reason, OffsetDateTime createdAt) {}
    public record OfferDto(Long id, String title, String description, String targetCategory, int bonusPoints, LocalDate validUntil, int personalizationScore) {}
    public record RecommendationDto(String title, String description, String source) {}
}
