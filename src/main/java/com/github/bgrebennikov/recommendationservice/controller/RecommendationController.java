package com.github.bgrebennikov.recommendationservice.controller;

import com.github.bgrebennikov.recommendationservice.data.RecommendationResponse;
import com.github.bgrebennikov.recommendationservice.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST-контроллер для получения статических рекомендаций пользователям.
 * <p>
 * Обрабатывает HTTP-запросы на основе базовых бизнес-правил и истории операций пользователя.
 *
 * @author Konstantin, Boris
 * @version 1.0
 */
@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Возвращает список статических персональных рекомендаций для указанного пользователя.
     *
     * @param userId Уникальный идентификатор пользователя (UUID)
     * @return DTO {@link RecommendationResponse} с набором подходящих продуктов
     */
    @GetMapping("/{user_id}")
    public RecommendationResponse getRecommendations(@PathVariable("user_id") UUID userId) {
        return recommendationService.getRecommendations(userId);
    }

}