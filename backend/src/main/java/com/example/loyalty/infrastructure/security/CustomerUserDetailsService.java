package com.example.loyalty.infrastructure.security;

import com.example.loyalty.application.port.CustomerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
    private final CustomerRepository customers;
    public CustomerUserDetailsService(CustomerRepository customers) { this.customers = customers; }
    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { var customer = customers.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username)); return User.withUsername(customer.getEmail()).password(customer.getPasswordHash()).authorities(customer.getRoles().stream().map(role -> role.getName()).toArray(String[]::new)).build(); }
}
