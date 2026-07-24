package com.github.bgrebennikov.recommendationservice.data.dto.rule;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record RuleItemDto(
        String id,
        String productId,
        String productName,
        String productText,
        List<RuleQueryDto> queries
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
