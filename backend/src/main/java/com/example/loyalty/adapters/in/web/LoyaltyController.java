package com.example.loyalty.adapters.in.web;

import com.example.loyalty.application.dto.LoyaltyDtos.*;
import com.example.loyalty.application.service.LoyaltyFacade;
import com.example.loyalty.application.service.RecommendationService;
import com.example.loyalty.domain.model.CustomerSegment;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/loyalty")
public class LoyaltyController {
    private final LoyaltyFacade loyalty; private final RecommendationService recommendations;
    public LoyaltyController(LoyaltyFacade loyalty, RecommendationService recommendations) { this.loyalty = loyalty; this.recommendations = recommendations; }
    @GetMapping("/balance") public BalanceDto balance(Authentication authentication) { return loyalty.balance(authentication.getName()); }
    @GetMapping("/offers") public List<OfferDto> offers(Authentication authentication) { return loyalty.personalizedOffers(authentication.getName()); }
    @PostMapping("/offers/{offerId}/claim") public OfferDto claim(Authentication authentication, @PathVariable Long offerId) { return loyalty.claim(authentication.getName(), offerId); }
    @PostMapping("/redemptions") public RedemptionDto redeemReward(Authentication authentication, @Valid @RequestBody RedemptionRequest request) { return loyalty.redeem(authentication.getName(), request); }
    @GetMapping("/certificates") public List<CertificateDto> certificates(Authentication authentication) { return loyalty.certificates(authentication.getName()); }
    @GetMapping("/purchase-profile") public PurchaseProfileDto purchaseProfile(Authentication authentication) { return loyalty.purchaseProfile(authentication.getName()); }
    @GetMapping("/program") public LoyaltyProgramDto program() { return loyalty.program(); }
    @PatchMapping("/segment/recalculate") public CustomerSegment segment(Authentication authentication) { return loyalty.recalculateSegment(authentication.getName()); }
    @GetMapping("/transactions") public Page<RewardTransactionDto> transactions(Authentication authentication, Pageable pageable) { return loyalty.transactions(authentication.getName(), pageable); }
    @GetMapping("/recommendations") public List<RecommendationDto> recommendations() { return recommendations.feed(); }
}
