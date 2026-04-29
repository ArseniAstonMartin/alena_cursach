package com.example.loyalty.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customers")
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private CustomerSegment segment = CustomerSegment.NEWCOMER;
    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "customer_roles", joinColumns = @JoinColumn(name = "customer_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    protected Customer() {}
    public Customer(String fullName, String email, String passwordHash) { this.fullName = fullName; this.email = email; this.passwordHash = passwordHash; }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public CustomerSegment getSegment() { return segment; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public Set<Role> getRoles() { return roles; }
    public void setSegment(CustomerSegment segment) { this.segment = segment; }
    public void changePasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void addRole(Role role) { roles.add(role); }
}
