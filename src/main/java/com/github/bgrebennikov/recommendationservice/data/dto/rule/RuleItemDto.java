package com.github.bgrebennikov.recommendationservice.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record RuleItemDto(
        String id,
        @JsonProperty("product_id")
        String productId,
        @JsonProperty("product_name")
        String productName,
        @JsonProperty("product_text")
        String productText,
        @JsonProperty("rule")
        List<RuleQueryResponseDto> queries
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
