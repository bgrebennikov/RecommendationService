package com.github.bgrebennikov.recommendationservice.service;

import com.github.bgrebennikov.recommendationservice.data.rule.RuleCreateRequest;
import com.github.bgrebennikov.recommendationservice.model.RuleEntity;
import com.github.bgrebennikov.recommendationservice.model.RuleQuery;
import com.github.bgrebennikov.recommendationservice.repository.RuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Transactional
    public void createRule(RuleCreateRequest request) {
        RuleEntity entity = new RuleEntity();
        entity.setProductId(request.getProductId());
        entity.setProductName(request.getProductName());
        entity.setProductText(request.getProductText());

        List<RuleQuery> queries = IntStream.range(0, request.getRule().size())
                .mapToObj(i -> {
                    var dRule = request.getRule().get(i);
                    RuleQuery query = new RuleQuery();
                    query.setQueryType(dRule.getQuery());
                    query.setArguments(dRule.getArguments());
                    query.setNegate(dRule.getNegate() != null && dRule.getNegate());
                    query.setSortOrder(i);
                    query.setRule(entity);
                    return query;
                })
                .collect(Collectors.toList());

        entity.setQueries(queries);
        ruleRepository.save(entity);
    }

    public List<RuleEntity> findAll() {
        return ruleRepository.findAll();
    }

    @Transactional
    public void deleteRule(UUID id) {
        ruleRepository.deleteById(id);
    }
}
