package com.example.loyalty.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public final class PurchaseDtos {
    private PurchaseDtos() {}
    public record PurchaseItemRequest(@NotNull Long productId, @Min(1) int quantity) {}
    public record CreatePurchaseRequest(@NotNull Long merchantId, @Valid @NotEmpty List<PurchaseItemRequest> items) {}
    public record PurchaseItemDto(Long productId, String productName, int quantity, BigDecimal unitPrice) {}
    public record PurchaseDto(Long id, Long customerId, Long merchantId, String merchantName, BigDecimal totalAmount, OffsetDateTime purchasedAt, List<PurchaseItemDto> items) {}
}
