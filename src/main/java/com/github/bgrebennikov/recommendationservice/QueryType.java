package com.github.bgrebennikov.recommendationservice.enums;

public enum QueryType {
    /**
     * Является ли пользователем продукта (например, имеет карту DEBIT)
     */
    USER_OF,

    /**
     * Является ли активным пользователем продукта
     */
    ACTIVE_USER_OF,

    /**
     * Сравнение суммы транзакций (например, сумма пополнений > 10 000)
     */
    TRANSACTION_SUM_COMPARE,

    /**
     * Сравнение количества транзакций (например, совершил более 5 покупок)
     */
    TRANSACTION_COUNT_COMPARE
}