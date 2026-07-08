package com.github.bgrebennikov.recommendationservice.rule;

import com.github.bgrebennikov.recommendationservice.data.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRuleSet implements RecommendationRuleSet {

    private static final UUID TOP_SAVING_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");

    private final RecommendationRepository repository;

    public TopSavingRuleSet(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional <RecommendationItem> evaluate(UUID userId) {

        boolean hasDebit = repository.hasProductType(userId, "DEBIT");
        if (!hasDebit) {
            return Optional.empty();
        }

        BigDecimal debitDeposits = repository.sumOfDepositsByType(userId, "DEBIT");
        BigDecimal savingDeposits = repository.sumOfDepositsByType(userId, "SAVING");

        BigDecimal debitWithdrawals = repository.sumOfWithdrawalsByType(userId, "DEBIT");

        BigDecimal limit = BigDecimal.valueOf(50_000);
        boolean condition2 = debitDeposits.compareTo(limit) >= 0 ||
                savingDeposits.compareTo(limit) >= 0;

        boolean condition3 = debitDeposits.compareTo(debitWithdrawals) > 0;

        if (condition2 && condition3) {
            return Optional.of(new RecommendationItem(
                    TOP_SAVING_ID,
                    "Top Saving",
                    null
            ));
        }
        return Optional.empty();
    }

}
