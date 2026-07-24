package com.github.bgrebennikov.recommendationservice.service;

import com.github.bgrebennikov.recommendationservice.data.rule.RuleCreateRequest;
import com.github.bgrebennikov.recommendationservice.model.RuleEntity;
import com.github.bgrebennikov.recommendationservice.model.RuleQuery;
import com.github.bgrebennikov.recommendationservice.repository.RuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для управления динамическими правилами рекомендаций в БД.
 * <p>
 * Предоставляет методы создания, получения и удаления правил с поддержкой транзакционности.
 *
 * @author Ekaterina, Boris
 * @version 1.0
 */
@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    /**
     * Создает новое правило на основе запроса и сохраняет его вместе с дочерними условиями в БД.
     * <p>
     * Метод связывает условия ({@link RuleQuery}) с правилом с сохранением их порядка (sortOrder).
     *
     * @param request DTO с данными для создания правила и списком условий
     * @return Сохраненную сущность {@link RuleEntity} с присвоенным ID и связанными условиями
     */
    @Transactional
    public RuleEntity createRule(RuleCreateRequest request) {
        RuleEntity entity = new RuleEntity();
        entity.setProductId(request.getProductId());
        entity.setProductName(request.getProductName());
        entity.setProductText(request.getProductText());

        var rulesList = request.getRule();
        for (int i = 0; i < rulesList.size(); i++) {
            var dRule = rulesList.get(i);
            RuleQuery query = new RuleQuery();
            query.setQueryType(dRule.getQuery());
            query.setArguments(dRule.getArguments());
            query.setNegate(Boolean.TRUE.equals(dRule.getNegate()));
            query.setSortOrder(i);


            entity.addQuery(query);
        }

        return ruleRepository.save(entity);
    }

    /**
     * Возвращает список всех существующих правил в системе.
     *
     * @return Список сущностей {@link RuleEntity}
     */
    @Transactional(readOnly = true)
    public List<RuleEntity> findAll() {
        return ruleRepository.findAll();
    }

    /**
     * Удаляет правило из базы данных по его уникальному идентификатору.
     *
     * @param id Уникальный идентификатор правила (UUID)
     */
    @Transactional
    public void deleteRule(UUID id) {
        ruleRepository.deleteById(id);
    }
}