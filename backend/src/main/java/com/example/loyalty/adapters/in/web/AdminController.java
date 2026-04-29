package com.example.loyalty.adapters.in.web;

import com.example.loyalty.domain.model.CustomerSegment;
import com.example.loyalty.domain.service.BusinessRuleViolationException;
import com.example.loyalty.infrastructure.config.IntegrationRegistry;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final IntegrationRegistry integrationRegistry;
    private final JdbcTemplate jdbcTemplate;

    public AdminController(IntegrationRegistry integrationRegistry, JdbcTemplate jdbcTemplate) {
        this.integrationRegistry = integrationRegistry;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/integrations") public Map<String, String> integrations() { return integrationRegistry.registeredIntegrations(); }

    @GetMapping("/stats")
    public AdminStatsDto stats() {
        Integer customers = jdbcTemplate.queryForObject("select count(*) from customers", Integer.class);
        Integer products = jdbcTemplate.queryForObject("select count(*) from products", Integer.class);
        Integer purchases = jdbcTemplate.queryForObject("select count(*) from purchases", Integer.class);
        BigDecimal revenue = jdbcTemplate.queryForObject("select coalesce(sum(total_amount),0) from purchases", BigDecimal.class);
        Integer activeCertificates = jdbcTemplate.queryForObject("select count(*) from redemption_orders where status = 'ACTIVE'", Integer.class);
        Integer activeOffers = jdbcTemplate.queryForObject("select count(*) from offers where status = 'ACTIVE'", Integer.class);
        return new AdminStatsDto(customers, products, purchases, revenue, activeCertificates, activeOffers);
    }

    @GetMapping("/users")
    public List<AdminUserDto> users() {
        return jdbcTemplate.query("""
            select c.id, c.full_name, c.email, c.segment, c.created_at, coalesce(la.points_balance,0) as points,
                   coalesce(count(distinct p.id),0) as purchases_count,
                   coalesce(sum(p.total_amount),0) as total_spent
            from customers c
            left join loyalty_accounts la on la.customer_id = c.id
            left join purchases p on p.customer_id = c.id
            group by c.id, c.full_name, c.email, c.segment, c.created_at, la.points_balance
            order by c.created_at desc
            """, (rs, rowNum) -> new AdminUserDto(rs.getLong("id"), rs.getString("full_name"), rs.getString("email"), CustomerSegment.valueOf(rs.getString("segment")), rs.getInt("points"), rs.getInt("purchases_count"), rs.getBigDecimal("total_spent"), rs.getObject("created_at", OffsetDateTime.class)));
    }

    @PatchMapping("/users/{id}/segment")
    public void updateSegment(@PathVariable Long id, @Valid @RequestBody SegmentRequest request) {
        jdbcTemplate.update("update customers set segment = ? where id = ?", request.segment().name(), id);
    }

    @PostMapping("/users/{id}/points")
    public void adjustPoints(@PathVariable Long id, @Valid @RequestBody PointsAdjustmentRequest request) {
        Long accountId = jdbcTemplate.query("select id from loyalty_accounts where customer_id = ?", rs -> rs.next() ? rs.getLong("id") : null, id);
        if (accountId == null) throw new BusinessRuleViolationException("Customer has no loyalty account");
        jdbcTemplate.update("update loyalty_accounts set points_balance = greatest(0, points_balance + ?) where id = ?", request.points(), accountId);
        jdbcTemplate.update("insert into reward_transactions(account_id, type, points, reason) values (?, 'ADJUSTMENT', ?, ?)", accountId, request.points(), request.reason());
    }

    @GetMapping("/merchants")
    public List<AdminMerchantDto> merchants() {
        return jdbcTemplate.query("select id, name, category from merchants order by name", (rs, rowNum) -> new AdminMerchantDto(rs.getLong("id"), rs.getString("name"), rs.getString("category")));
    }

    @GetMapping("/products")
    public List<AdminProductDto> products() {
        return jdbcTemplate.query("""
            select p.id, p.merchant_id, m.name as merchant_name, p.name, p.category, p.price
            from products p join merchants m on m.id = p.merchant_id
            order by p.id desc
            """, (rs, rowNum) -> productDto(rs.getLong("id"), rs.getLong("merchant_id"), rs.getString("merchant_name"), rs.getString("name"), rs.getString("category"), rs.getBigDecimal("price")));
    }

    @PostMapping("/products")
    public AdminProductDto createProduct(@Valid @RequestBody ProductRequest request) {
        Long id = jdbcTemplate.queryForObject("insert into products(merchant_id, name, category, price) values (?, ?, ?, ?) returning id", Long.class, request.merchantId(), request.name(), request.category(), request.price());
        return productById(id);
    }

    @PutMapping("/products/{id}")
    public AdminProductDto updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        int updated = jdbcTemplate.update("update products set merchant_id = ?, name = ?, category = ?, price = ? where id = ?", request.merchantId(), request.name(), request.category(), request.price(), id);
        if (updated == 0) throw new BusinessRuleViolationException("Product not found");
        return productById(id);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Long id) {
        int deleted = jdbcTemplate.update("delete from products where id = ? and not exists (select 1 from purchase_items where product_id = ?)", id, id);
        if (deleted == 0) throw new BusinessRuleViolationException("Product is used in purchases or does not exist");
    }

    private AdminProductDto productById(Long id) {
        return jdbcTemplate.queryForObject("""
            select p.id, p.merchant_id, m.name as merchant_name, p.name, p.category, p.price
            from products p join merchants m on m.id = p.merchant_id where p.id = ?
            """, (rs, rowNum) -> productDto(rs.getLong("id"), rs.getLong("merchant_id"), rs.getString("merchant_name"), rs.getString("name"), rs.getString("category"), rs.getBigDecimal("price")), id);
    }

    private AdminProductDto productDto(Long id, Long merchantId, String merchantName, String name, String category, BigDecimal price) {
        return new AdminProductDto(id, merchantId, merchantName, name, category, price);
    }

    public record AdminStatsDto(Integer customers, Integer products, Integer purchases, BigDecimal revenue, Integer activeCertificates, Integer activeOffers) {}
    public record AdminUserDto(Long id, String fullName, String email, CustomerSegment segment, int pointsBalance, int purchasesCount, BigDecimal totalSpent, OffsetDateTime createdAt) {}
    public record AdminMerchantDto(Long id, String name, String category) {}
    public record AdminProductDto(Long id, Long merchantId, String merchantName, String name, String category, BigDecimal price) {}
    public record ProductRequest(@NotNull Long merchantId, @NotBlank String name, @NotBlank String category, @NotNull @Min(0) BigDecimal price) {}
    public record SegmentRequest(@NotNull CustomerSegment segment) {}
    public record PointsAdjustmentRequest(int points, @NotBlank String reason) {}
}
