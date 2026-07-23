package com.github.bgrebennikov.recommendationservice.rule;

import com.github.bgrebennikov.recommendationservice.data.rule.DRuleQuery;
import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import com.github.bgrebennikov.recommendationservice.model.RuleQuery;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TransactionSumCompareRule implements RecommendationDynamicRuleSet {
    private final RecommendationRepository repository;

    public TransactionSumCompareRule(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isQueryAvailable(DRuleQuery query) {
        return query == DRuleQuery.TRANSACTION_SUM_COMPARE;
    }

    @Override
    public boolean evaluate(UUID userId, RuleQuery query) {
        // 1. Извлекаем аргументы из query
        ProductType productType = ProductType.valueOf(query.getArguments().get(0)); // Например, DEBIT
        String transactionType = query.getArguments().get(1);                      // Например, "DEPOSIT" или "WITHDRAW"
        String operator = query.getArguments().get(2);                             // ">", "<", "="
        BigDecimal constant = new BigDecimal(query.getArguments().get(3));         // Сумма для сравнения

        BigDecimal sum;

        // 2. В зависимости от аргумента выбираем, какой метод репозитория вызвать
        if ("DEPOSIT".equalsIgnoreCase(transactionType)) {
            sum = repository.sumOfDepositsByType(userId, productType);
        } else if ("WITHDRAW".equalsIgnoreCase(transactionType)) {
            sum = repository.sumOfWithdrawalsByType(userId, productType);
        } else {
            throw new IllegalArgumentException("Неизвестный тип транзакции: " + transactionType);
        }

        // 3. Сравниваем результат и возвращаем с учетом отрицания (negate)
        boolean result = compare(sum, constant, operator);
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
