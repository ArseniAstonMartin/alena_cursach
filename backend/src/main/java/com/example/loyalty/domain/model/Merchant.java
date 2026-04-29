package com.example.loyalty.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "merchants")
public class Merchant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String category;
    protected Merchant() {}
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
}
