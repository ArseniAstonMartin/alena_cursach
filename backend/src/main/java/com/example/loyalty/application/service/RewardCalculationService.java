package com.example.loyalty.application.service;

import com.example.loyalty.domain.model.Customer;
import com.example.loyalty.domain.model.CustomerSegment;
import com.example.loyalty.domain.model.Purchase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RewardCalculationService {
    private final JdbcTemplate jdbcTemplate;

    public RewardCalculationService(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public RewardBreakdown calculate(Customer customer, Purchase purchase) {
        int basePoints = purchase.getItems().stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                .multiply(cashbackRate(item.getProduct().getCategory()))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP))
            .mapToInt(BigDecimal::intValue)
            .sum();
        BigDecimal multiplier = segmentMultiplier(customer.getSegment());
        int offerBonus = claimedOfferBonus(customer.getId(), purchase.getId());
        int finalPoints = BigDecimal.valueOf(basePoints).multiply(multiplier).setScale(0, RoundingMode.HALF_UP).intValue() + offerBonus;
        finalPoints = Math.max(finalPoints, purchase.getTotalAmount().compareTo(BigDecimal.ZERO) > 0 ? 1 : 0);
        String explanation = "Base " + basePoints + " pts x segment " + multiplier + " + activated offer " + offerBonus + " pts";
        return new RewardBreakdown(basePoints, multiplier, offerBonus, finalPoints, explanation);
    }

    private BigDecimal cashbackRate(String category) {
        return jdbcTemplate.query("select cashback_percent from reward_rules where category = ? and active = true", rs -> rs.next() ? rs.getBigDecimal(1) : BigDecimal.valueOf(5), category);
    }

    private BigDecimal segmentMultiplier(CustomerSegment segment) {
        return jdbcTemplate.query("select multiplier from customer_segment_thresholds where segment = ?", rs -> rs.next() ? rs.getBigDecimal(1) : BigDecimal.ONE, segment.name());
    }

    private int claimedOfferBonus(Long customerId, Long purchaseId) {
        Integer bonus = jdbcTemplate.queryForObject("""
            select coalesce(max(o.bonus_points), 0)
            from claimed_offers co
            join offers o on o.id = co.offer_id
            join purchase_items pi on pi.purchase_id = ?
            join products p on p.id = pi.product_id
            where co.customer_id = ?
              and co.status = 'CLAIMED'
              and o.status = 'ACTIVE'
              and o.valid_until >= current_date
              and o.target_category = p.category
            """, Integer.class, purchaseId, customerId);
        return bonus == null ? 0 : bonus;
    }

    public void saveBreakdown(Long purchaseId, RewardBreakdown breakdown) {
        jdbcTemplate.update("""
            insert into purchase_reward_breakdowns(purchase_id, base_points, segment_multiplier, offer_bonus_points, final_points, explanation)
            values (?, ?, ?, ?, ?, ?)
            on conflict (purchase_id) do nothing
            """, purchaseId, breakdown.basePoints(), breakdown.segmentMultiplier(), breakdown.offerBonusPoints(), breakdown.finalPoints(), breakdown.explanation());
        if (breakdown.offerBonusPoints() > 0) {
            jdbcTemplate.update("""
                update claimed_offers co
                set status = 'USED', used_at = now()
                from offers o, purchase_items pi, products p
                where co.offer_id = o.id
                  and pi.product_id = p.id
                  and pi.purchase_id = ?
                  and co.customer_id = (select customer_id from purchases where id = ?)
                  and co.status = 'CLAIMED'
                  and o.target_category = p.category
                """, purchaseId, purchaseId);
        }
    }

    public record RewardBreakdown(int basePoints, BigDecimal segmentMultiplier, int offerBonusPoints, int finalPoints, String explanation) {}
}
