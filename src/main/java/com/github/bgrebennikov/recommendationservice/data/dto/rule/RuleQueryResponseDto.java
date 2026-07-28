package com.github.bgrebennikov.recommendationservice.data.dto.rule;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record RuleQueryResponseDto(
        String queryType,
        List<String> arguments,
        Boolean negate
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
