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
class TopSavingRuleSetTest {

    @Mock
    private RecommendationRepository repository;

    @InjectMocks
    private TopSavingRuleSet rule;

    private final UUID userId = UUID.randomUUID();
    private final UUID expectedProductId = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    private final String expectedProductName = "Top Saving";

    /**
     * ПОЗИТИВНЫЙ СЦЕНАРИЙ: Все условия выполняются
     * 1. Пользователь использует DEBIT -> true
     * 2. Сумма пополнений DEBIT >= 50000 ИЛИ SAVING >= 50000 -> 60000
     * 3. Сумма пополнений DEBIT > сумма трат DEBIT -> 60000 > 50000
     */
    @Test
    void shouldReturnRecommendationWhenAllConditionsMet() {
        // given
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(true);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(60000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("SAVING")))
                .thenReturn(BigDecimal.valueOf(1000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(50000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(expectedProductId);
        assertThat(result.get().getName()).isEqualTo(expectedProductName);
    }

    /**
     * ПОЗИТИВНЫЙ СЦЕНАРИЙ: Условие выполняется через SAVING (используем ИЛИ)
     * DEBIT пополнения меньше 50000, но SAVING >= 50000
     */
    @Test
    void shouldReturnRecommendationWhenSavingDepositsConditionMetEvenIfDebitLow() {
        // given
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(true);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(10000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("SAVING")))
                .thenReturn(BigDecimal.valueOf(60000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(5000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(expectedProductId);
    }

    /**
     * ПОЗИТИВНЫЙ СЦЕНАРИЙ: Граничное значение для DEBIT = 50000 (ровно)
     */
    @Test
    void shouldReturnRecommendationWhenDebitDepositsExactly50000() {
        // given
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(true);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(50000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("SAVING")))
                .thenReturn(BigDecimal.valueOf(0));
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(40000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isPresent();
    }

    /**
     * НЕГАТИВНЫЙ СЦЕНАРИЙ: Пользователь не использует DEBIT
     */
    @Test
    void shouldReturnEmptyWhenUserDoesNotUseDebit() {
        // given
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(false);

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * НЕГАТИВНЫЙ СЦЕНАРИЙ: Оба условия ИЛИ не выполняются
     * DEBIT пополнения < 50000 И SAVING пополнения < 50000
     */
    @Test
    void shouldReturnEmptyWhenBothDepositsLessThan50000() {
        // given
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(true);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(30000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("SAVING")))
                .thenReturn(BigDecimal.valueOf(20000));

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
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(true);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(60000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("SAVING")))
                .thenReturn(BigDecimal.valueOf(1000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(70000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * НЕГАТИВНЫЙ СЦЕНАРИЙ: Сумма пополнений DEBIT = сумме трат DEBIT (не строго больше)
     */
    @Test
    void shouldReturnEmptyWhenDebitDepositsEqualsWithdraws() {
        // given
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(true);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(60000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("SAVING")))
                .thenReturn(BigDecimal.valueOf(1000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(60000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * НЕГАТИВНЫЙ СЦЕНАРИЙ: DEBIT пополнения = 50000, но траты больше
     */
    @Test
    void shouldReturnEmptyWhenDebitDepositsExactly50000ButWithdrawsHigher() {
        // given
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(true);
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(50000));
        when(repository.getSumDepositsByProductType(eq(userId), eq("SAVING")))
                .thenReturn(BigDecimal.valueOf(0));
        when(repository.getSumDepositsByProductType(eq(userId), eq("DEBIT")))
                .thenReturn(BigDecimal.valueOf(60000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }
}
