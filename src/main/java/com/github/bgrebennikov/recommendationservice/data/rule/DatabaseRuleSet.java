package com.github.bgrebennikov.recommendationservice.data.rule;
import com.github.bgrebennikov.recommendationservice.data.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.model.RuleEntity;
import com.github.bgrebennikov.recommendationservice.repository.RuleRepository;
import com.github.bgrebennikov.recommendationservice.rule.RecommendationRuleSet;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class DatabaseRuleSet implements RecommendationRuleSet {

    private final RuleRepository ruleRepository;
    private final DynamicRuleEvaluator evaluator;

    public DatabaseRuleSet(RuleRepository ruleRepository, DynamicRuleEvaluator evaluator) {
        this.ruleRepository = ruleRepository;
        this.evaluator = evaluator;
    }

    @Override
    public Optional<RecommendationItem> evaluate(UUID userId) {
        // Загружаем все правила из БД
        return ruleRepository.findAll().stream()
                .filter(rule -> isRuleSatisfied(rule, userId))
                .map(rule -> new RecommendationItem(
                        rule.getProductId(),   // 1. Сначала UUID (ID продукта)
                        rule.getProductName(), // 2. Потом String (Имя)
                        rule.getProductText()  // 3. Потом String (Текст)
                ))
                .findFirst();  // Возвращаем первую подошедшую рекомендацию
    }

    private boolean isRuleSatisfied(RuleEntity rule, UUID userId) {
        // Правило считается выполненным, если ВСЕ его Query вернули true (AND логика)
        return rule.getQueries().stream()
                .allMatch(query -> evaluator.evaluate(query, userId));
    }
}
