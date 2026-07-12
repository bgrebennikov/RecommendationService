package com.github.bgrebennikov.recommendationservice.controller;

import com.github.bgrebennikov.recommendationservice.data.RecommendationResponse;
import com.github.bgrebennikov.recommendationservice.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{user_id}")
    public RecommendationResponse getRecommendations(@PathVariable("user_id") UUID userId) {
        return recommendationService.getRecommendations(userId);
    }

}
