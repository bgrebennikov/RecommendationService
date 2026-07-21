CREATE TABLE IF NOT EXISTS rule_query (
    id UUID PRIMARY KEY,
    rule_id UUID NOT NULL,
    query_type VARCHAR(50) NOT NULL,
    arguments JSONB,
    negate BOOLEAN NOT NULL,
    sort_order INT NOT NULL,
    CONSTRAINT fk_rule_query_rule FOREIGN KEY (rule_id) REFERENCES rule_entity (id) ON DELETE CASCADE
);