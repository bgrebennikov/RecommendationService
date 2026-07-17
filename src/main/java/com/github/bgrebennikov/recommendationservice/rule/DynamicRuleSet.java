package com.github.bgrebennikov.recommendationservice.rule;

import com.github.bgrebennikov.recommendationservice.data.RecommendationItem;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


@Component
public class DynamicRuleSet implements RecommendationRuleSet {


    @Override
    public Optional<RecommendationItem> evaluate(UUID userId) {



        return Optional.empty();
    }
}
