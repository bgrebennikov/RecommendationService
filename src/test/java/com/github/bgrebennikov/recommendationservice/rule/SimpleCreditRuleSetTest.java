package com.github.bgrebennikov.recommendationservice.rule;

import com.github.bgrebennikov.recommendationservice.data.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleCreditRuleSetTest {

    @Mock
    private RecommendationRepository repository;

    @InjectMocks
    private SimpleCreditRuleSet rule;

    private final UUID userId = UUID.randomUUID();
    private final UUID expectedProductId = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    private final String expectedProductName = "Простой кредит";

    /**
     * ПОЗИТИВНЫЙ СЦЕНАРИЙ: Все условия выполняются
     * 1. Пользователь НЕ использует CREDIT -> false
     * 2. Сумма пополнений DEBIT > сумма трат DEBIT -> 200000 > 150000
     * 3. Сумма трат DEBIT > 100000 -> 150000 > 100000
     */
    @Test
    void shouldReturnRecommendationWhenAllConditionsMet() {
        // given
        when(repository.hasProductType(eq(userId), eq("CREDIT"))).thenReturn(false);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(200000));
        when(repository.getSumWithdrawsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(150000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(expectedProductId);
        assertThat(result.get().getName()).isEqualTo(expectedProductName);
    }

    /**
     * ПОЗИТИВНЫЙ СЦЕНАРИЙ: Траты значительно больше 100000, но пополнения больше трат
     */
    @Test
    void shouldReturnRecommendationWhenSpendingVeryHigh() {
        // given
        when(repository.hasProductType(eq(userId), eq("CREDIT"))).thenReturn(false);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(1000000));
        when(repository.getSumWithdrawsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(500000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isPresent();
    }

    /**
     * НЕГАТИВНЫЙ СЦЕНАРИЙ: Пользователь уже использует CREDIT
     */
    @Test
    void shouldReturnEmptyWhenUserUsesCredit() {
        // given
        when(repository.hasProductType(eq(userId), eq("CREDIT"))).thenReturn(true);

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * НЕГАТИВНЫЙ СЦЕНАРИЙ: Сумма пополнений DEBIT <= сумма трат DEBIT
     */
    @Test
    void shouldReturnEmptyWhenDebitDepositsLessThanWithdraws() {
        // given
        when(repository.hasProductType(eq(userId), eq("CREDIT"))).thenReturn(false);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(100000));
        when(repository.getSumWithdrawsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(150000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * НЕГАТИВНЫЙ СЦЕНАРИЙ: Сумма пополнений DEBIT = сумме трат DEBIT
     */
    @Test
    void shouldReturnEmptyWhenDebitDepositsEqualsWithdraws() {
        // given
        when(repository.hasProductType(eq(userId), eq("CREDIT"))).thenReturn(false);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(200000));
        when(repository.getSumWithdrawsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(200000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * НЕГАТИВНЫЙ СЦЕНАРИЙ: Сумма трат DEBIT <= 100000 (граничное значение)
     */
    @Test
    void shouldReturnEmptyWhenDebitWithdrawsLessOrEqual100000() {
        // given
        when(repository.hasProductType(eq(userId), eq("CREDIT"))).thenReturn(false);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(200000));
        when(repository.getSumWithdrawsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(100000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * НЕГАТИВНЫЙ СЦЕНАРИЙ: Сумма трат DEBIT меньше 100000
     */
    @Test
    void shouldReturnEmptyWhenDebitWithdrawsLessThan100000() {
        // given
        when(repository.hasProductType(eq(userId), eq("CREDIT"))).thenReturn(false);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(200000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(80000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * НЕГАТИВНЫЙ СЦЕНАРИЙ: Пополнения больше трат, но траты меньше 100000
     */
    @Test
    void shouldReturnEmptyWhenDepositsHigherButWithdrawsBelowThreshold() {
        // given
        when(repository.hasProductType(eq(userId), eq("CREDIT"))).thenReturn(false);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(50000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(30000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * ДОПОЛНИТЕЛЬНЫЙ НЕГАТИВНЫЙ СЦЕНАРИЙ: Все условия кроме CREDIT выполняются,
     * но CREDIT есть -> должен быть empty
     */
    @Test
    void shouldReturnEmptyWhenCreditExistsEvenIfOtherConditionsMet() {
        // given
        when(repository.hasProductType(eq(userId), eq("CREDIT"))).thenReturn(true);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(200000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(150000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }
}
