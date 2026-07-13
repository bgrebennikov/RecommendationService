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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Invest500RuleSetTest {

    @Mock
    private RecommendationRepository repository;

    @InjectMocks
    private Invest500RuleSet rule;

    private final UUID userId = UUID.randomUUID();
    private final UUID expectedProductId = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private final String expectedProductName = "Invest 500";

    /**
     * ПОЗИТИВНЫЙ СЦЕНАРИЙ: Все условия выполняются
     * 1. Пользователь использует DEBIT -> true
     * 2. Пользователь НЕ использует INVEST -> false
     * 3. Сумма пополнений SAVING > 1000 -> 1500
     */
    @Test
    void shouldReturnRecommendationWhenAllConditionsMet() {
        // given
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(true);
        when(repository.hasProductType(eq(userId), eq("INVEST"))).thenReturn(false);
        when(repository.getSumDepositsByProductType(eq(userId), eq("SAVING")))
                .thenReturn(BigDecimal.valueOf(1500));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(expectedProductId);
        assertThat(result.get().getName()).isEqualTo(expectedProductName);
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
     * НЕГАТИВНЫЙ СЦЕНАРИЙ: Пользователь уже использует INVEST
     */
    @Test
    void shouldReturnEmptyWhenUserUsesInvest() {
        // given
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(true);
        when(repository.hasProductType(eq(userId), eq("INVEST"))).thenReturn(true);

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * НЕГАТИВНЫЙ СЦЕНАРИЙ: Сумма пополнений SAVING <= 1000
     * Проверяем граничное значение - ровно 1000
     */
    @Test
    void shouldReturnEmptyWhenSavingDepositsLessOrEqual1000() {
        // given
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(true);
        when(repository.hasProductType(eq(userId), eq("INVEST"))).thenReturn(false);
        when(repository.getSumDepositsByProductType(eq(userId), eq("SAVING")))
                .thenReturn(BigDecimal.valueOf(1000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * НЕГАТИВНЫЙ СЦЕНАРИЙ: Сумма пополнений SAVING меньше 1000
     */
    @Test
    void shouldReturnEmptyWhenSavingDepositsLessThan1000() {
        // given
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(true);
        when(repository.hasProductType(eq(userId), eq("INVEST"))).thenReturn(false);
        when(repository.getSumDepositsByProductType(eq(userId), eq("SAVING")))
                .thenReturn(BigDecimal.valueOf(500));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * ДОПОЛНИТЕЛЬНЫЙ НЕГАТИВНЫЙ СЦЕНАРИЙ: Все условия кроме INVEST выполняются,
     * но INVEST есть -> должен быть empty
     */
    @Test
    void shouldReturnEmptyWhenInvestExistsEvenIfOtherConditionsMet() {
        // given
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(true);
        when(repository.hasProductType(eq(userId), eq("INVEST"))).thenReturn(true);
        when(repository.getSumDepositsByProductType(eq(userId), eq("SAVING")))
                .thenReturn(BigDecimal.valueOf(2000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * ДОПОЛНИТЕЛЬНЫЙ ПОЗИТИВНЫЙ СЦЕНАРИЙ: Сумма пополнений SAVING значительно больше 1000
     */
    @Test
    void shouldReturnRecommendationWhenSavingDepositsMuchHigher() {
        // given
        when(repository.hasProductType(eq(userId), eq("DEBIT"))).thenReturn(true);
        when(repository.hasProductType(eq(userId), eq("INVEST"))).thenReturn(false);
        when(repository.getSumDepositsByProductType(eq(userId), eq("SAVING")))
                .thenReturn(BigDecimal.valueOf(1000000));

        // when
        Optional<RecommendationItem> result = rule.evaluate(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(expectedProductId);
    }
}
