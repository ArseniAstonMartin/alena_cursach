package com.example.loyalty.application.port;

import com.example.loyalty.domain.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {}
