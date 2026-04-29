package com.example.loyalty.application.service;

import com.example.loyalty.application.dto.AuthDtos.*;
import com.example.loyalty.application.dto.CustomerDto;
import com.example.loyalty.application.port.*;
import com.example.loyalty.domain.model.Customer;
import com.example.loyalty.domain.model.LoyaltyAccount;
import com.example.loyalty.domain.model.RefreshToken;
import com.example.loyalty.domain.service.BusinessRuleViolationException;
import com.example.loyalty.domain.service.NotFoundException;
import com.example.loyalty.infrastructure.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AuthService {
    private final CustomerRepository customers; private final RoleRepository roles; private final LoyaltyAccountRepository accounts; private final RefreshTokenRepository refreshTokens; private final PasswordEncoder passwordEncoder; private final JwtService jwtService; private final long refreshTokenDays;
    public AuthService(CustomerRepository customers, RoleRepository roles, LoyaltyAccountRepository accounts, RefreshTokenRepository refreshTokens, PasswordEncoder passwordEncoder, JwtService jwtService, @Value("${loyalty.jwt.refresh-token-days}") long refreshTokenDays) { this.customers = customers; this.roles = roles; this.accounts = accounts; this.refreshTokens = refreshTokens; this.passwordEncoder = passwordEncoder; this.jwtService = jwtService; this.refreshTokenDays = refreshTokenDays; }
    @Transactional public TokenResponse register(RegisterRequest request) { if (customers.existsByEmail(request.email())) throw new BusinessRuleViolationException("Email is already registered"); Customer customer = new Customer(request.fullName(), request.email(), passwordEncoder.encode(request.password())); customer.addRole(roles.findByName("ROLE_CUSTOMER").orElseThrow(() -> new NotFoundException("Default role not found"))); Customer saved = customers.save(customer); accounts.save(new LoyaltyAccount(saved)); return issueTokens(saved); }
    @Transactional public TokenResponse login(LoginRequest request) { Customer customer = customers.findByEmail(request.email()).orElseThrow(() -> new BadCredentialsException("Invalid credentials")); if (!passwordEncoder.matches(request.password(), customer.getPasswordHash())) throw new BadCredentialsException("Invalid credentials"); return issueTokens(customer); }
    @Transactional public TokenResponse refresh(RefreshRequest request) { RefreshToken token = refreshTokens.findByToken(request.refreshToken()).orElseThrow(() -> new BadCredentialsException("Invalid refresh token")); if (token.isExpired()) { refreshTokens.delete(token); throw new BadCredentialsException("Refresh token expired"); } refreshTokens.delete(token); return issueTokens(token.getCustomer()); }
    private TokenResponse issueTokens(Customer customer) { String refresh = UUID.randomUUID().toString(); refreshTokens.save(new RefreshToken(customer, refresh, OffsetDateTime.now().plusDays(refreshTokenDays))); return new TokenResponse(jwtService.createAccessToken(customer), refresh, CustomerDto.from(customer)); }
}
