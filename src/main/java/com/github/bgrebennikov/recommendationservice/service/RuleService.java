package com.github.bgrebennikov.recommendationservice.service;

import com.github.bgrebennikov.recommendationservice.data.dto.rule.RuleCreateRequest;
import com.github.bgrebennikov.recommendationservice.data.dto.rule.RuleQueryDto;
import com.github.bgrebennikov.recommendationservice.data.dto.rule.RuleItemDto;
import com.github.bgrebennikov.recommendationservice.data.dto.rule.RuleResponseDto;
import com.github.bgrebennikov.recommendationservice.model.RuleEntity;
import com.github.bgrebennikov.recommendationservice.model.RuleQuery;
import com.github.bgrebennikov.recommendationservice.repository.RuleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для управления динамическими правилами рекомендаций в БД.
 * <p>
 * Предоставляет методы создания, получения и удаления правил с поддержкой транзакционности
 * и автоматическим кэшированием списков правил в Redis.
 *
 * @author Ekaterina, Boris
 * @version 1.1
 */
@Service
public class RuleService {

    public static final String RULES_CACHE_KEY = "s_rules";

    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    /**
     * Создает новое правило на основе запроса и сохраняет его вместе с дочерними условиями в БД.
     * <p>
     * Сбрасывает кэш правил, чтобы новые запросы рекомендаций подхватили созданное правило.
     *
     * @param request DTO с данными для создания правила и списком условий
     * @return Сохраненную сущность {@link RuleEntity} с присвоенным ID и связанными условиями
     */
    @Transactional
    @CacheEvict(value = RULES_CACHE_KEY, allEntries = true)
    public RuleEntity createRule(RuleCreateRequest request) {
        RuleEntity entity = new RuleEntity();
        entity.setProductId(request.getProductId());
        entity.setProductName(request.getProductName());
        entity.setProductText(request.getProductText());

        var rulesList = request.getRule();
        if (rulesList != null) {
            for (int i = 0; i < rulesList.size(); i++) {
                var dRule = rulesList.get(i);
                RuleQuery query = new RuleQuery();
                query.setQueryType(dRule.getQuery());
                query.setArguments(dRule.getArguments());
                query.setNegate(Boolean.TRUE.equals(dRule.getNegate()));
                query.setSortOrder(i);

                entity.addQuery(query);
            }
        }

        return ruleRepository.save(entity);
    }

    /**
     * Возвращает список всех существующих правил в системе.
     * <p>
     * Результат кэшируется в Redis под ключом {@code rules}.
     *
     * @return Список сущностей {@link RuleEntity}
     */
    @Transactional(readOnly = true)
    @Cacheable(value = RULES_CACHE_KEY)
    public RuleResponseDto findAll() {

        return new RuleResponseDto(
                ruleRepository.findAll().stream()
                        .map(this::toDto)
                        .toList()
        );
    }

    /**
     * Удаляет правило из базы данных по его уникальному идентификатору.
     * <p>
     * Сбрасывает кэш правил, чтобы удаленное правило больше не участвовало в расчетах.
     *
     * @param id Уникальный идентификатор правила (UUID)
     */
    @Transactional
    @CacheEvict(value = RULES_CACHE_KEY, allEntries = true)
    public void deleteRule(UUID id) {
        ruleRepository.deleteById(id);
    }


    private RuleItemDto toDto(RuleEntity entity) {

        List<RuleQueryDto> queryDto = entity.getQueries().stream()
                .map(q -> new RuleQueryDto(
                        q.getQueryType().name(), q.getArguments(), q.getNegate(), q.getSortOrder()
                )).toList();

        return new RuleItemDto(
                entity.getId().toString(),
                entity.getProductId().toString(),
                entity.getProductName(),
                entity.getProductText(),
                queryDto
        );
    }
}