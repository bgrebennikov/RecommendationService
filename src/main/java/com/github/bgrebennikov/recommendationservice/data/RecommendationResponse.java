package com.github.bgrebennikov.recommendationservice.data;

import java.util.List;

public class RecommendationResponse {
    private String userId;
    private List<RecommendationItem> recommendations;

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
