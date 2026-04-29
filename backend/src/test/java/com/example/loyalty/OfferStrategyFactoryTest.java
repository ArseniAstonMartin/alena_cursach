package com.example.loyalty;

import com.example.loyalty.domain.model.Customer;
import com.example.loyalty.domain.service.OfferStrategyFactory;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class OfferStrategyFactoryTest {
    @Test
    void factoryCreatesComparatorForPersonalizedOffers() {
        OfferStrategyFactory factory = new OfferStrategyFactory();
        Customer customer = new Customer("Test", "test@example.com", "hash");
        assertThat(factory.comparatorFor(customer)).isNotNull();
    }
}
