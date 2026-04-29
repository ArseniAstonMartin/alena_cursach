package com.example.loyalty.infrastructure.events;

import com.example.loyalty.application.port.*;
import com.example.loyalty.application.service.RewardCalculationService;
import com.example.loyalty.domain.model.*;
import com.example.loyalty.domain.service.NotFoundException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PurchaseRewardListener {
    private final CustomerRepository customers; private final LoyaltyAccountRepository accounts; private final RewardTransactionRepository transactions; private final PurchaseRepository purchases; private final RewardCalculationService rewardCalculationService;
    public PurchaseRewardListener(CustomerRepository customers, LoyaltyAccountRepository accounts, RewardTransactionRepository transactions, PurchaseRepository purchases, RewardCalculationService rewardCalculationService) { this.customers = customers; this.accounts = accounts; this.transactions = transactions; this.purchases = purchases; this.rewardCalculationService = rewardCalculationService; }
    @EventListener @Transactional public void onPurchase(PurchaseCompletedEvent event) { Customer customer = customers.findById(event.customerId()).orElseThrow(() -> new NotFoundException("Customer not found")); LoyaltyAccount account = accounts.findByCustomer(customer).orElseThrow(() -> new NotFoundException("Loyalty account not found")); Purchase purchase = purchases.findById(event.purchaseId()).orElseThrow(() -> new NotFoundException("Purchase not found")); var breakdown = rewardCalculationService.calculate(customer, purchase); account.earn(breakdown.finalPoints()); rewardCalculationService.saveBreakdown(purchase.getId(), breakdown); transactions.save(new RewardTransaction(account, RewardTransactionType.EARN, breakdown.finalPoints(), breakdown.explanation())); }
}
