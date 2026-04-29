package com.example.loyalty.application.service;

import com.example.loyalty.application.dto.LoyaltyDtos.RecommendationDto;
import com.example.loyalty.infrastructure.integrations.ExchangeRateClient;
import com.example.loyalty.infrastructure.integrations.GitHubClient;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RecommendationService {
    private final GitHubClient gitHubClient; private final ExchangeRateClient exchangeRateClient;
    public RecommendationService(GitHubClient gitHubClient, ExchangeRateClient exchangeRateClient) { this.gitHubClient = gitHubClient; this.exchangeRateClient = exchangeRateClient; }
    public List<RecommendationDto> feed() { return List.of(new RecommendationDto("Tech partner bonus", "GitHub status: " + gitHubClient.status(), "GitHub REST API / OAuth-ready client"), new RecommendationDto("International shopping", "EUR rate snapshot: " + exchangeRateClient.eurRate(), "ExchangeRate REST API")); }
}
