package com.example.loyalty.application.dto;

import com.example.loyalty.domain.model.CustomerSegment;
import com.example.loyalty.domain.model.RewardTransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public final class LoyaltyDtos {
    private LoyaltyDtos() {}
    public record BalanceDto(Long customerId, int pointsBalance, CustomerSegment segment) {}
    public record RewardTransactionDto(Long id, RewardTransactionType type, int points, String reason, OffsetDateTime createdAt) {}
    public record OfferDto(Long id, String title, String description, String targetCategory, int bonusPoints, LocalDate validUntil, int personalizationScore, String personalizationReason, String status, String nextStep, String missionType, BigDecimal baselineValue, BigDecimal targetValue, BigDecimal currentValue, int progressPercent, int requiredRating, String businessGoal) {}
    public record RecommendationDto(String title, String description, String source) {}
    public record RedemptionRequest(@Min(50) int points, @NotBlank String rewardName, String targetCategory) {}
    public record RedemptionDto(int pointsSpent, BigDecimal discountAmount, int remainingBalance, String confirmationCode, String rewardName, String targetCategory, String personalizationReason) {}
    public record CertificateDto(Long id, int pointsSpent, BigDecimal discountAmount, String rewardName, String confirmationCode, String targetCategory, String status, OffsetDateTime createdAt, OffsetDateTime expiresAt, OffsetDateTime usedAt, Long purchaseId) {}
    public record RewardRuleDto(String category, BigDecimal cashbackPercent, String description) {}
    public record SegmentRuleDto(CustomerSegment segment, BigDecimal minTotalSpend, BigDecimal multiplier, String description) {}
    public record LoyaltyProgramDto(List<RewardRuleDto> rewardRules, List<SegmentRuleDto> segmentRules, String redemptionRule) {}
    public record CategoryInsightDto(String category, int purchaseCount, BigDecimal spent, BigDecimal sharePercent, String lastPurchaseAt, BigDecimal averageCheck) {}
    public record PurchaseProfileDto(List<CategoryInsightDto> favoriteCategories, int purchaseCount, BigDecimal totalSpent, String lastPurchaseAt, BigDecimal averageCheck, String purchaseFrequencyLabel, int buyerRating, String buyerRatingLabel) {}
}
