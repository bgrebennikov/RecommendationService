package com.github.bgrebennikov.recommendationservice.service;

import com.github.bgrebennikov.recommendationservice.data.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.data.RecommendationResponse;
import com.github.bgrebennikov.recommendationservice.rule.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSets;

    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public RecommendationResponse getRecommendations(UUID userId) {
        List<RecommendationItem> recommendations = ruleSets.stream()
                .map(rule -> rule.evaluate(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        return new RecommendationResponse(userId.toString(), recommendations);
    }

}
