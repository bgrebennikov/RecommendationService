package com.github.bgrebennikov.recommendationservice.controller;

import com.github.bgrebennikov.recommendationservice.data.RecommendationItem;
import com.github.bgrebennikov.recommendationservice.data.RecommendationResponse;
import com.github.bgrebennikov.recommendationservice.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RuleController.class)
class RuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    private final UUID testUserId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");

    // ==================== ТЕСТЫ ДЛЯ GET /rules/{userId} ====================

    @Test
    void shouldReturnRecommendationsWhenServiceReturnsData() throws Exception {
        // given - подготовка тестовых данных
        RecommendationItem item1 = new RecommendationItem(
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                "Invest 500",
                "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС)"
        );
        RecommendationItem item2 = new RecommendationItem(
                UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
                "Top Saving",
                "Откройте свою собственную «Копилку» с нашим банком!"
        );

        RecommendationResponse mockResponse = new RecommendationResponse(
                testUserId.toString(),
                List.of(item1, item2)
        );

        when(recommendationService.getRecommendations(testUserId)).thenReturn(mockResponse);

        // when & then - выполняем запрос и проверяем ответ
        mockMvc.perform(get("/rules/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("147f6a0f-3b91-413b-ab99-87f081d60d5a"))
                .andExpect(jsonPath("$[0].name").value("Invest 500"))
                .andExpect(jsonPath("$[1].id").value("59efc529-2fff-41af-baff-90ccd7402925"))
                .andExpect(jsonPath("$[1].name").value("Top Saving"));
    }

    @Test
    void shouldReturnEmptyArrayWhenNoRecommendations() throws Exception {
        // given
        RecommendationResponse emptyResponse = new RecommendationResponse(
                testUserId.toString(),
                List.of()
        );

        when(recommendationService.getRecommendations(testUserId)).thenReturn(emptyResponse);

        // when & then
        mockMvc.perform(get("/rules/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnRecommendationsForDifferentUser() throws Exception {
        // given
        UUID anotherUserId = UUID.fromString("d4a4d619-9a0c-4fc5-b0cb-76c49409546b");
        RecommendationItem item = new RecommendationItem(
                UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"),
                "Простой кредит",
                "Откройте мир выгодных кредитов с нами!"
        );

        RecommendationResponse mockResponse = new RecommendationResponse(
                anotherUserId.toString(),
                List.of(item)
        );

        when(recommendationService.getRecommendations(anotherUserId)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/rules/{userId}", anotherUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("ab138afb-f3ba-4a93-b74f-0fcee86d447f"))
                .andExpect(jsonPath("$[0].name").value("Простой кредит"));
    }

    @Test
    void shouldHandleValidUuidInPath() throws Exception {
        // given
        UUID validUuid = UUID.randomUUID();
        RecommendationResponse emptyResponse = new RecommendationResponse(
                validUuid.toString(),
                List.of()
        );

        when(recommendationService.getRecommendations(validUuid)).thenReturn(emptyResponse);

        // when & then
        mockMvc.perform(get("/rules/{userId}", validUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ==================== ТЕСТЫ ДЛЯ GET /rules/{userId}/count ====================

    @Test
    void shouldReturnCorrectCountWhenUserHasRecommendations() throws Exception {
        // given
        RecommendationItem item1 = new RecommendationItem(
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                "Invest 500",
                "Описание"
        );
        RecommendationItem item2 = new RecommendationItem(
                UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
                "Top Saving",
                "Описание"
        );

        RecommendationResponse mockResponse = new RecommendationResponse(
                testUserId.toString(),
                List.of(item1, item2)
        );

        when(recommendationService.getRecommendations(testUserId)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/rules/{userId}/count", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(2));
    }

    @Test
    void shouldReturnZeroCountWhenNoRecommendations() throws Exception {
        // given
        RecommendationResponse emptyResponse = new RecommendationResponse(
                testUserId.toString(),
                List.of()
        );

        when(recommendationService.getRecommendations(testUserId)).thenReturn(emptyResponse);

        // when & then
        mockMvc.perform(get("/rules/{userId}/count", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0));
    }

    // ==================== ТЕСТЫ ДЛЯ GET /rules/{userId}/exists ====================

    @Test
    void shouldReturnTrueWhenUserHasRecommendations() throws Exception {
        // given
        RecommendationItem item = new RecommendationItem(
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                "Invest 500",
                "Описание"
        );

        RecommendationResponse mockResponse = new RecommendationResponse(
                testUserId.toString(),
                List.of(item)
        );

        when(recommendationService.getRecommendations(testUserId)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/rules/{userId}/exists", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void shouldReturnFalseWhenUserHasNoRecommendations() throws Exception {
        // given
        RecommendationResponse emptyResponse = new RecommendationResponse(
                testUserId.toString(),
                List.of()
        );

        when(recommendationService.getRecommendations(testUserId)).thenReturn(emptyResponse);

        // when & then
        mockMvc.perform(get("/rules/{userId}/exists", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    // ==================== ТЕСТ С ВСЕМИ ТРЕМЯ РЕКОМЕНДАЦИЯМИ ====================

    @Test
    void shouldReturnAllThreeRecommendationsForEligibleUser() throws Exception {
        // given
        UUID eligibleUserId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");
        RecommendationItem item1 = new RecommendationItem(
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                "Invest 500",
                "Откройте свой путь к успеху..."
        );
        RecommendationItem item2 = new RecommendationItem(
                UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
                "Top Saving",
                "Откройте свою собственную «Копилку»..."
        );
        RecommendationItem item3 = new RecommendationItem(
                UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"),
                "Простой кредит",
                "Откройте мир выгодных кредитов..."
        );

        RecommendationResponse mockResponse = new RecommendationResponse(
                eligibleUserId.toString(),
                List.of(item1, item2, item3)
        );

        when(recommendationService.getRecommendations(eligibleUserId)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/rules/{userId}", eligibleUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Invest 500"))
                .andExpect(jsonPath("$[1].name").value("Top Saving"))
                .andExpect(jsonPath("$[2].name").value("Простой кредит"));
    }
}
