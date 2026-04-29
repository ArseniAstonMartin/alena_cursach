package com.example.loyalty.application.service;

import com.example.loyalty.application.dto.CustomerDto;
import com.example.loyalty.application.port.CustomerRepository;
import com.example.loyalty.domain.model.Customer;
import com.example.loyalty.domain.service.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CustomerQueryService {
    private final CustomerRepository customers;
    public CustomerQueryService(CustomerRepository customers) { this.customers = customers; }
    public Customer requireByEmail(String email) { return customers.findByEmail(email).orElseThrow(() -> new NotFoundException("Customer not found")); }
    public CustomerDto me(Authentication authentication) { return CustomerDto.from(requireByEmail(authentication.getName())); }
    public Page<CustomerDto> search(String q, Pageable pageable) { String normalized = q == null ? "" : q; return customers.findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(normalized, normalized, pageable).map(CustomerDto::from); }
}
