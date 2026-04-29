package com.example.loyalty.application.service;

import com.example.loyalty.application.dto.PurchaseDtos.*;
import com.example.loyalty.application.port.*;
import com.example.loyalty.domain.model.*;
import com.example.loyalty.domain.service.NotFoundException;
import com.example.loyalty.infrastructure.events.PurchaseCompletedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

@Service
public class PurchaseService {
    private final CustomerQueryService customerQueryService; private final MerchantRepository merchants; private final ProductRepository products; private final PurchaseRepository purchases; private final ApplicationEventPublisher events;
    public PurchaseService(CustomerQueryService customerQueryService, MerchantRepository merchants, ProductRepository products, PurchaseRepository purchases, ApplicationEventPublisher events) { this.customerQueryService = customerQueryService; this.merchants = merchants; this.products = products; this.purchases = purchases; this.events = events; }
    @Transactional public PurchaseDto create(String email, CreatePurchaseRequest request) { Customer customer = customerQueryService.requireByEmail(email); Merchant merchant = merchants.findById(request.merchantId()).orElseThrow(() -> new NotFoundException("Merchant not found")); Purchase purchase = new Purchase(customer, merchant); request.items().forEach(item -> { Product product = products.findById(item.productId()).orElseThrow(() -> new NotFoundException("Product not found")); purchase.addItem(product, item.quantity(), product.getPrice()); }); Purchase saved = purchases.save(purchase); events.publishEvent(new PurchaseCompletedEvent(saved.getId(), customer.getId(), saved.getTotalAmount())); return toDto(saved); }
    @Transactional(readOnly = true) public Page<PurchaseDto> history(String email, Pageable pageable) { Customer customer = customerQueryService.requireByEmail(email); return purchases.findByCustomer(customer, pageable).map(this::toDto); }
    private PurchaseDto toDto(Purchase purchase) { return new PurchaseDto(purchase.getId(), purchase.getCustomer().getId(), purchase.getMerchant().getId(), purchase.getMerchant().getName(), purchase.getTotalAmount(), purchase.getPurchasedAt(), purchase.getItems().stream().map(item -> new PurchaseItemDto(item.getProduct().getId(), item.getProduct().getName(), item.getQuantity(), item.getUnitPrice())).collect(Collectors.toList())); }
}
