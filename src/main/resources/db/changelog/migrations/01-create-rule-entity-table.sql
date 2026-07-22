--liquibase formatted sql
--changeset boris:01-create-rule-entity
CREATE TABLE IF NOT EXISTS rule_entity
(
    id           UUID PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    product_id   UUID         NOT NULL,
    product_text TEXT
);