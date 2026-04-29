package com.example.loyalty.application.port;

import com.example.loyalty.domain.model.LoyaltyAccount;
import com.example.loyalty.domain.model.RewardTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardTransactionRepository extends JpaRepository<RewardTransaction, Long> { Page<RewardTransaction> findByAccount(LoyaltyAccount account, Pageable pageable); }
