package com.github.bgrebennikov.recommendationservice.rule;


import com.github.bgrebennikov.recommendationservice.data.rule.DRuleQuery;
import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import com.github.bgrebennikov.recommendationservice.model.RuleQuery;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TransactionSumCompareDepositWithdraw implements RecommendationDynamicRuleSet {
    private final RecommendationRepository repository;

    public TransactionSumCompareDepositWithdraw(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isQueryAvailable(DRuleQuery query) {
        return query == DRuleQuery.TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW;
    }

    @Override
    public boolean evaluate(UUID userId, RuleQuery query) {
        // Аргументы: 0: Type, 1: Operator
        ProductType productType = ProductType.valueOf(query.getArguments().get(0));
        String operator = query.getArguments().get(1);

        // Вызываем существующие методы репозитория
        BigDecimal deposits = repository.sumOfDepositsByType(userId, productType);
        BigDecimal withdrawals = repository.sumOfWithdrawalsByType(userId, productType);

        boolean result = compare(deposits, withdrawals, operator);
        return query.getNegate() ? !result : result;
    }

    private boolean compare(BigDecimal a, BigDecimal b, String operator) {
        int comparison = a.compareTo(b);
        return switch (operator) {
            case ">" -> comparison > 0;
            case "<" -> comparison < 0;
            case "=" -> comparison == 0;
            case ">=" -> comparison >= 0;
            case "<=" -> comparison <= 0;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }
}
