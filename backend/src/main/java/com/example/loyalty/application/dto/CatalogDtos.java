package com.example.loyalty.application.dto;

import java.math.BigDecimal;

public final class CatalogDtos {
    private CatalogDtos() {}
    public record MerchantDto(Long id, String name, String category) {}
    public record ProductDto(Long id, Long merchantId, String merchantName, String name, String category, BigDecimal price) {}
}
