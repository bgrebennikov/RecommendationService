package com.github.bgrebennikov.recommendationservice.rule;

import com.github.bgrebennikov.recommendationservice.data.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRuleSet implements RecommendationRuleSet {

    private static final UUID TOP_SAVING_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");

    private static final String NAME = "Top Saving";
    private static final String TEXT = """
            Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, 
            который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков 
            и потерянных квитанций — всё под контролем!
            
            Преимущества «Копилки»:
            
            Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически 
            переводить определенную сумму на ваш счет.
            
            Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления 
            и корректируйте стратегию при необходимости.
            
            Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только 
            через мобильное приложение или интернет-банкинг.
            
            Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!
            """;

    private final RecommendationRepository repository;

    public TopSavingRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional <RecommendationItem> evaluate(UUID userId) {

        boolean hasDebit = repository.hasProductType(userId, ProductType.DEBIT);
        if (!hasDebit) {
            return Optional.empty();
        }

        BigDecimal debitDeposits = repository.sumOfDepositsByType(userId, ProductType.DEBIT);
        BigDecimal savingDeposits = repository.sumOfDepositsByType(userId, ProductType.SAVING);

        BigDecimal debitWithdrawals = repository.sumOfWithdrawalsByType(userId, ProductType.DEBIT);

        BigDecimal limit = BigDecimal.valueOf(50_000);
        boolean isDepositsGreaterLimi = debitDeposits.compareTo(limit) >= 0 ||
                savingDeposits.compareTo(limit) >= 0;

        boolean isDepositsWithdrawals = debitDeposits.compareTo(debitWithdrawals) > 0;

        if (isDepositsGreaterLimi && isDepositsWithdrawals) {
            return Optional.of(new RecommendationItem(
                    TOP_SAVING_ID,
                    NAME,
                    TEXT
            ));
        }
        return Optional.empty();
    }

}
