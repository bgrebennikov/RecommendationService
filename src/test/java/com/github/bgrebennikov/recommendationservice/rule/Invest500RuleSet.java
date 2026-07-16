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
class Invest500RuleSetTest {

    private RecommendationRepository repository;
    private Invest500RuleSet ruleSet;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        repository = Mockito.mock(RecommendationRepository.class);
        ruleSet = new Invest500RuleSet(repository);
    }

    @Test
    @DisplayName("Позитивный тест: Invest500 должен подойти, если все условия выполнены")
    void shouldReturnRecommendationWhenUserMatches() {
        Mockito.when(repository.hasProductType(userId, "DEBIT")).thenReturn(true);
        Mockito.when(repository.hasProductType(userId, "INVEST")).thenReturn(false);
        Mockito.when(repository.sumOfDepositsByType(userId, "SAVING"))
                .thenReturn(new BigDecimal("5000.00"));

        Optional<RecommendationItem> result = ruleSet.evaluate(userId);

        assertTrue(result.isPresent());
        assertEquals("Invest 500", result.get().getName());
    }

    @Test
    @DisplayName("Негативный тест: Invest500 должен вернуть Optional.empty(), если нет дебетовой карты")
    void shouldReturnEmptyWhenUserHasNoDebitCard() {
        Mockito.when(repository.hasProductType(userId, "DEBIT")).thenReturn(false);

        Optional<RecommendationItem> result = ruleSet.evaluate(userId);
        assertTrue(result.isEmpty());
    }

}
