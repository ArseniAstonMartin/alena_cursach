package com.example.loyalty.application.port;

import com.example.loyalty.domain.model.Customer;
import com.example.loyalty.domain.model.LoyaltyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, Long> { Optional<LoyaltyAccount> findByCustomer(Customer customer); }
