package com.github.bgrebennikov.recommendationservice.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO отдельного динамического условия для расчета рекомендаций.
 *
 * @author Boris
 * @version 1.0
 */
public class RuleQueryRequest {

    @JsonProperty("query")
    private DRuleQuery query;

    @JsonProperty("arguments")
    private List<String> arguments;

    @JsonProperty("negate")
    private Boolean negate;

    /**
     * @param query     Тип бизнес-проверки
     * @param arguments Параметры проверки (например: тип продукта, минимальная сумма, валюта)
     * @param negate    Флаг инверсии (true — правило сработает, если проверка НЕ выполнена)
     */
    public RuleQueryRequest(DRuleQuery query, List<String> arguments, Boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }

    RuleQueryRequest() {
    }

    /**
     * Позиционные параметры для выполнения {@link #getQuery()}.
     * <p>
     * Содержимое зависит от типа запроса. Например, для сравнения сумм транзакций
     * ожидаются значения: {@code [продукт, оператор (>, <), сумма]}.
     */
    public List<String> getArguments() {
        return arguments;
    }

    public DRuleQuery getQuery() {
        return query;
    }

    /**
     * Определяет, нужно ли инвертировать результат проверки (NOT).
     *
     * @return {@code true} если условие должно работать как отрицание; {@code false} или {@code null},
     * если выполняется прямое условие.
     */
    public Boolean getNegate() {
        return negate;
    }
}