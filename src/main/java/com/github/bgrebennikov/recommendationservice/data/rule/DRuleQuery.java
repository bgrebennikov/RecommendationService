package com.github.bgrebennikov.recommendationservice.data.rule;

/**
 * Перечисление поддерживаемых типов условий (запросов) для динамических правил.
 *
 * @author Boris
 * @version 1.0
 */
public enum DRuleQuery {
    USER_OF,
    ACTIVE_USER_OF,
    TRANSACTION_SUM_COMPARE,
    TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW;

}