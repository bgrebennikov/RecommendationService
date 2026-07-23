package com.github.bgrebennikov.recommendationservice.data.types;

/**
 * Категории финансовых продуктов, используемые при анализе транзакций и вычислении рекомендаций.
 *
 * @author Boris
 * @version 1.0
 */
public enum ProductType {
    CREDIT, DEBIT, SAVING, INVEST;
}