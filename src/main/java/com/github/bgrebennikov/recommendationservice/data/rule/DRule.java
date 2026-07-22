package com.github.bgrebennikov.recommendationservice.data.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class DRule {
    @JsonProperty("query")
    private DRuleQuery query;

    @JsonProperty("arguments")
    private List<String> arguments;

    @JsonProperty("negate")
    private Boolean negate;

    public DRule(DRuleQuery query, List<String> arguments, Boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }

    DRule() {
    }

    public List<String> getArguments() {
        return arguments;
    }

    public DRuleQuery getQuery() {
        return query;
    }

    public Boolean getNegate() {
        return negate;
    }


}
