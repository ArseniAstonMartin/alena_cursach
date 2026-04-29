package com.example.loyalty.application.service;

import com.example.loyalty.domain.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseInsightService {
    private final JdbcTemplate jdbcTemplate;

    public PurchaseInsightService(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public PurchaseProfile profile(Customer customer) {
        List<CategoryStat> categories = jdbcTemplate.query("""
            select p.category, count(*) as purchases_count, coalesce(sum(pi.quantity * pi.unit_price),0) as spent
            from purchases pu
            join purchase_items pi on pi.purchase_id = pu.id
            join products p on p.id = pi.product_id
            where pu.customer_id = ?
            group by p.category
            order by spent desc, purchases_count desc
            """, (rs, rowNum) -> new CategoryStat(rs.getString("category"), rs.getInt("purchases_count"), rs.getBigDecimal("spent")), customer.getId());
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
        return 12;
    }

    public String explanation(PurchaseProfile profile, String targetCategory) {
        Optional<CategoryStat> category = profile.categories().stream().filter(stat -> stat.category().equals(targetCategory)).findFirst();
        if (category.isPresent()) {
            return "Вы часто покупаете " + targetCategory + ": " + category.get().purchaseCount() + " покупок на " + category.get().spent().intValue() + " ₽. Поэтому оффер поднят выше.";
        }
        if (profile.purchaseCount() == 0) return "Истории покупок пока нет, поэтому это стартовое предложение для знакомства с категорией.";
        String top = profile.categories().isEmpty() ? "покупкам" : profile.categories().getFirst().category();
        return "В истории преобладает " + top + ", а " + targetCategory + " почти не покупалась. Это предложение расширяет интересы клиента.";
    }

    public record CategoryStat(String category, int purchaseCount, BigDecimal spent) {}
    public record PurchaseProfile(List<CategoryStat> categories, int purchaseCount, BigDecimal totalSpent, OffsetDateTime lastPurchaseAt) {}
}
