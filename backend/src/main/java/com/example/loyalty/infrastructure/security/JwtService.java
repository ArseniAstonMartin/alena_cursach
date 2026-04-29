package com.example.loyalty.infrastructure.security;

import com.example.loyalty.domain.model.Customer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key; private final long accessTokenMinutes;
    public JwtService(@Value("${loyalty.jwt.secret}") String secret, @Value("${loyalty.jwt.access-token-minutes}") long accessTokenMinutes) { this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.accessTokenMinutes = accessTokenMinutes; }
    public String createAccessToken(Customer customer) { Instant now = Instant.now(); return Jwts.builder().subject(customer.getEmail()).claim("roles", customer.getRoles().stream().map(r -> r.getName()).toList()).claim("customerId", customer.getId()).issuedAt(Date.from(now)).expiration(Date.from(now.plus(accessTokenMinutes, ChronoUnit.MINUTES))).signWith(key).compact(); }
    public String subject(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject(); }
}
