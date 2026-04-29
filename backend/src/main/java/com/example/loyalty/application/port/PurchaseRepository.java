package com.example.loyalty.application.port;

import com.example.loyalty.domain.model.Customer;
import com.example.loyalty.domain.model.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> { @EntityGraph(attributePaths = {"merchant", "items", "items.product"}) Page<Purchase> findByCustomer(Customer customer, Pageable pageable); }
