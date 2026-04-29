package com.example.loyalty.infrastructure.events;

import java.math.BigDecimal;

public record PurchaseCompletedEvent(Long purchaseId, Long customerId, BigDecimal totalAmount) {}
