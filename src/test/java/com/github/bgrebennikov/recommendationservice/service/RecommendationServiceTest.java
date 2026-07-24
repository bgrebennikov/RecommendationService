package com.github.bgrebennikov.recommendationservice.service;

import com.github.bgrebennikov.recommendationservice.data.dto.recommendation.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.data.dto.recommendation.RecommendationResponse;
import com.github.bgrebennikov.recommendationservice.repository.RuleRepository;
import com.github.bgrebennikov.recommendationservice.rule.RecommendationRuleSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    private RecommendationRuleSet investRuleSet;
    private RecommendationRuleSet creditRuleSet;

    private RecommendationService service;

    private UUID userId;


    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        investRuleSet = Mockito.mock(RecommendationRuleSet.class);
        creditRuleSet = Mockito.mock(RecommendationRuleSet.class);
        RuleRepository ruleRepository = Mockito.mock(RuleRepository.class);
        service = new RecommendationService(
                List.of(investRuleSet, creditRuleSet),
                ruleRepository,
                List.of()

        );

    }

    @Test
    @DisplayName("Должен вернуть все подходящие рекомментации")
    void shouldReturnAllMatchingRecommendations() {
        RecommendationItem investRec = new RecommendationItem(
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"), "Invest500", "text"
        );
        RecommendationItem creditRec = new RecommendationItem(
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5b"), "Простой кредит", "text"
        );


        Mockito.when(investRuleSet.evaluate(userId)).thenReturn(Optional.of(investRec));
        Mockito.when(creditRuleSet.evaluate(userId)).thenReturn(Optional.of(creditRec));

        RecommendationResponse response = service.getRecommendations(userId);

        assertEquals(userId.toString(), response.getUserId());
        assertEquals(2, response.getRecommendations().size());




        assertTrue(response.getRecommendations().stream().anyMatch(item -> item.getName().equals("Invest500")));
        assertTrue(response.getRecommendations().stream().anyMatch(item -> item.getName().equals("Простой кредит")));
    }


    @Test
    @DisplayName("Должен вернуть пустой список если нет подходящих рекоммендаций")
    void shouldReturnEmptyListOnNoRuleMatch() {
        Mockito.when(investRuleSet.evaluate(userId)).thenReturn(Optional.empty());
        Mockito.when(creditRuleSet.evaluate(userId)).thenReturn(Optional.empty());

        RecommendationResponse response = service.getRecommendations(userId);

        assertEquals(userId.toString(), response.getUserId());
        assertTrue(response.getRecommendations().isEmpty());
    }
}
