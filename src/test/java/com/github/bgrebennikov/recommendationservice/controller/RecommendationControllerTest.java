package com.github.bgrebennikov.recommendationservice.controller;

import com.github.bgrebennikov.recommendationservice.data.dto.recommendation.RecommendationItemDto;
import com.github.bgrebennikov.recommendationservice.data.dto.recommendation.RecommendationResponse;
import com.github.bgrebennikov.recommendationservice.service.RecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RecommendationController.class)
class RecommendationControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecommendationService recommendationService;

    private static final UUID USER_FOR_INVEST_500 = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");
    private static final UUID USER_FOR_TOP_SAVING = UUID.fromString("d4a4d619-9a0c-4fc5-b0cb-76c49409546b");
    private static final UUID USER_FOR_SIMPLE_CREDIT = UUID.fromString("1f9b149c-6577-448a-bc94-16bea229b71a");

    @Test
    @DisplayName("Должен вернуть рекомендацию Invest 500 для соответствующего пользователя")
    void shouldReturnInvest500Recommendation() throws Exception {
        RecommendationItemDto investItem = new RecommendationItemDto(
                UUID.fromString("22222222-2fff-41af-baff-90ccd7402925"),
                "Invest 500",
                "Текст ТЗ для Invest 500"
        );
        RecommendationResponse mockResponse = new RecommendationResponse(USER_FOR_INVEST_500.toString(), List.of(investItem));

        Mockito.when(recommendationService.getRecommendations(USER_FOR_INVEST_500)).thenReturn(mockResponse);

        mockMvc.perform(get("/recommendation/" + USER_FOR_INVEST_500)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(USER_FOR_INVEST_500.toString()))
                .andExpect(jsonPath("$.recommendations[0].name").value("Invest 500"))
                .andExpect(jsonPath("$.recommendations[0].id").value("22222222-2fff-41af-baff-90ccd7402925"));
    }

    @Test
    @DisplayName("Должен вернуть рекомендацию Top Saving для соответствующего пользователя")
    void shouldReturnTopSavingRecommendation() throws Exception {
        RecommendationItemDto savingItem = new RecommendationItemDto(
                UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
                "Top Saving",
                "Текст ТЗ для Top Saving"
        );
        RecommendationResponse mockResponse = new RecommendationResponse(USER_FOR_TOP_SAVING.toString(), List.of(savingItem));

        Mockito.when(recommendationService.getRecommendations(USER_FOR_TOP_SAVING)).thenReturn(mockResponse);

        mockMvc.perform(get("/recommendation/" + USER_FOR_TOP_SAVING)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(USER_FOR_TOP_SAVING.toString()))
                .andExpect(jsonPath("$.recommendations[0].name").value("Top Saving"))
                .andExpect(jsonPath("$.recommendations[0].id").value("59efc529-2fff-41af-baff-90ccd7402925"));
    }

    @Test
    @DisplayName("Должен вернуть рекомендацию Простой кредит для соответствующего пользователя")
    void shouldReturnSimpleCreditRecommendation() throws Exception {
        RecommendationItemDto creditItem = new RecommendationItemDto(
                UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"),
                "Простой кредит",
                "Текст ТЗ для Простого кредита"
        );
        RecommendationResponse mockResponse = new RecommendationResponse(USER_FOR_SIMPLE_CREDIT.toString(), List.of(creditItem));

        Mockito.when(recommendationService.getRecommendations(USER_FOR_SIMPLE_CREDIT)).thenReturn(mockResponse);

        mockMvc.perform(get("/recommendation/" + USER_FOR_SIMPLE_CREDIT)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(USER_FOR_SIMPLE_CREDIT.toString()))
                .andExpect(jsonPath("$.recommendations[0].name").value("Простой кредит"))
                .andExpect(jsonPath("$.recommendations[0].id").value("ab138afb-f3ba-4a93-b74f-0fcee86d447f"));
    }

    @Test
    @DisplayName("Должен вернуть статус 200 и пустой список рекомендаций, если подходящих правил нет")
    void shouldReturnEmptyRecommendationsList() throws Exception {
        UUID userId = UUID.randomUUID();
        RecommendationResponse mockResponse = new RecommendationResponse(userId.toString(), Collections.emptyList());

        Mockito.when(recommendationService.getRecommendations(userId)).thenReturn(mockResponse);

        mockMvc.perform(get("/recommendation/" + userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userId.toString()))
                .andExpect(jsonPath("$.recommendations").isEmpty());
    }
}
