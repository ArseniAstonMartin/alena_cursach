package com.example.loyalty.application.service;

import com.example.loyalty.application.dto.LoyaltyDtos.*;
import com.example.loyalty.application.port.*;
import com.example.loyalty.domain.model.*;
import com.example.loyalty.domain.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LoyaltyFacade {
    private final CustomerQueryService customerQueryService; private final LoyaltyAccountRepository accounts; private final RewardTransactionRepository transactions; private final OfferRepository offers; private final ClaimedOfferRepository claimedOffers; private final OfferStrategyFactory strategies; private final JdbcTemplate jdbcTemplate;
    public LoyaltyFacade(CustomerQueryService customerQueryService, LoyaltyAccountRepository accounts, RewardTransactionRepository transactions, OfferRepository offers, ClaimedOfferRepository claimedOffers, OfferStrategyFactory strategies, JdbcTemplate jdbcTemplate) { this.customerQueryService = customerQueryService; this.accounts = accounts; this.transactions = transactions; this.offers = offers; this.claimedOffers = claimedOffers; this.strategies = strategies; this.jdbcTemplate = jdbcTemplate; }
    @Transactional(readOnly = true) public BalanceDto balance(String email) { Customer customer = customerQueryService.requireByEmail(email); LoyaltyAccount account = accounts.findByCustomer(customer).orElseThrow(() -> new NotFoundException("Loyalty account not found")); return new BalanceDto(customer.getId(), account.getPointsBalance(), customer.getSegment()); }
    @Transactional(readOnly = true) public List<OfferDto> personalizedOffers(String email) { Customer customer = customerQueryService.requireByEmail(email); return offers.findByStatus(OfferStatus.ACTIVE).stream().sorted(strategies.comparatorFor(customer)).map(offer -> new OfferDto(offer.getId(), offer.getTitle(), offer.getDescription(), offer.getTargetCategory(), offer.getBonusPoints(), offer.getValidUntil(), strategies.combinedScore(customer, offer))).toList(); }
    @Transactional public OfferDto claim(String email, Long offerId) { Customer customer = customerQueryService.requireByEmail(email); Offer offer = offers.findById(offerId).orElseThrow(() -> new NotFoundException("Offer not found")); if (claimedOffers.existsByCustomerAndOffer(customer, offer)) throw new BusinessRuleViolationException("Offer already claimed"); claimedOffers.save(new ClaimedOffer(customer, offer)); return new OfferDto(offer.getId(), offer.getTitle(), offer.getDescription(), offer.getTargetCategory(), offer.getBonusPoints(), offer.getValidUntil(), strategies.combinedScore(customer, offer)); }
    @Transactional public BalanceDto redeem(String email, int points) { Customer customer = customerQueryService.requireByEmail(email); LoyaltyAccount account = accounts.findByCustomer(customer).orElseThrow(() -> new NotFoundException("Loyalty account not found")); if (account.getPointsBalance() < points) throw new BusinessRuleViolationException("Not enough points"); account.redeem(points); transactions.save(new RewardTransaction(account, RewardTransactionType.REDEEM, -points, "Manual redemption")); return new BalanceDto(customer.getId(), account.getPointsBalance(), customer.getSegment()); }
    @Transactional public CustomerSegment recalculateSegment(String email) { Customer customer = customerQueryService.requireByEmail(email); java.math.BigDecimal total = jdbcTemplate.queryForObject("select coalesce(sum(total_amount),0) from purchases where customer_id = ?", java.math.BigDecimal.class, customer.getId()); int spent = total == null ? 0 : total.intValue(); customer.setSegment(spent > 700 ? CustomerSegment.PREMIUM : spent > 150 ? CustomerSegment.REGULAR : CustomerSegment.NEWCOMER); return customer.getSegment(); }
    @Transactional(readOnly = true) public Page<RewardTransactionDto> transactions(String email, Pageable pageable) { Customer customer = customerQueryService.requireByEmail(email); LoyaltyAccount account = accounts.findByCustomer(customer).orElseThrow(() -> new NotFoundException("Loyalty account not found")); return transactions.findByAccount(account, pageable).map(tx -> new RewardTransactionDto(tx.getId(), tx.getType(), tx.getPoints(), tx.getReason(), tx.getCreatedAt())); }
}
