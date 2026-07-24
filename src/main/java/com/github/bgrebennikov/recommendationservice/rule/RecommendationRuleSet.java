package com.github.bgrebennikov.recommendationservice.rule;

import com.github.bgrebennikov.recommendationservice.data.RecommendationItem;

import java.util.Optional;
import java.util.UUID;

/**
 * Базовый контракт для выполнения проверок (статичных правил), определяющих применимость
 * конкретной рекомендации для пользователя.
 *
 * @author Boris
 * @version 1.0
 */
public interface RecommendationRuleSet {

    /**
     * Вычисляет правило для указанного пользователя.
     *
     * @param userId Уникальный идентификатор пользователя
     * @return {@link Optional} с рекомендацией, если пользователь соответствует условиям правила;
     * иначе {@link Optional#empty()}
     */
    Optional<RecommendationItem> evaluate(UUID userId);
}