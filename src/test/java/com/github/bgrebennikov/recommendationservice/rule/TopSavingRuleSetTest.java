package com.github.bgrebennikov.recommendationservice.rule;

import com.github.bgrebennikov.recommendationservice.data.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.repository.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TopSavingRuleSetTest {

    private RecommendationRepository repository;
    private TopSavingRuleSet ruleSet;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        repository = Mockito.mock(RecommendationRepository.class);
        ruleSet = new TopSavingRuleSet(repository);
    }

    @Test
    @DisplayName("Позитивный тест: Top Saving должен подойти, если условия выполнены")
    void shouldReturnRecommendationWhenUserMatches() {
        Mockito.when(repository.hasProductType(userId, "DEBIT")).thenReturn(true);
        Mockito.when(repository.sumOfDepositsByType(userId, "DEBIT")).thenReturn(new BigDecimal("60000.00"));
        Mockito.when(repository.sumOfDepositsByType(userId, "SAVING")).thenReturn(BigDecimal.ZERO);
        Mockito.when(repository.sumOfWithdrawalsByType(userId, "DEBIT")).thenReturn(new BigDecimal("40000.00"));

        Optional<RecommendationItem> result = ruleSet.evaluate(userId);

        assertTrue(result.isPresent());
        assertEquals("Top Saving", result.get().getName());
    }

    @Test
    @DisplayName("Негативный тест: Должен вернуть Optional.empty(), если нет дебетовой карты")
    void shouldReturnEmptyWhenNoDebitCard() {
        Mockito.when(repository.hasProductType(userId, "DEBIT")).thenReturn(false);
        Optional<RecommendationItem> result = ruleSet.evaluate(userId);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Негативный тест: Должен вернуть Optional.empty(), если траты превышают пополнения")
    void shouldReturnEmptyWhenWithdrawalsExceedDeposits() {
        Mockito.when(repository.hasProductType(userId, "DEBIT")).thenReturn(true);
        Mockito.when(repository.sumOfDepositsByType(userId, "DEBIT")).thenReturn(new BigDecimal("60000.00"));
        Mockito.when(repository.sumOfDepositsByType(userId, "SAVING")).thenReturn(BigDecimal.ZERO);
        Mockito.when(repository.sumOfWithdrawalsByType(userId, "DEBIT")).thenReturn(new BigDecimal("70000.00"));

        Optional<RecommendationItem> result = ruleSet.evaluate(userId);

        assertTrue(result.isEmpty());
    }
}