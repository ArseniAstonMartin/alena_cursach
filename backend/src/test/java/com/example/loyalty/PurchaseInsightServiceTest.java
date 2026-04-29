package com.example.loyalty;

import com.example.loyalty.application.service.PurchaseInsightService;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    void explainsKnownAndUnknownCategories() {
        var profile = new PurchaseInsightService.PurchaseProfile(
            List.of(new PurchaseInsightService.CategoryStat("BOOKS", 2, BigDecimal.valueOf(180), OffsetDateTime.now().minusDays(2))),
            2,
            BigDecimal.valueOf(180),
            OffsetDateTime.now().minusDays(2)
        );

        assertThat(service.explanation(profile, "BOOKS")).contains("BOOKS");
        assertThat(service.explanation(profile, "SPORT")).contains("развивать новую категорию");
        assertThat(service.frequencyLabel(profile)).isEqualTo("покупает недавно");
    }

    @Test
    void recommendsCategoryUsingStrategicPriorityAndSpend() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        PurchaseInsightService serviceWithJdbc = new PurchaseInsightService(jdbcTemplate);
        when(jdbcTemplate.query(anyString(), any(ResultSetExtractor.class))).thenReturn("BOOKS");
        var profile = new PurchaseInsightService.PurchaseProfile(
            List.of(
                new PurchaseInsightService.CategoryStat("GROCERY", 4, BigDecimal.valueOf(400), OffsetDateTime.now()),
                new PurchaseInsightService.CategoryStat("SPORT", 1, BigDecimal.valueOf(40), OffsetDateTime.now())
            ),
            5,
            BigDecimal.valueOf(440),
            OffsetDateTime.now()
        );

        assertThat(serviceWithJdbc.recommendedCertificateCategory(profile)).isEqualTo("BOOKS");
    }
}
