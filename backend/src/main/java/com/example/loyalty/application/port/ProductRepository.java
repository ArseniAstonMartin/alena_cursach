package com.example.loyalty.application.port;

import com.example.loyalty.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> { Page<Product> findByCategoryContainingIgnoreCaseOrNameContainingIgnoreCase(String category, String name, Pageable pageable); }
