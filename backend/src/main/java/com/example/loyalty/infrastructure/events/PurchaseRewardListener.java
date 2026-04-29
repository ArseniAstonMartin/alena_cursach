package com.example.loyalty.infrastructure.events;

import com.example.loyalty.application.port.*;
import com.example.loyalty.domain.model.*;
import com.example.loyalty.domain.service.NotFoundException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PurchaseRewardListener {
    private final CustomerRepository customers; private final LoyaltyAccountRepository accounts; private final RewardTransactionRepository transactions;
    public PurchaseRewardListener(CustomerRepository customers, LoyaltyAccountRepository accounts, RewardTransactionRepository transactions) { this.customers = customers; this.accounts = accounts; this.transactions = transactions; }
    @EventListener @Transactional public void onPurchase(PurchaseCompletedEvent event) { Customer customer = customers.findById(event.customerId()).orElseThrow(() -> new NotFoundException("Customer not found")); LoyaltyAccount account = accounts.findByCustomer(customer).orElseThrow(() -> new NotFoundException("Loyalty account not found")); int points = event.totalAmount().intValue() / 10; account.earn(points); transactions.save(new RewardTransaction(account, RewardTransactionType.EARN, points, "Purchase #" + event.purchaseId())); }
}
