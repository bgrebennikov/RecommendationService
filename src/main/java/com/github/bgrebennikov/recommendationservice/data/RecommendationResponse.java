package com.github.bgrebennikov.recommendationservice.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO ответа, содержащее сформированный список рекомендаций для пользователя.
 *
 * @author Ekaterina, Boris
 * @version 1.0
 */
public class RecommendationResponse {

    @JsonProperty("user_id")
    private String userId;

    private List<RecommendationItem> recommendations;

    /**
     * @param userId          Уникальный идентификатор пользователя
     * @param recommendations Список целевых рекомендаций
     */
    public RecommendationResponse(String userId, List<RecommendationItem> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<RecommendationItem> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<RecommendationItem> recommendations) {
        this.recommendations = recommendations;
    }
}