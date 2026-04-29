package com.example.loyalty.adapters.in.web;

import com.example.loyalty.application.dto.PurchaseDtos.*;
import com.example.loyalty.application.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/purchases")
public class PurchaseController {
    private final PurchaseService purchases;
    public PurchaseController(PurchaseService purchases) { this.purchases = purchases; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public PurchaseDto create(Authentication authentication, @Valid @RequestBody CreatePurchaseRequest request) { return purchases.create(authentication.getName(), request); }
    @GetMapping public Page<PurchaseDto> history(Authentication authentication, Pageable pageable) { return purchases.history(authentication.getName(), pageable); }
}
