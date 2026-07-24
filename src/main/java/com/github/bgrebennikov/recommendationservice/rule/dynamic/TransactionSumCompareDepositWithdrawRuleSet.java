package com.github.bgrebennikov.recommendationservice.rule.dynamic;


import com.github.bgrebennikov.recommendationservice.data.dto.rule.DRuleQuery;
import com.github.bgrebennikov.recommendationservice.data.types.ComparisonOperator;
import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import com.github.bgrebennikov.recommendationservice.model.RuleQuery;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import com.github.bgrebennikov.recommendationservice.rule.RecommendationDynamicRuleSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class TransactionSumCompareDepositWithdrawRuleSet implements RecommendationDynamicRuleSet {
    private final RecommendationRepository repository;

    public TransactionSumCompareDepositWithdrawRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isQueryAvailable(DRuleQuery query) {
        return query == DRuleQuery.TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW;
    }

    @Override
    public boolean evaluate(UUID userId, RuleQuery query) {
        List<String> args = query.getArguments();
        if (args == null || args.size() < 2) {
            return false;
        }

        try {
            ProductType productType = ProductType.valueOf(args.get(0));
            ComparisonOperator operator = ComparisonOperator.fromSymbol(args.get(1));

            BigDecimal deposits = repository.sumOfDepositsByType(userId, productType);
            BigDecimal withdrawals = repository.sumOfWithdrawalsByType(userId, productType);

            BigDecimal safeDeposits = deposits != null ? deposits : BigDecimal.ZERO;
            BigDecimal safeWithdrawals = withdrawals != null ? withdrawals : BigDecimal.ZERO;

            boolean result = operator.compare(safeDeposits, safeWithdrawals);

            return Boolean.TRUE.equals(query.getNegate()) != result;

        } catch (IllegalArgumentException e) {
            // Перехвачен невалидный ProductType или неизвестный оператор
            return false;
        }
    }
}
