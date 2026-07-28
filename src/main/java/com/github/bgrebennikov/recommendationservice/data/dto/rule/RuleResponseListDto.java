package com.github.bgrebennikov.recommendationservice.data.dto.rule;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record RuleResponseListDto(
        List<RuleItemDto> data
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
