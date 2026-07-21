package com.github.bgrebennikov.recommendationservice.controller;

import com.github.bgrebennikov.recommendationservice.data.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rules")
public class RuleController {

    private final RecommendationService recommendationService;

    public RuleController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Получить все рекомендации для пользователя
     * GET /rules/{userId}
     */
    @GetMapping("/{userId}")
    public List<RecommendationItem> getUserRules(@PathVariable UUID userId) {
        return recommendationService.getRecommendations(userId).getRecommendations();
    }

    /**
     * Получить количество рекомендаций для пользователя
     * GET /rules/{userId}/count
     */
    @GetMapping("/{userId}/count")
    public int getRulesCount(@PathVariable UUID userId) {
        return recommendationService.getRecommendations(userId).getRecommendations().size();
    }

    /**
     * Проверить, есть ли у пользователя рекомендации
     * GET /rules/{userId}/exists
     */
    @GetMapping("/{userId}/exists")
    public boolean hasRules(@PathVariable UUID userId) {
        return !recommendationService.getRecommendations(userId).getRecommendations().isEmpty();
    }
}
