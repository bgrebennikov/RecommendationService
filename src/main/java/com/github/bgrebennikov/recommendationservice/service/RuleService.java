package com.github.bgrebennikov.recommendationservice.service;

import com.github.bgrebennikov.recommendationservice.data.RuleMapper;
import com.github.bgrebennikov.recommendationservice.data.dto.rule.RuleCreateRequest;
import com.github.bgrebennikov.recommendationservice.data.dto.rule.RuleItemDto;
import com.github.bgrebennikov.recommendationservice.data.dto.rule.RuleResponseListDto;
import com.github.bgrebennikov.recommendationservice.data.persistence.RuleEntity;
import com.github.bgrebennikov.recommendationservice.repository.RuleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Сервис для управления динамическими правилами рекомендаций в БД.
 * <p>
 * Предоставляет методы создания, получения и удаления правил с поддержкой транзакционности
 * и автоматическим кэшированием списков правил в Redis.
 *
 * @author Ekaterina, Boris
 * @version 1.3
 */
@Service
public class RuleService {

    public static final String RULES_CACHE_KEY = "s_rules";

    private final RuleRepository ruleRepository;
    private final RuleMapper ruleMapper;

    public RuleService(RuleRepository ruleRepository, RuleMapper ruleMapper) {
        this.ruleRepository = ruleRepository;
        this.ruleMapper = ruleMapper;
    }

    /**
     * Создает новое правило на основе запроса и сохраняет его вместе с дочерними условиями в БД.
     * <p>
     * Сбрасывает кэш правил, чтобы новые запросы рекомендаций подхватили созданное правило.
     *
     * @param request DTO с данными для создания правила и списком условий
     * @return DTO сохраненного правила {@link RuleItemDto}
     */
    @Transactional
    @CacheEvict(value = RULES_CACHE_KEY, allEntries = true)
    public RuleItemDto createRule(RuleCreateRequest request) {
        RuleEntity entity = ruleMapper.toEntity(request);

        if (entity.getQueries() != null) {
            entity.getQueries().forEach(query -> query.setRule(entity));
        }

        RuleEntity savedEntity = ruleRepository.save(entity);
        return ruleMapper.toItemDto(savedEntity);
    }

    /**
     * Возвращает список всех существующих правил в системе.
     * <p>
     * Результат кэшируется в Redis под ключом {@value RULES_CACHE_KEY}.
     *
     * @return DTO-контейнер со списком всех правил {@link RuleResponseListDto}
     */
    @Transactional(readOnly = true)
    @Cacheable(value = RULES_CACHE_KEY)
    public RuleResponseListDto findAll() {
        return ruleMapper.toListResponseDto(
                ruleRepository.findAllWithQueries()
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
}