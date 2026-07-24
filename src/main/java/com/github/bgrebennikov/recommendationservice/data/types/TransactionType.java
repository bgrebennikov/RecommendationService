package com.github.bgrebennikov.recommendationservice.data.types;

import java.util.Arrays;

/**
 * Типы финансово-банковских транзакций (пополнение / списание).
 *
 * @author Boris
 * @version 1.0
 */
public enum TransactionType {

    DEPOSIT,
    WITHDRAW;

    /**
     * Безопасное получение {@link TransactionType} из строки без учета регистра.
     *
     * @param value строковое представление типа транзакции
     * @return соответствующий {@link TransactionType}
     * @throws IllegalArgumentException если тип не найден
     */
    public static TransactionType fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("TransactionType string must not be null");
        }
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown transaction type: " + value));
    }
}