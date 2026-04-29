package com.example.loyalty.adapters.in.web;

import com.example.loyalty.infrastructure.config.IntegrationRegistry;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final IntegrationRegistry integrationRegistry;
    public AdminController(IntegrationRegistry integrationRegistry) { this.integrationRegistry = integrationRegistry; }
    @GetMapping("/integrations") public Map<String, String> integrations() { return integrationRegistry.registeredIntegrations(); }
}
