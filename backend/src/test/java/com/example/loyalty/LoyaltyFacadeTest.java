package com.example.loyalty;

import com.example.loyalty.application.port.*;
import com.example.loyalty.application.service.CustomerQueryService;
import com.example.loyalty.application.service.LoyaltyFacade;
import com.example.loyalty.application.dto.LoyaltyDtos.RedemptionRequest;
import com.example.loyalty.domain.model.Customer;
import com.example.loyalty.domain.model.LoyaltyAccount;
import com.example.loyalty.domain.service.BusinessRuleViolationException;
import com.example.loyalty.domain.service.OfferStrategyFactory;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LoyaltyFacadeTest {
    private final CustomerQueryService customerQueryService = mock(CustomerQueryService.class);
    private final LoyaltyAccountRepository accounts = mock(LoyaltyAccountRepository.class);
    private final RewardTransactionRepository transactions = mock(RewardTransactionRepository.class);
    private final OfferRepository offers = mock(OfferRepository.class);
    private final ClaimedOfferRepository claimedOffers = mock(ClaimedOfferRepository.class);
    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final LoyaltyFacade facade = new LoyaltyFacade(customerQueryService, accounts, transactions, offers, claimedOffers, new OfferStrategyFactory(), jdbcTemplate);

    @Test
    void redeemCreatesCertificateAndDecreasesBalance() {
        Customer customer = new Customer("Alice", "alice@example.com", "hash");
        LoyaltyAccount account = new LoyaltyAccount(customer);
        account.earn(200);
        when(customerQueryService.requireByEmail("alice@example.com")).thenReturn(customer);
        when(accounts.findByCustomer(customer)).thenReturn(Optional.of(account));

        var result = facade.redeem("alice@example.com", new RedemptionRequest(100, "Coffee certificate"));

        assertThat(result.pointsSpent()).isEqualTo(100);
        assertThat(result.discountAmount()).isEqualByComparingTo("10");
        assertThat(result.remainingBalance()).isEqualTo(100);
        assertThat(result.confirmationCode()).startsWith("LP-");
        verify(transactions).save(any());
        verify(jdbcTemplate).update(startsWith("insert into redemption_orders"), any(), any(), eq(100), any(), eq("Coffee certificate"), any());
    }

    @Test
    void redeemRejectsWhenBalanceIsTooLow() {
        Customer customer = new Customer("Bob", "bob@example.com", "hash");
        LoyaltyAccount account = new LoyaltyAccount(customer);
        account.earn(40);
        when(customerQueryService.requireByEmail("bob@example.com")).thenReturn(customer);
        when(accounts.findByCustomer(customer)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> facade.redeem("bob@example.com", new RedemptionRequest(50, "Discount")))
            .isInstanceOf(BusinessRuleViolationException.class)
            .hasMessageContaining("Not enough points");
        verifyNoInteractions(transactions);
    }
}
