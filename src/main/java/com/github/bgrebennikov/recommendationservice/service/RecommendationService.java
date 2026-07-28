package com.github.bgrebennikov.recommendationservice.service;

import com.github.bgrebennikov.recommendationservice.data.dto.recommendation.RecommendationItemDto;
import com.github.bgrebennikov.recommendationservice.data.dto.recommendation.RecommendationResponse;
import com.github.bgrebennikov.recommendationservice.data.dto.rule.DRuleQuery;
import com.github.bgrebennikov.recommendationservice.data.persistence.RuleEntity;
import com.github.bgrebennikov.recommendationservice.data.persistence.RuleQueryEntity;
import com.github.bgrebennikov.recommendationservice.repository.RuleRepository;
import com.github.bgrebennikov.recommendationservice.rule.RecommendationDynamicRuleSet;
import com.github.bgrebennikov.recommendationservice.rule.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Сервис формирования персональных рекомендаций для пользователей.
 * <p>
 * Объединяет результат выполнения двух движков:
 * <ul>
 *   <li><b>Статический</b> — проверяет набор жестко запрограммированных правил ({@link RecommendationRuleSet})</li>
 *   <li><b>Динамический</b> — загружает правила из БД ({@link RuleRepository}) и вычисляет их
 *       через зарегистрированные процессоры стратегий ({@link RecommendationDynamicRuleSet})</li>
 * </ul>
 *
 * @author Konstantin, Boris
 * @version 1.0
 */
@Service
public class RecommendationService {

    private static final Logger LOGGER = Logger.getLogger(RecommendationService.class.getName());

    private final List<RecommendationRuleSet> staticRuleSets;
    private final RuleRepository ruleRepository;
    private final Map<DRuleQuery, RecommendationDynamicRuleSet> dynamicRuleSetMap;

    public RecommendationService(List<RecommendationRuleSet> staticRuleSets,
                                 RuleRepository ruleRepository,
                                 List<RecommendationDynamicRuleSet> dynamicRuleSets) {
        this.staticRuleSets = staticRuleSets;
        this.ruleRepository = ruleRepository;

        this.dynamicRuleSetMap = dynamicRuleSets.stream()
                .collect(Collectors.toMap(
                        RecommendationService::getSupportedQuery,
                        Function.identity()
                ));
    }

    /**
     * Формирует полный список доступных рекомендаций для указанного пользователя.
     *
     * @param userId Уникальный идентификатор пользователя
     * @return Общий результат с агрегированным списком персональных рекомендаций
     */
    public RecommendationResponse getRecommendations(UUID userId) {
        List<RecommendationItemDto> recommendations = new ArrayList<>();

        recommendations.addAll(evaluateStaticRules(userId));
        recommendations.addAll(evaluateDynamicRules(userId));

        return new RecommendationResponse(userId.toString(), recommendations);
    }

    private List<RecommendationItemDto> evaluateStaticRules(UUID userId) {
        return staticRuleSets.stream()
                .map(rule -> rule.evaluate(userId))
                .flatMap(Optional::stream)
                .toList();
    }

    private List<RecommendationItemDto> evaluateDynamicRules(UUID userId) {
        return ruleRepository.findAll().stream()
                .filter(rule -> isRuleMatches(userId, rule))
                .map(this::toRecommendationItem)
                .toList();
    }

    private boolean isRuleMatches(UUID userId, RuleEntity rule) {
        List<RuleQueryEntity> queries = rule.getQueries();
        if (queries == null || queries.isEmpty()) {
            return false;
        }
        return queries.stream().allMatch(query -> evaluateQuery(userId, query));
    }

    private boolean evaluateQuery(UUID userId, RuleQueryEntity query) {
        DRuleQuery queryType = query.getQueryType();
        if (queryType == null) {
            LOGGER.log(Level.WARNING, "Query type is null for query: {0}", query);
            return false;
        }

        RecommendationDynamicRuleSet strategy = dynamicRuleSetMap.get(queryType);
        if (strategy == null) {
            LOGGER.log(Level.WARNING, "No strategy registered for query type: {0}", queryType);
            return false;
        }

        return strategy.evaluate(userId, query);
    }

    private RecommendationItemDto toRecommendationItem(RuleEntity rule) {
        return new RecommendationItemDto(
                rule.getId(),
                rule.getProductName(),
                rule.getProductText()
        );
    }

    private static DRuleQuery getSupportedQuery(RecommendationDynamicRuleSet strategy) {
        for (DRuleQuery query : DRuleQuery.values()) {
            if (strategy.isQueryAvailable(query)) {
                return query;
            }
        }
        throw new IllegalStateException("Strategy " + strategy.getClass().getName() + " does not support any DRuleQuery type");
    }
}