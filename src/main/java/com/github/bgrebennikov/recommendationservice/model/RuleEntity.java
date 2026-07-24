package com.github.bgrebennikov.recommendationservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Konstantin, Boris
 * @version 1.0
 * Сущность динамического правила для формирования рекомендаций продуктов
 * Содержит информацию о рекомендуемом продукте и список условий (запросов),
 * по которым определяется, подходит ли продукт конкретному пользователю.
 */
@Entity
@Table(name = "rule_entity")
public class RuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_text", columnDefinition = "TEXT")
    private String productText;

    @JsonManagedReference
    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<RuleQuery> queries;

    public RuleEntity() {
    }

    public RuleEntity(String productName, UUID productId, String productText, List<RuleQuery> queries) {
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.queries = queries;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public List<RuleQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<RuleQuery> queries) {
        this.queries = queries != null ? queries : new ArrayList<>();
    }


    /**
     * Вспомогательный метод для добавления условия в правило.
     * <p>
     * Синхронизирует обе стороны двунаправленной связи OneToMany:
     * добавляет элемент в текущую коллекцию и устанавливает ссылку на текущее правило в RuleQuery.
     *
     * @param query Добавляемое условие (запрос)
     */
    public void addQuery(RuleQuery query) {
        if (this.queries == null) {
            this.queries = new ArrayList<>();
        }
        this.queries.add(query);
        query.setRule(this);
    }

    /**
     * Вспомогательный метод для удаления условия из правила.
     * <p>
     * Синхронизирует обе стороны двунаправленной связи OneToMany:
     * удаляет элемент из коллекции и обнуляет ссылку на правило в RuleQuery.
     *
     * @param query Удаляемое условие (запрос)
     */
    public void removeQuery(RuleQuery query) {
        if (this.queries != null) {
            this.queries.remove(query);
            query.setRule(null);
        }
    }
}

