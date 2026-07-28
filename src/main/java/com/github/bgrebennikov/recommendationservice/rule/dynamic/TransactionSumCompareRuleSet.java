package com.github.bgrebennikov.recommendationservice.rule.dynamic;

import com.github.bgrebennikov.recommendationservice.data.dto.rule.DRuleQuery;
import com.github.bgrebennikov.recommendationservice.data.types.ComparisonOperator;
import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import com.github.bgrebennikov.recommendationservice.data.types.TransactionType;
import com.github.bgrebennikov.recommendationservice.data.persistence.RuleQueryEntity;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import com.github.bgrebennikov.recommendationservice.rule.RecommendationDynamicRuleSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Динамическое правило для сравнения суммы транзакций определенного типа с фиксированным числовым порогом.
 * <p>
 * Вычисляет агрегированную сумму пополнений или списаний по конкретному продукту
 * и сравнивает ее с константой с помощью оператора сравнения.
 * <p>
 * <b>Ожидаемые аргументы в {@link RuleQueryEntity}:</b>
 * <ul>
 *     <li>{@code arguments[0]} — наименование {@link ProductType} (например, {@code "DEBIT"});</li>
 *     <li>{@code arguments[1]} — тип транзакции ({@code "DEPOSIT"} или {@code "WITHDRAW"});</li>
 *     <li>{@code arguments[2]} — оператор сравнения ({@code ">"}, {@code "<"}, {@code "="}, {@code ">="}, {@code "<="});</li>
 *     <li>{@code arguments[3]} — пороговая сумма в виде строки (например, {@code "100000.00"}).</li>
 * </ul>
 *
 * @author Ekaterina, Boris
 * @version 1.1
 */
@Component
public class TransactionSumCompareRuleSet implements RecommendationDynamicRuleSet {

    private final RecommendationRepository repository;

    public TransactionSumCompareRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isQueryAvailable(DRuleQuery query) {
        return query == DRuleQuery.TRANSACTION_SUM_COMPARE;
    }

    @Override
    public boolean evaluate(UUID userId, RuleQueryEntity query) {
        List<String> args = query.getArguments();
        if (args == null || args.size() < 4) {
            return false;
        }

        try {
            ProductType productType = ProductType.valueOf(args.get(0));
            TransactionType transactionType = TransactionType.fromString(args.get(1));
            ComparisonOperator operator = ComparisonOperator.fromSymbol(args.get(2));
            BigDecimal constant = new BigDecimal(args.get(3));

            BigDecimal sum = fetchTransactionSum(userId, productType, transactionType);
            BigDecimal safeSum = sum != null ? sum : BigDecimal.ZERO;

            boolean result = operator.compare(safeSum, constant);

            return Boolean.TRUE.equals(query.getNegate()) != result;

        } catch (IllegalArgumentException e) {
            // Перехвачены ошибки: невалидный ProductType, неизвестный оператор,
            // неверный тип транзакции или парсинг BigDecimal
            return false;
        }
    }

    /**
     * Запрашивает из репозитория сумму транзакций в зависимости от типа {@link TransactionType}.
     *
     * @param userId          идентификатор пользователя
     * @param productType     тип продукта
     * @param transactionType тип транзакции
     * @return рассчитанная сумма транзакций
     */
    private BigDecimal fetchTransactionSum(UUID userId, ProductType productType, TransactionType transactionType) {
        return switch (transactionType) {
            case DEPOSIT -> repository.sumOfDepositsByType(userId, productType);
            case WITHDRAW -> repository.sumOfWithdrawalsByType(userId, productType);
        };
    }
}