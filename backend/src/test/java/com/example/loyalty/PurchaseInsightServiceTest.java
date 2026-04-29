package com.example.loyalty;

import com.example.loyalty.application.service.PurchaseInsightService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class PurchaseInsightServiceTest {
    private final PurchaseInsightService service = new PurchaseInsightService(null);

    @Test
    void ratesBuyerFromSpendFrequencyDiversityAndRecency() {
        var profile = new PurchaseInsightService.PurchaseProfile(
            List.of(new PurchaseInsightService.CategoryStat("BOOKS", 3, BigDecimal.valueOf(300), OffsetDateTime.now())),
            3,
            BigDecimal.valueOf(300),
            OffsetDateTime.now()
        );

        assertThat(service.buyerRating(profile)).isGreaterThan(20);
        assertThat(service.buyerRatingLabel(profile)).isIn("Start", "Silver", "Gold", "Platinum");
        assertThat(service.averageCheck(profile)).isEqualByComparingTo("100.00");
    }

    @Test
    void recommendsUnderdevelopedStrategicCategory() {
        var profile = new PurchaseInsightService.PurchaseProfile(
            List.of(
                new PurchaseInsightService.CategoryStat("BOOKS", 5, BigDecimal.valueOf(500), OffsetDateTime.now()),
                new PurchaseInsightService.CategoryStat("SPORT", 1, BigDecimal.valueOf(20), OffsetDateTime.now())
            ),
            6,
            BigDecimal.valueOf(520),
            OffsetDateTime.now()
        );

        assertThat(service.sharePercent(profile, profile.categories().get(1))).isEqualByComparingTo("3.85");
        assertThat(service.certificateReason(profile, "GROCERY")).contains("почти не покупали");
    }
}
