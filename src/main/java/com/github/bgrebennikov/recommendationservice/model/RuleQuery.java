package com.github.bgrebennikov.recommendationservice.model;

import com.github.bgrebennikov.recommendationservice.data.rule.DRuleQuery;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rule_query")
public class RuleQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "query_type", nullable = false)
    private DRuleQuery queryType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "arguments", columnDefinition = "JSONB")
    private List<String> arguments;

    @Column(nullable = false)
    private Boolean negate;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @ManyToOne
    @JoinColumn(name = "rule_id", nullable = false)
    private RuleEntity rule;


    public RuleQuery() {}

    public RuleQuery(DRuleQuery queryType, List<String> arguments, Boolean negate, Integer sortOrder) {
        this.queryType = queryType;
        this.arguments = arguments;
        this.negate = negate;
        this.sortOrder = sortOrder;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public DRuleQuery getQueryType() { return queryType; }
    public void setQueryType(DRuleQuery queryType) { this.queryType = queryType; }

    public List<String> getArguments() { return arguments; }
    public void setArguments(List<String> arguments) { this.arguments = arguments; }

    public Boolean getNegate() { return negate; }
    public void setNegate(Boolean negate) { this.negate = negate; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public RuleEntity getRule() { return rule; }
    public void setRule(RuleEntity rule) { this.rule = rule; }
}

