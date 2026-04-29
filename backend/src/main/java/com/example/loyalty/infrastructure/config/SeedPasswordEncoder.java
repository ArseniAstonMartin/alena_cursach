package com.example.loyalty.infrastructure.config;

import com.example.loyalty.application.port.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SeedPasswordEncoder implements ApplicationRunner {
    private final CustomerRepository customers;
    private final PasswordEncoder passwordEncoder;

    public SeedPasswordEncoder(CustomerRepository customers, PasswordEncoder passwordEncoder) {
        this.customers = customers;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        customers.findAll().stream()
            .filter(customer -> customer.getPasswordHash().startsWith("seed:"))
            .forEach(customer -> customer.changePasswordHash(passwordEncoder.encode(customer.getPasswordHash().substring(5))));
    }
}
