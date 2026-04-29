package com.example.loyalty.application.port;

import com.example.loyalty.domain.model.ClaimedOffer;
import com.example.loyalty.domain.model.Customer;
import com.example.loyalty.domain.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimedOfferRepository extends JpaRepository<ClaimedOffer, Long> { boolean existsByCustomerAndOffer(Customer customer, Offer offer); }
