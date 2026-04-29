package com.example.loyalty.adapters.in.web;

import com.example.loyalty.application.dto.CustomerDto;
import com.example.loyalty.application.service.CustomerQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerQueryService customers;
    public CustomerController(CustomerQueryService customers) { this.customers = customers; }
    @GetMapping("/me") public CustomerDto me(Authentication authentication) { return customers.me(authentication); }
    @GetMapping @PreAuthorize("hasRole('ADMIN')") public Page<CustomerDto> search(@RequestParam(required = false) String q, Pageable pageable) { return customers.search(q, pageable); }
}
