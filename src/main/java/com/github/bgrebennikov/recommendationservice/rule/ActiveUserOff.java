package com.github.bgrebennikov.recommendationservice.rule;

import com.github.bgrebennikov.recommendationservice.data.rule.DRuleQuery;
import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import com.github.bgrebennikov.recommendationservice.model.RuleQuery;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ActiveUserOff implements RecommendationDynamicRuleSet {
    private final RecommendationRepository repository;

    public ActiveUserOff(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isQueryAvailable(DRuleQuery query) {
        return query.equals(DRuleQuery.ACTIVE_USER_OF);
    }

    @Override
    public boolean evaluate(UUID userId, RuleQuery query) {
        // Аргумент: "DEBIT", "CREDIT" и т.д.
        ProductType productType = ProductType.valueOf(query.getArguments().get(0));
        long count = repository.countTransactionsByType(userId, productType);

        boolean result = count >= 5;
        return query.getNegate() ? !result : result;
    }
}


