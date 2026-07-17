package com.github.bgrebennikov.recommendationservice.data.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class RuleCreateRequest {
    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("product_text")
    private String productText;

    @JsonProperty("rule")
    private List<DRule> rule;

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductText() {
        return productText;
    }

    public List<DRule> getRule() {
        return rule;
    }
}
