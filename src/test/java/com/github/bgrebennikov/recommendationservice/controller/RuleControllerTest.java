package com.github.bgrebennikov.recommendationservice.controller;

import com.github.bgrebennikov.recommendationservice.data.dto.rule.*;
import com.github.bgrebennikov.recommendationservice.service.RuleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RuleController.class)
class RuleControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RuleService ruleService;

    private static final UUID TEST_RULE_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    private static final UUID TEST_PRODUCT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");

    @Test
    @DisplayName("POST /rule — Должен успешно создать правило и вернуть DTO с HTTP 200 OK")
    void shouldCreateRuleSuccessfully() throws Exception {
        RuleQueryRequest queryRequest = new RuleQueryRequest(DRuleQuery.USER_OF, List.of("CREDIT"), false);
        RuleCreateRequest createRequest = new RuleCreateRequest(
                "Простой кредит",
                TEST_PRODUCT_ID,
                "Текст ТЗ для Простого кредита",
                List.of(queryRequest)
        );

        RuleQueryResponseDto queryResponseDto = new RuleQueryResponseDto("USER_OF", List.of("CREDIT"), false);
        RuleItemDto mockResponse = new RuleItemDto(
                TEST_RULE_ID.toString(),
                TEST_PRODUCT_ID.toString(),
                "Простой кредит",
                "Текст ТЗ для Простого кредита",
                List.of(queryResponseDto)
        );

        Mockito.when(ruleService.createRule(any(RuleCreateRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_RULE_ID.toString()))
                .andExpect(jsonPath("$.product_name").value("Простой кредит"))
                .andExpect(jsonPath("$.product_text").value("Текст ТЗ для Простого кредита"))
                .andExpect(jsonPath("$.rule[0].query").value("USER_OF"))
                .andExpect(jsonPath("$.rule[0].arguments[0]").value("CREDIT"))
                .andExpect(jsonPath("$.rule[0].negate").value(false));
    }

    @Test
    @DisplayName("GET /rule — Должен вернуть список правил с HTTP 200 OK")
    void shouldReturnAllRulesList() throws Exception {
        RuleQueryResponseDto queryDto = new RuleQueryResponseDto("USER_OF", List.of("DEBIT"), false);
        RuleItemDto itemDto = new RuleItemDto(
                TEST_RULE_ID.toString(),
                TEST_PRODUCT_ID.toString(),
                "Top Saving",
                "Текст ТЗ для Top Saving",
                List.of(queryDto)
        );
        RuleResponseListDto mockResponse = new RuleResponseListDto(List.of(itemDto));

        Mockito.when(ruleService.findAll()).thenReturn(mockResponse);

        mockMvc.perform(get("/rule")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(TEST_RULE_ID.toString()))
                .andExpect(jsonPath("$.data[0].product_name").value("Top Saving"))
                .andExpect(jsonPath("$.data[0].product_text").value("Текст ТЗ для Top Saving"))
                .andExpect(jsonPath("$.data[0].rule[0].query").value("USER_OF"));
    }

    @Test
    @DisplayName("GET /rule — Должен вернуть пустой список, если правила отсутствуют")
    void shouldReturnEmptyListWhenNoRules() throws Exception {
        RuleResponseListDto emptyResponse = new RuleResponseListDto(Collections.emptyList());
        Mockito.when(ruleService.findAll()).thenReturn(emptyResponse);

        mockMvc.perform(get("/rule")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("DELETE /rule/{id} — Должен успешно удалить правило и вернуть HTTP 204 No Content")
    void shouldDeleteRuleSuccessfully() throws Exception {
        Mockito.doNothing().when(ruleService).deleteRule(TEST_RULE_ID);

        mockMvc.perform(delete("/rule/" + TEST_RULE_ID))
                .andExpect(status().isNoContent());

        Mockito.verify(ruleService, Mockito.times(1)).deleteRule(TEST_RULE_ID);
    }
}