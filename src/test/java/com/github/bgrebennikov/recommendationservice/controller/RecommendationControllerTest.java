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

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    private final UUID testUserId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");

    @Test
    void shouldReturnRecommendationsWhenServiceReturnsData() throws Exception {
        // given - ПРАВИЛЬНЫЙ ПОРЯДОК: (id, name, text)
        RecommendationItem item1 = new RecommendationItem(
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),  // id
                "Invest 500",                                              // name
                "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка!"
        );

        RecommendationItem item2 = new RecommendationItem(
                UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),  // id
                "Top Saving",                                              // name
                "Откройте свою собственную «Копилку» с нашим банком!"
        );

        RecommendationResponse mockResponse = new RecommendationResponse(
                testUserId.toString(),
                List.of(item1, item2)
        );

        when(recommendationService.getRecommendations(testUserId)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/recommendation/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(testUserId.toString()))
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations.length()").value(2))
                .andExpect(jsonPath("$.recommendations[0].id").value("147f6a0f-3b91-413b-ab99-87f081d60d5a"))
                .andExpect(jsonPath("$.recommendations[0].name").value("Invest 500"))
                .andExpect(jsonPath("$.recommendations[1].id").value("59efc529-2fff-41af-baff-90ccd7402925"))
                .andExpect(jsonPath("$.recommendations[1].name").value("Top Saving"));
    }

    @Test
    void shouldReturnEmptyRecommendationsWhenServiceReturnsEmptyList() throws Exception {
        // given
        RecommendationResponse emptyResponse = new RecommendationResponse(
                testUserId.toString(),
                List.of()
        );

        when(recommendationService.getRecommendations(testUserId)).thenReturn(emptyResponse);

        // when & then
        mockMvc.perform(get("/recommendation/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(testUserId.toString()))
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations").isEmpty());
    }

    @Test
    void shouldReturnRecommendationsForDifferentUserId() throws Exception {
        // given
        UUID anotherUserId = UUID.fromString("d4a4d619-9a0c-4fc5-b0cb-76c49409546b");
        RecommendationItem item = new RecommendationItem(
                UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"),  // id
                "Простой кредит",                                          // name
                "Откройте мир выгодных кредитов с нами!"
        );

        RecommendationResponse mockResponse = new RecommendationResponse(
                anotherUserId.toString(),
                List.of(item)
        );

        when(recommendationService.getRecommendations(anotherUserId)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/recommendation/{userId}", anotherUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(anotherUserId.toString()))
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations.length()").value(1))
                .andExpect(jsonPath("$.recommendations[0].id").value("ab138afb-f3ba-4a93-b74f-0fcee86d447f"))
                .andExpect(jsonPath("$.recommendations[0].name").value("Простой кредит"));
    }

    @Test
    void shouldHandleValidUuidInPath() throws Exception {
        // given
        UUID validUuid = UUID.randomUUID();
        RecommendationResponse mockResponse = new RecommendationResponse(
                validUuid.toString(),
                List.of()
        );

        when(recommendationService.getRecommendations(validUuid)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/recommendation/{userId}", validUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(validUuid.toString()));
    }

    @Test
    void shouldReturnCorrectJsonStructure() throws Exception {
        // given
        RecommendationItem item = new RecommendationItem(
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),  // id
                "Test Product",                                            // name
                "Test description"
        );

        RecommendationResponse mockResponse = new RecommendationResponse(
                testUserId.toString(),
                List.of(item)
        );

        when(recommendationService.getRecommendations(testUserId)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/recommendation/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").exists())
                .andExpect(jsonPath("$.recommendations").exists())
                .andExpect(jsonPath("$.user_id").isString())
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations[0].id").exists())
                .andExpect(jsonPath("$.recommendations[0].name").exists())
                .andExpect(jsonPath("$.recommendations[0].text").exists())
                .andExpect(jsonPath("$.recommendations[0].id").isString())
                .andExpect(jsonPath("$.recommendations[0].name").isString())
                .andExpect(jsonPath("$.recommendations[0].text").isString());
    }
}
