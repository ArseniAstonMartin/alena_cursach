package com.example.loyalty.application.service;

import com.example.loyalty.application.dto.PurchaseDtos.*;
import com.example.loyalty.application.port.*;
import com.example.loyalty.domain.model.*;
import com.example.loyalty.domain.service.BusinessRuleViolationException;
import com.example.loyalty.domain.service.NotFoundException;
import com.example.loyalty.infrastructure.events.PurchaseCompletedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
public class PurchaseService {
    private final CustomerQueryService customerQueryService; private final MerchantRepository merchants; private final ProductRepository products; private final PurchaseRepository purchases; private final ApplicationEventPublisher events; private final JdbcTemplate jdbcTemplate;
    public PurchaseService(CustomerQueryService customerQueryService, MerchantRepository merchants, ProductRepository products, PurchaseRepository purchases, ApplicationEventPublisher events, JdbcTemplate jdbcTemplate) { this.customerQueryService = customerQueryService; this.merchants = merchants; this.products = products; this.purchases = purchases; this.events = events; this.jdbcTemplate = jdbcTemplate; }
    @Transactional public PurchaseDto create(String email, CreatePurchaseRequest request) { Customer customer = customerQueryService.requireByEmail(email); Merchant merchant = merchants.findById(request.merchantId()).orElseThrow(() -> new NotFoundException("Merchant not found")); Purchase purchase = new Purchase(customer, merchant); request.items().forEach(item -> { Product product = products.findById(item.productId()).orElseThrow(() -> new NotFoundException("Product not found")); purchase.addItem(product, item.quantity(), product.getPrice()); }); if (request.certificateId() != null) applyCertificate(customer, purchase, request.certificateId()); Purchase saved = purchases.save(purchase); markCertificatePurchase(saved); events.publishEvent(new PurchaseCompletedEvent(saved.getId(), customer.getId(), saved.getTotalAmount())); return toDto(saved); }
    @Transactional(readOnly = true) public Page<PurchaseDto> history(String email, Pageable pageable) { Customer customer = customerQueryService.requireByEmail(email); return purchases.findByCustomer(customer, pageable).map(this::toDto); }

    private void applyCertificate(Customer customer, Purchase purchase, Long certificateId) {
        String categories = purchase.getItems().stream().map(item -> item.getProduct().getCategory()).collect(Collectors.joining(","));
        Certificate certificate = jdbcTemplate.query("""
            select id, discount_amount, confirmation_code, target_category
            from redemption_orders
            where id = ?
              and customer_id = ?
              and status = 'ACTIVE'
              and expires_at > now()
            """, rs -> rs.next() ? new Certificate(rs.getLong("id"), rs.getBigDecimal("discount_amount"), rs.getString("confirmation_code"), rs.getString("target_category")) : null, certificateId, customer.getId());
        if (certificate == null) throw new BusinessRuleViolationException("Certificate is not active or does not belong to customer");
        if (!certificate.targetCategory().equals("ANY") && !categories.contains(certificate.targetCategory())) throw new BusinessRuleViolationException("Certificate can only be applied to " + certificate.targetCategory());
        purchase.applyCertificate(certificate.id(), certificate.discountAmount());
        jdbcTemplate.update("update redemption_orders set status = 'USED', used_at = now() where id = ?", certificate.id());
    }

    private void markCertificatePurchase(Purchase purchase) {
        if (purchase.getCertificateId() != null) {
            jdbcTemplate.update("update redemption_orders set purchase_id = ? where id = ?", purchase.getId(), purchase.getCertificateId());
        }
    }

    private PurchaseDto toDto(Purchase purchase) {
        RewardInfo reward = rewardInfo(purchase.getId());
        CertificateInfo certificate = certificateInfo(purchase.getCertificateId());
        return new PurchaseDto(purchase.getId(), purchase.getCustomer().getId(), purchase.getMerchant().getId(), purchase.getMerchant().getName(), purchase.getGrossAmount(), purchase.getDiscountAmount(), purchase.getTotalAmount(), purchase.getCertificateId(), certificate == null ? null : certificate.code(), reward.points(), reward.explanation(), purchase.getPurchasedAt(), purchase.getItems().stream().map(item -> new PurchaseItemDto(item.getProduct().getId(), item.getProduct().getName(), item.getQuantity(), item.getUnitPrice())).collect(Collectors.toList()));
    }

    private RewardInfo rewardInfo(Long purchaseId) {
        return jdbcTemplate.query("select final_points, explanation from purchase_reward_breakdowns where purchase_id = ?", rs -> rs.next() ? new RewardInfo(rs.getInt("final_points"), rs.getString("explanation")) : new RewardInfo(0, null), purchaseId);
    }

    private CertificateInfo certificateInfo(Long certificateId) {
        if (certificateId == null) return null;
        return jdbcTemplate.query("select confirmation_code from redemption_orders where id = ?", rs -> rs.next() ? new CertificateInfo(rs.getString("confirmation_code")) : null, certificateId);
    }

    private record Certificate(Long id, BigDecimal discountAmount, String code, String targetCategory) {}
    private record CertificateInfo(String code) {}
    private record RewardInfo(int points, String explanation) {}
}
