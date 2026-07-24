package com.github.bgrebennikov.recommendationservice.rule.statics;

import com.github.bgrebennikov.recommendationservice.data.dto.recommendation.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import com.github.bgrebennikov.recommendationservice.rule.RecommendationRuleSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Правило рекомендации накопительного продукта «Top Saving» («Копилка»).
 * <p>
 * Продукт предлагается пользователю при выполнении следующих условий:
 * <ul>
 *   <li>У пользователя есть хотя бы один открытый дебетовый счет ({@link ProductType#DEBIT})</li>
 *   <li>Сумма пополнений по дебетовым или накопительным счетам не менее 50 000 ₽</li>
 *   <li>Сумма пополнений по дебетовым счетам строго больше суммы списаний по ним</li>
 * </ul>
 *
 * @author Konstantin
 * @version 1.0
 */
@Component
public class TopSavingRuleSet implements RecommendationRuleSet {

    private static final UUID TOP_SAVING_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");

    private static final String NAME = "Top Saving";
    private static final String TEXT = """
             Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент,\s
             который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков\s
             и потерянных квитанций — всё под контролем!
            \s
             Преимущества «Копилки»:
            \s
             Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически\s
             переводить определенную сумму на ваш счет.
            \s
             Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления\s
             и корректируйте стратегию при необходимости.
            \s
             Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только\s
             через мобильное приложение или интернет-банкинг.
            \s
             Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!
            """;

    private final RecommendationRepository repository;

    public TopSavingRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationItem> evaluate(UUID userId) {
        boolean hasDebit = repository.hasProductType(userId, ProductType.DEBIT);
        if (!hasDebit) {
            return Optional.empty();
        }

        BigDecimal debitDeposits = repository.sumOfDepositsByType(userId, ProductType.DEBIT);
        BigDecimal savingDeposits = repository.sumOfDepositsByType(userId, ProductType.SAVING);
        BigDecimal debitWithdrawals = repository.sumOfWithdrawalsByType(userId, ProductType.DEBIT);

        BigDecimal limit = BigDecimal.valueOf(50_000);

        boolean isDepositsGreaterLimit = (debitDeposits != null && debitDeposits.compareTo(limit) >= 0) ||
                (savingDeposits != null && savingDeposits.compareTo(limit) >= 0);

        boolean isDepositsGreaterWithdrawals = debitDeposits != null && debitWithdrawals != null &&
                debitDeposits.compareTo(debitWithdrawals) > 0;

        if (isDepositsGreaterLimit && isDepositsGreaterWithdrawals) {
            return Optional.of(new RecommendationItem(
                    TOP_SAVING_ID,
                    NAME,
                    TEXT
            ));
        }

        return Optional.empty();
    }
}