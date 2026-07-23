package com.github.bgrebennikov.recommendationservice.rule.dynamic;

import com.github.bgrebennikov.recommendationservice.data.rule.DRuleQuery;
import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import com.github.bgrebennikov.recommendationservice.model.RuleQuery;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import com.github.bgrebennikov.recommendationservice.rule.RecommendationDynamicRuleSet;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Динамическое правило для проверки активности пользователя по конкретному типу продукта.
 * <p>
 * Правило считается выполнимым если пользователь совершил
 * <b>не менее 5 транзакций</b> по переданному типу продукта.
 * <p>
 *
 * @author Ekaterina, Boris
 * @version 1.1
 */
@Component
public class ActiveUserOffRuleSet implements RecommendationDynamicRuleSet {

    /**
     * Минимальное количество транзакций для признания пользователя активным.
     */
    private static final long MIN_TRANSACTIONS_THRESHOLD = 5;

    private final RecommendationRepository repository;

    public ActiveUserOffRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isQueryAvailable(DRuleQuery query) {
        return query == DRuleQuery.ACTIVE_USER_OF;
    }

    @Override
    public boolean evaluate(UUID userId, RuleQuery query) {
        try {
            ProductType productType = ProductType.valueOf(query.getArguments().get(0));
            long count = repository.countTransactionsByType(userId, productType);

            boolean isActive = count >= MIN_TRANSACTIONS_THRESHOLD;

            return Boolean.TRUE.equals(query.getNegate()) != isActive;
        } catch (IllegalArgumentException e) {
            // Неизвестный ProductType
            return false;
        }
    }
}