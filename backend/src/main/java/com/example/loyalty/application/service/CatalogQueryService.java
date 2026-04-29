package com.example.loyalty.application.service;

import com.example.loyalty.application.dto.CatalogDtos.*;
import com.example.loyalty.application.port.MerchantRepository;
import com.example.loyalty.application.port.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CatalogQueryService {
    private final MerchantRepository merchants; private final ProductRepository products;
    public CatalogQueryService(MerchantRepository merchants, ProductRepository products) { this.merchants = merchants; this.products = products; }
    public Page<MerchantDto> merchants(Pageable pageable) { return merchants.findAll(pageable).map(m -> new MerchantDto(m.getId(), m.getName(), m.getCategory())); }
    public Page<ProductDto> products(String search, Pageable pageable) { String q = search == null ? "" : search; return products.findByCategoryContainingIgnoreCaseOrNameContainingIgnoreCase(q, q, pageable).map(p -> new ProductDto(p.getId(), p.getMerchant().getId(), p.getMerchant().getName(), p.getName(), p.getCategory(), p.getPrice())); }
}
