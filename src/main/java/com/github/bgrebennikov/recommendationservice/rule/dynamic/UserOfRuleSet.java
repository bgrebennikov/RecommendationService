package com.github.bgrebennikov.recommendationservice.rule.dynamic;

import com.github.bgrebennikov.recommendationservice.data.rule.DRuleQuery;
import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import com.github.bgrebennikov.recommendationservice.model.RuleQuery;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import com.github.bgrebennikov.recommendationservice.rule.RecommendationDynamicRuleSet;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class UserOfRuleSet implements RecommendationDynamicRuleSet {

    private final RecommendationRepository recommendationRepository;
    private final Logger logger = Logger.getLogger(UserOfRuleSet.class.getName());

    public UserOfRuleSet(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @Override
    public boolean isQueryAvailable(DRuleQuery query) {
        return DRuleQuery.USER_OF.equals(query);
    }

    @Override
    public boolean evaluate(UUID userId, RuleQuery query) {
        logger.log(Level.INFO, "Query: " + query);
        logger.log(Level.INFO, "User: " + userId);
        logger.log(Level.INFO, "Arguments: " + query.getArguments());

        boolean negate = Boolean.TRUE.equals(query.getNegate());
        String product = query.getArguments().get(0);

        boolean hasProduct = recommendationRepository.hasProductType(userId, ProductType.valueOf(product));

        return negate != hasProduct;
    }
}