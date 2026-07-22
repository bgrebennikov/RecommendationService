package com.github.bgrebennikov.recommendationservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;


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
    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<RuleQuery> queries;

    public RuleEntity() {}

    public RuleEntity(String productName, UUID productId, String productText, List<RuleQuery> queries) {
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.queries = queries;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public String getProductText() { return productText; }
    public void setProductText(String productText) { this.productText = productText; }

    public List<RuleQuery> getQueries() { return queries; }
    public void setQueries(List<RuleQuery> queries) { this.queries = queries; }
}

