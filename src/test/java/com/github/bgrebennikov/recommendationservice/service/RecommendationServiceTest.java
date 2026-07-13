package com.github.bgrebennikov.recommendationservice.service;

import com.github.bgrebennikov.recommendationservice.data.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.data.RecommendationResponse;
import com.github.bgrebennikov.recommendationservice.rule.RecommendationRuleSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)  // ← Правильная аннотация для сервиса!
class RecommendationServiceTest {

    @Mock
    private RecommendationRuleSet ruleSet1;

    @Mock
    private RecommendationRuleSet ruleSet2;

    @Mock
    private RecommendationRuleSet ruleSet3;

    @InjectMocks
    private RecommendationService service;

    private final UUID userId = UUID.randomUUID();

    @Test
    void shouldReturnRecommendationsWhenRulesMatch() {
        // given - ПРАВИЛЬНЫЙ ПОРЯДОК: (id, name, text)
        RecommendationItem item1 = new RecommendationItem(
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                "Invest 500",
                "Описание Invest 500"
        );
        RecommendationItem item2 = new RecommendationItem(
                UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
                "Top Saving",
                "Описание Top Saving"
        );

        when(ruleSet1.evaluate(userId)).thenReturn(Optional.of(item1));
        when(ruleSet2.evaluate(userId)).thenReturn(Optional.of(item2));
        when(ruleSet3.evaluate(userId)).thenReturn(Optional.empty());

        // when
        RecommendationResponse response = service.getRecommendations(userId);

        // then
        assertThat(response.getRecommendations()).hasSize(2);
        assertThat(response.getRecommendations()).containsExactly(item1, item2);
    }

    @Test
    void shouldReturnEmptyListWhenNoRulesMatch() {
        // given
        when(ruleSet1.evaluate(userId)).thenReturn(Optional.empty());
        when(ruleSet2.evaluate(userId)).thenReturn(Optional.empty());
        when(ruleSet3.evaluate(userId)).thenReturn(Optional.empty());

        // when
        RecommendationResponse response = service.getRecommendations(userId);

        // then
        assertThat(response.getRecommendations()).isEmpty();
    }

    @Test
    void shouldReturnOnlyMatchingRules() {
        // given
        RecommendationItem item = new RecommendationItem(
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                "Invest 500",
                "Описание Invest 500"
        );

        when(ruleSet1.evaluate(userId)).thenReturn(Optional.of(item));
        when(ruleSet2.evaluate(userId)).thenReturn(Optional.empty());
        when(ruleSet3.evaluate(userId)).thenReturn(Optional.empty());

        // when
        RecommendationResponse response = service.getRecommendations(userId);

        // then
        assertThat(response.getRecommendations()).hasSize(1);
        assertThat(response.getRecommendations().get(0)).isEqualTo(item);
    }
}
