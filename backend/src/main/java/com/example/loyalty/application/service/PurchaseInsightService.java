package com.example.loyalty.application.service;

import com.example.loyalty.domain.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseInsightService {
    private final JdbcTemplate jdbcTemplate;

    public PurchaseInsightService(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public PurchaseProfile profile(Customer customer) {
        List<CategoryStat> categories = jdbcTemplate.query("""
            select p.category,
                   count(*) as purchases_count,
                   coalesce(sum(pi.quantity * pi.unit_price),0) as spent,
                   max(pu.purchased_at) as last_purchase_at
            from purchases pu
            join purchase_items pi on pi.purchase_id = pu.id
            join products p on p.id = pi.product_id
            where pu.customer_id = ?
            group by p.category
            order by spent desc, purchases_count desc
            """, (rs, rowNum) -> new CategoryStat(rs.getString("category"), rs.getInt("purchases_count"), rs.getBigDecimal("spent"), rs.getObject("last_purchase_at", OffsetDateTime.class)), customer.getId());
        Integer purchaseCount = jdbcTemplate.queryForObject("select count(*) from purchases where customer_id = ?", Integer.class, customer.getId());
        BigDecimal totalSpent = jdbcTemplate.queryForObject("select coalesce(sum(total_amount),0) from purchases where customer_id = ?", BigDecimal.class, customer.getId());
        OffsetDateTime lastPurchaseAt = jdbcTemplate.query("select max(purchased_at) from purchases where customer_id = ?", rs -> rs.next() ? rs.getObject(1, OffsetDateTime.class) : null, customer.getId());
        return new PurchaseProfile(categories, purchaseCount == null ? 0 : purchaseCount, totalSpent == null ? BigDecimal.ZERO : totalSpent, lastPurchaseAt);
    }

    public int historyScore(PurchaseProfile profile, String targetCategory) {
        Optional<CategoryStat> category = profile.categories().stream().filter(stat -> stat.category().equals(targetCategory)).findFirst();
        if (category.isPresent()) {
            int loyalty = Math.min(45, category.get().spent().intValue() / 8 + category.get().purchaseCount() * 6);
            return 30 + loyalty;
        }
        if (profile.purchaseCount() == 0) return 20;
        return 18;
    }

    public String explanation(PurchaseProfile profile, String targetCategory) {
        Optional<CategoryStat> category = profile.categories().stream().filter(stat -> stat.category().equals(targetCategory)).findFirst();
        if (category.isPresent()) {
            return "В истории есть интерес к " + targetCategory + ": " + category.get().purchaseCount() + " покупок на " + category.get().spent().intValue() + " ₽. Оффер усиливает категорию с подтвержденным спросом.";
        }
        if (profile.purchaseCount() == 0) return "Истории покупок пока нет, поэтому это стартовое предложение для знакомства с категорией.";
        String top = profile.categories().isEmpty() ? "покупкам" : profile.categories().getFirst().category();
        return "В истории преобладает " + top + ", а " + targetCategory + " почти не покупалась. Оффер помогает бизнесу развивать новую категорию для клиента.";
    }

    public String recommendedCertificateCategory(PurchaseProfile profile) {
        if (profile.purchaseCount() == 0) return "ANY";
        return jdbcTemplate.query("""
            select pc.code
            from product_categories pc
            left join (
                select p.category, coalesce(sum(pi.quantity * pi.unit_price),0) as spent
                from purchases pu
                join purchase_items pi on pi.purchase_id = pu.id
                join products p on p.id = pi.product_id
                where pu.customer_id in (select id from customers order by id limit 100000)
                group by p.category
            ) global_stats on global_stats.category = pc.code
            where pc.active = true
            order by (
                select coalesce(sum(stat.spent),0)
                from (values %s) as stat(category, spent)
                where stat.category = pc.code
            ) asc, pc.strategic_priority desc, coalesce(global_stats.spent,0) desc
            limit 1
            """.formatted(categoryValues(profile)), rs -> rs.next() ? rs.getString("code") : "ANY");
    }

    private String categoryValues(PurchaseProfile profile) {
        if (profile.categories().isEmpty()) return "('NONE', 0::numeric)";
        return profile.categories().stream()
            .map(stat -> "('" + stat.category().replace("'", "''") + "', " + stat.spent() + "::numeric)")
            .reduce((left, right) -> left + "," + right)
            .orElse("('NONE', 0::numeric)");
    }

    public String certificateReason(PurchaseProfile profile, String category) {
        if ("ANY".equals(category)) return "Сертификат универсальный, потому что истории покупок пока недостаточно.";
        Optional<CategoryStat> stat = profile.categories().stream().filter(item -> item.category().equals(category)).findFirst();
        if (stat.isPresent()) {
            return "Сертификат выдан на " + category + ": это категория с меньшей долей в вашей истории (" + stat.get().spent().intValue() + " ₽), поэтому бизнес стимулирует следующий интерес.";
        }
        return "Сертификат выдан на " + category + ", потому что вы почти не покупали эту категорию. Это персональная рекомендация расширить покупки.";
    }

    public int buyerRating(PurchaseProfile profile) {
        int spendScore = Math.min(45, profile.totalSpent().intValue() / 20);
        int frequencyScore = Math.min(25, profile.purchaseCount() * 4);
        int diversityScore = Math.min(20, profile.categories().size() * 7);
        int recencyScore = profile.lastPurchaseAt() == null ? 0 : Math.max(0, 10 - (int) Math.min(10, Duration.between(profile.lastPurchaseAt(), OffsetDateTime.now()).toDays()));
        return Math.min(100, spendScore + frequencyScore + diversityScore + recencyScore);
    }

    public String buyerRatingLabel(PurchaseProfile profile) {
        int rating = buyerRating(profile);
        if (rating >= 80) return "Platinum";
        if (rating >= 55) return "Gold";
        if (rating >= 30) return "Silver";
        return "Start";
    }

    public BigDecimal averageCheck(PurchaseProfile profile) {
        if (profile.purchaseCount() == 0) return BigDecimal.ZERO;
        return profile.totalSpent().divide(BigDecimal.valueOf(profile.purchaseCount()), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal sharePercent(PurchaseProfile profile, CategoryStat category) {
        if (profile.totalSpent().compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return category.spent().multiply(BigDecimal.valueOf(100)).divide(profile.totalSpent(), 2, RoundingMode.HALF_UP);
    }

    public String frequencyLabel(PurchaseProfile profile) {
        if (profile.purchaseCount() == 0 || profile.lastPurchaseAt() == null) return "нет истории";
        long days = Math.max(1, Duration.between(profile.lastPurchaseAt(), OffsetDateTime.now()).toDays());
        if (days <= 3) return "покупает недавно";
        if (days <= 14) return "активный клиент";
        if (days <= 45) return "умеренная активность";
        return "риск оттока";
    }

    public BigDecimal categoryAverageCheck(CategoryStat category) {
        if (category.purchaseCount() == 0) return BigDecimal.ZERO;
        return category.spent().divide(BigDecimal.valueOf(category.purchaseCount()), 2, RoundingMode.HALF_UP);
    }

    public record CategoryStat(String category, int purchaseCount, BigDecimal spent, OffsetDateTime lastPurchaseAt) {}
    public record PurchaseProfile(List<CategoryStat> categories, int purchaseCount, BigDecimal totalSpent, OffsetDateTime lastPurchaseAt) {}
}
