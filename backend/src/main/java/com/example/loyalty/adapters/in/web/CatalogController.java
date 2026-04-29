package com.example.loyalty.adapters.in.web;

import com.example.loyalty.application.dto.CatalogDtos.*;
import com.example.loyalty.application.service.CatalogQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {
    private final CatalogQueryService catalog;
    public CatalogController(CatalogQueryService catalog) { this.catalog = catalog; }
    @GetMapping("/merchants") public Page<MerchantDto> merchants(Pageable pageable) { return catalog.merchants(pageable); }
    @GetMapping("/products") public Page<ProductDto> products(@RequestParam(required = false) String q, Pageable pageable) { return catalog.products(q, pageable); }
}
