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
 * @author Ekaterina, Boris
 * @version 1.0
 * Класс представляет CRUD методы для управления динамическими правилами в БД.
 *
 */

@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

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

    @Transactional(readOnly = true)
    public List<RuleEntity> findAll() {
        return ruleRepository.findAll();
    }

    @Transactional
    public void deleteRule(UUID id) {
        ruleRepository.deleteById(id);
    }
}