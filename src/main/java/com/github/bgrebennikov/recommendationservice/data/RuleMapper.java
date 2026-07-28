package com.github.bgrebennikov.recommendationservice.data;

import com.github.bgrebennikov.recommendationservice.data.dto.rule.*;
import com.github.bgrebennikov.recommendationservice.data.persistence.RuleEntity;
import com.github.bgrebennikov.recommendationservice.data.persistence.RuleQueryEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


/**
 * Маппер для преобразования сущностей правил рекомендаций ({@link RuleEntity}, {@link RuleQueryEntity})
 * в соответствующие DTO и обратно.
 * <p>
 * Использует MapStruct для автоматической генерации реализации в Spring-контексте.
 *
 * @author Boris
 * @version 1.0
 */
@Mapper(
        componentModel = SPRING
)
public interface RuleMapper {

    RuleItemDto toItemDto(RuleEntity ruleEntity);

    default RuleResponseListDto toListResponseDto(List<RuleEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return new RuleResponseListDto(List.of());
        }

        List<RuleItemDto> items = entities.stream()
                .map(this::toItemDto)
                .toList();

        return new RuleResponseListDto(items);
    }

    @AfterMapping
    default void linkQueriesAndSetSortOrder(@MappingTarget RuleEntity entity) {
        if (entity.getQueries() != null) {
            for (int i = 0; i < entity.getQueries().size(); i++) {
                RuleQueryEntity query = entity.getQueries().get(i);
                query.setRule(entity);
                query.setSortOrder(i);
            }
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "queries", source = "rule")
    RuleEntity toEntity(RuleCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rule", ignore = true)
    @Mapping(target = "sortOrder", ignore = true)
    @Mapping(target = "queryType", source = "query")
    RuleQueryEntity toQueryEntity(RuleQueryRequest ruleEntity);

}
