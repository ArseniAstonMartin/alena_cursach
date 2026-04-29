package com.example.loyalty.domain.service;

import com.example.loyalty.domain.model.Customer;
import com.example.loyalty.domain.model.Offer;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
public class OfferStrategyFactory {
    private final List<OfferStrategy> strategies = List.of(
        (customer, offer) -> offer.getTargetSegment() == customer.getSegment() ? 50 : 0,
        (customer, offer) -> offer.getValidUntil().isAfter(LocalDate.now()) ? 20 : -100,
        (customer, offer) -> Math.min(30, offer.getBonusPoints() / 10)
    );
    public int combinedScore(Customer customer, Offer offer) { return strategies.stream().mapToInt(strategy -> strategy.score(customer, offer)).sum(); }
    public Comparator<Offer> comparatorFor(Customer customer) { return Comparator.comparingInt((Offer offer) -> combinedScore(customer, offer)).reversed(); }
}
