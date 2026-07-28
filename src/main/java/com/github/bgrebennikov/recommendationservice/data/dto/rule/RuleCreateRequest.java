package com.github.bgrebennikov.recommendationservice.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

/**
 * DTO-запрос для создания нового динамического правила рекомендаций.
 *
 * @author Boris
 * @version 1.0
 */
public class RuleCreateRequest {

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("product_text")
    private String productText;

    @JsonProperty("rule")
    private List<RuleQueryRequest> rule;

    /**
     * Конструктор с полным набором параметров.
     *
     * @param productName Название рекомендуемого продукта
     * @param productId   Уникальный идентификатор продукта (UUID)
     * @param productText Описание или рекламный текст рекомендации
     * @param rule        Список условий (динамических правил), определяющих выдачу продукта
     */
    public RuleCreateRequest(String productName, UUID productId, String productText, List<RuleQueryRequest> rule) {
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.rule = rule;
    }

    /**
     * Конструктор по умолчанию для десериализации Jackson.
     */
    RuleCreateRequest() {}

    /**
     * Возвращает уникальный идентификатор продукта.
     *
     * @return Идентификатор продукта
     */
    public UUID getProductId() {
        return productId;
    }

    /**
     * Возвращает название рекомендуемого продукта.
     *
     * @return Название продукта
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Возвращает рекламный текст рекомендации.
     *
     * @return Текст рекомендации
     */
    public String getProductText() {
        return productText;
    }

    /**
     * Возвращает список динамических условий правила.
     *
     * @return Список условий {@link RuleQueryRequest}
     */
    public List<RuleQueryRequest> getRule() {
        return rule;
    }
}