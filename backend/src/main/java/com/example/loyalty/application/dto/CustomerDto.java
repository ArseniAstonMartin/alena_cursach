package com.example.loyalty.application.dto;

import com.example.loyalty.domain.model.Customer;
import com.example.loyalty.domain.model.CustomerSegment;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record CustomerDto(Long id, String fullName, String email, CustomerSegment segment, Set<String> roles, OffsetDateTime createdAt) {
    public static CustomerDto from(Customer customer) {
        return new CustomerDto(customer.getId(), customer.getFullName(), customer.getEmail(), customer.getSegment(), customer.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()), customer.getCreatedAt());
    }
}
