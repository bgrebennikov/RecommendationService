package com.github.bgrebennikov.recommendationservice.rule;

import com.github.bgrebennikov.recommendationservice.data.dto.recommendation.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import com.github.bgrebennikov.recommendationservice.rule.statics.SimpleCreditRuleSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SimpleCreditRuleSetTest {

    private RecommendationRepository repository;
    private SimpleCreditRuleSet ruleSet;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        repository = Mockito.mock(RecommendationRepository.class);
        ruleSet = new SimpleCreditRuleSet(repository);
    }

    @Test
    @DisplayName("Позитивный тест: Простой кредит должен подойти, если все условия выполнены")
    void shouldReturnRecommendationWhenUserMatches() {

        Mockito.when(repository.hasProductType(userId, ProductType.CREDIT)).thenReturn(false);
        Mockito.when(repository.sumOfWithdrawalsByType(userId, ProductType.DEBIT)).thenReturn(new BigDecimal("120000.00"));
        Mockito.when(repository.sumOfDepositsByType(userId, ProductType.DEBIT)).thenReturn(new BigDecimal("150000.00"));

        Optional<RecommendationItem> result = ruleSet.evaluate(userId);

        assertTrue(result.isPresent());
        assertEquals("Простой кредит", result.get().getName());
    }

    @Test
    @DisplayName("Негативный тест: Должен вернуть Optional.empty(), если у пользователя уже есть кредит")
    void shouldReturnEmptyWhenUserAlreadyHasCredit() {
        Mockito.when(repository.hasProductType(userId, ProductType.CREDIT)).thenReturn(true);

        Optional<RecommendationItem> result = ruleSet.evaluate(userId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Негативный тест: Должен вернуть Optional.empty(), если траты меньше или равны 100 000")
    void shouldReturnEmptyWhenWithdrawalsAreNotEnough() {
        Mockito.when(repository.hasProductType(userId, ProductType.CREDIT)).thenReturn(false);

        Mockito.when(repository.sumOfWithdrawalsByType(userId, ProductType.DEBIT)).thenReturn(new BigDecimal("100000.00"));
        Mockito.when(repository.sumOfDepositsByType(userId, ProductType.DEBIT)).thenReturn(new BigDecimal("110000.00"));

        Optional<RecommendationItem> result = ruleSet.evaluate(userId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Негативный тест: Должен вернуть Optional.empty(), если пополнения меньше трат")
    void shouldReturnEmptyWhenDepositsAreLessThanWithdrawals() {
        Mockito.when(repository.hasProductType(userId, ProductType.CREDIT)).thenReturn(false);
        Mockito.when(repository.sumOfWithdrawalsByType(userId, ProductType.DEBIT)).thenReturn(new BigDecimal("150000.00"));
        Mockito.when(repository.sumOfDepositsByType(userId, ProductType.DEBIT)).thenReturn(new BigDecimal("130000.00"));

        Optional<RecommendationItem> result = ruleSet.evaluate(userId);

        assertTrue(result.isEmpty());
    }
}
