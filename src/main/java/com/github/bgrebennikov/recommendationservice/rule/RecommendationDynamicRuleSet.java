package com.github.bgrebennikov.recommendationservice.rule;

import com.github.bgrebennikov.recommendationservice.data.rule.DRuleQuery;
import com.github.bgrebennikov.recommendationservice.model.RuleQuery;

import java.util.UUID;

public interface RecommendationDynamicRuleSet {

    boolean isQueryAvailable(DRuleQuery query);

    boolean evaluate(UUID userId, RuleQuery query);
}
