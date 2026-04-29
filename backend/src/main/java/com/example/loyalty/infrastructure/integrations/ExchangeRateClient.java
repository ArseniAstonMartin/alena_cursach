package com.example.loyalty.infrastructure.integrations;

import com.example.loyalty.infrastructure.config.ExternalIntegration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.Map;

@Component
@ExternalIntegration("exchange-rate")
public class ExchangeRateClient {
    private final RestClient restClient;
    public ExchangeRateClient(RestClient.Builder builder, @Value("${integrations.exchange.base-url}") String baseUrl) { this.restClient = builder.baseUrl(baseUrl).build(); }
    public String eurRate() { try { Map<?, ?> response = restClient.get().uri("/latest?base=USD&symbols=EUR").retrieve().body(Map.class); return String.valueOf(response == null ? "n/a" : response.get("rates")); } catch (RuntimeException ex) { return "external API unavailable"; } }
}
