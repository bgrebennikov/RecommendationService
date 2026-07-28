package com.github.bgrebennikov.recommendationservice.service;

import com.github.bgrebennikov.recommendationservice.data.RuleMapper;
import com.github.bgrebennikov.recommendationservice.data.dto.rule.*;
import com.github.bgrebennikov.recommendationservice.data.persistence.RuleEntity;
import com.github.bgrebennikov.recommendationservice.data.persistence.RuleQueryEntity;
import com.github.bgrebennikov.recommendationservice.repository.RuleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleServiceTest {

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private RuleMapper ruleMapper;

    @InjectMocks
    private RuleService ruleService;

    private static final UUID RULE_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    private static final UUID PRODUCT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447a");

    @Test
    @DisplayName("createRule — должен смаппить запрос, проставить parent-child связь, сохранить сущность и вернуть DTO")
    void createRule_ShouldSaveAndReturnDto() {
        RuleQueryRequest queryRequest = new RuleQueryRequest(DRuleQuery.USER_OF, List.of("CREDIT"), false);
        RuleCreateRequest createRequest = new RuleCreateRequest(
                "Простой кредит",
                PRODUCT_ID,
                "Описание",
                List.of(queryRequest)
        );

        RuleQueryEntity queryEntity = new RuleQueryEntity();
        RuleEntity unmappedEntity = new RuleEntity();
        unmappedEntity.setQueries(new ArrayList<>(List.of(queryEntity)));

        RuleEntity savedEntity = new RuleEntity();
        savedEntity.setId(RULE_ID);

        RuleItemDto expectedDto = new RuleItemDto(RULE_ID.toString(), PRODUCT_ID.toString(), "Простой кредит", "Описание", Collections.emptyList());

        when(ruleMapper.toEntity(createRequest)).thenReturn(unmappedEntity);
        when(ruleRepository.save(unmappedEntity)).thenReturn(savedEntity);
        when(ruleMapper.toItemDto(savedEntity)).thenReturn(expectedDto);

        RuleItemDto result = ruleService.createRule(createRequest);

        assertNotNull(result);
        assertEquals(expectedDto, result);

        assertEquals(unmappedEntity, queryEntity.getRule());

        verify(ruleMapper, times(1)).toEntity(createRequest);
        verify(ruleRepository, times(1)).save(unmappedEntity);
        verify(ruleMapper, times(1)).toItemDto(savedEntity);
    }

    @Test
    @DisplayName("findAll — должен вызвать findAllWithQueries и смаппить полученный список в DTO")
    void findAll_ShouldReturnMappedResponseList() {
        // Given
        RuleEntity entity = new RuleEntity();
        entity.setId(RULE_ID);
        List<RuleEntity> entitiesList = List.of(entity);

        RuleItemDto itemDto = new RuleItemDto(
                RULE_ID.toString(), PRODUCT_ID.toString(),
                "Simple Credit", "description", List.of()
        );
        RuleResponseListDto expectedResponse = new RuleResponseListDto(List.of(itemDto));

        when(ruleRepository.findAllWithQueries()).thenReturn(entitiesList);
        when(ruleMapper.toListResponseDto(entitiesList)).thenReturn(expectedResponse);

        RuleResponseListDto result = ruleService.findAll();

        assertNotNull(result);
        assertEquals(expectedResponse, result);

        verify(ruleRepository, times(1)).findAllWithQueries();
        verify(ruleMapper, times(1)).toListResponseDto(entitiesList);
    }

    @Test
    @DisplayName("deleteRule — должен передать UUID репозиторию для удаления")
    void deleteRule_ShouldCallRepositoryDelete() {
        doNothing().when(ruleRepository).deleteById(RULE_ID);
        ruleService.deleteRule(RULE_ID);
        verify(ruleRepository, times(1)).deleteById(RULE_ID);
    }
}