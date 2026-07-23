package com.github.bgrebennikov.recommendationservice.data.types;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Операторы сравнения для численных значений (например, BigDecimal).
 *
 * @author Boris
 * @version 1.0
 */
public enum ComparisonOperator {

    GREATER(">") {
        @Override
        public boolean compare(BigDecimal a, BigDecimal b) {
            return a.compareTo(b) > 0;
        }
    },
    LESS("<") {
        @Override
        public boolean compare(BigDecimal a, BigDecimal b) {
            return a.compareTo(b) < 0;
        }
    },
    EQUALS("=") {
        @Override
        public boolean compare(BigDecimal a, BigDecimal b) {
            return a.compareTo(b) == 0;
        }
    },
    GREATER_OR_EQUAL(">=") {
        @Override
        public boolean compare(BigDecimal a, BigDecimal b) {
            return a.compareTo(b) >= 0;
        }
    },
    LESS_OR_EQUAL("<=") {
        @Override
        public boolean compare(BigDecimal a, BigDecimal b) {
            return a.compareTo(b) <= 0;
        }
    };

    private final String symbol;

    ComparisonOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    /**
     * Абстрактный метод сравнения двух чисел.
     */
    public abstract boolean compare(BigDecimal a, BigDecimal b);

    /**
     * Безопасный поиск Enum-значения по его строковому символу (например, ">=").
     *
     * @param symbol строковый символ оператора
     * @return соответствующий {@link ComparisonOperator}
     * @throws IllegalArgumentException если оператор не найден
     */
    public static ComparisonOperator fromSymbol(String symbol) {
        return Arrays.stream(values())
                .filter(op -> op.symbol.equals(symbol))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown operator symbol: " + symbol));
    }
}