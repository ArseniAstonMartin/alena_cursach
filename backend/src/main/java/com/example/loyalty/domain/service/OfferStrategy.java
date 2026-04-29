package com.example.loyalty.domain.service;

import com.example.loyalty.domain.model.Customer;
import com.example.loyalty.domain.model.Offer;

@FunctionalInterface
public interface OfferStrategy { int score(Customer customer, Offer offer); }
