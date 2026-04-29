package com.example.loyalty.infrastructure.integrations;

import com.example.loyalty.infrastructure.config.ExternalIntegration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ExternalIntegration("github")
public class GitHubClient {
    private final RestClient restClient;
    public GitHubClient(RestClient.Builder builder, @Value("${integrations.github.base-url}") String baseUrl) { this.restClient = builder.baseUrl(baseUrl).build(); }
    public String status() { try { return restClient.get().uri("/zen").retrieve().body(String.class); } catch (RuntimeException ex) { return "external API unavailable"; } }
}
