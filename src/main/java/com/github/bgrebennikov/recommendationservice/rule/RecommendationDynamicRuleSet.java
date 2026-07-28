package com.github.bgrebennikov.recommendationservice.rule;

import com.github.bgrebennikov.recommendationservice.data.dto.rule.DRuleQuery;
import com.github.bgrebennikov.recommendationservice.data.persistence.RuleQueryEntity;

import java.util.UUID;

/**
 * Сервисный контракт для выполнения индивидуальных динамических проверок (запросов) правил.
 *
 * @author Boris
 * @version 1.0
 */
public interface RecommendationDynamicRuleSet {

    /**
     * Проверяет, поддерживается ли указанный тип запроса текущим процессором правил.
     *
     * @param query Тип динамического запроса
     * @return {@code true}, если данный процессор умеет обрабатывать передаваемый тип запроса
     */
    boolean isQueryAvailable(DRuleQuery query);

    /**
     * Выполняет бизнес-проверку конкретного условия из правила для пользователя.
     *
     * @param userId Уникальный идентификатор пользователя
     * @param query  Сущность условия из БД, содержащая аргументы и логику проверки
     * @return {@code true}, если условие выполнено; иначе {@code false}
     */
    boolean evaluate(UUID userId, RuleQueryEntity query);
}