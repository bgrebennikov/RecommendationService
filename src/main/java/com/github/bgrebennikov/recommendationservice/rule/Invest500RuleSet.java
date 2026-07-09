package com.github.bgrebennikov.recommendationservice.rule;

import com.github.bgrebennikov.recommendationservice.data.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500RuleSet implements RecommendationRuleSet {


    private static final UUID INVEST_500_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private static final String NAME = "Invest 500";
    private static final String TEXT = "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! " +
            "Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета " +
            "на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за " +
            "актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!";

    private final RecommendationRepository repository;


    public Invest500RuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationItem> evaluate(UUID userId) {


        boolean hasDebit = repository.hasProductType(userId, "DEBIT");
        if (!hasDebit) {
            return Optional.empty();
        }


        boolean hasInvest = repository.hasProductType(userId, "INVEST");
        if (hasInvest) {
            return Optional.empty();
        }


        BigDecimal savingDeposits = repository.sumOfDepositsByType(userId, "SAVING");


        boolean isSavingEnough = savingDeposits != null && savingDeposits.compareTo(BigDecimal.valueOf(1000)) > 0;

        if (isSavingEnough) {
            return Optional.of(new RecommendationItem(
                    INVEST_500_ID,
                    NAME,
                    TEXT
            ));
        }

        return Optional.empty();
    }
}
