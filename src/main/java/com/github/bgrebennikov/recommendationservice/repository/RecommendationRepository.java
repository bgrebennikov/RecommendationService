package com.github.bgrebennikov.recommendationservice.repository;

import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Репозиторий доступа к базе знаний о транзакциях и финансовых продуктах пользователей.
 * <p>
 * Выполняет агрегационные SQL-запросы к истории транзакций. Результаты вычислений
 * кешируются в Redis для снижения нагрузки на БД при частых проверках правил.
 *
 * @author Ekaterina, Boris
 * @version 1.0
 */
@Repository
public class RecommendationRepository {

    private final JdbcTemplate jdbcTemplate;

    public RecommendationRepository(
            @Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Проверяет, пользуется ли пользователь указанным типом банковского продукта.
     * <p>
     * Результат кешируется в Redis с ключом {@code user-products::<userId>:<productType>}.
     *
     * @param userId      Идентификатор пользователя
     * @param productType Тип банковского продукта
     * @return {@code true}, если у пользователя есть транзакции по указанному продукту
     */
    @Cacheable(value = "user-products", key = "#userId.toString() + ':' + #productType.name()")
    public boolean hasProductType(UUID userId, ProductType productType) {
        String sql = """
                SELECT COUNT(*)
                FROM TRANSACTIONS t
                         JOIN PRODUCTS p ON t.PRODUCT_ID = p.id
                WHERE t.USER_ID = ?
                  AND p.TYPE = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, productType.toString());
        return count != null && count > 0;
    }

    /**
     * Вычисляет общую сумму пополнений (DEPOSIT) пользователя по конкретному типу продукта.
     * <p>
     * Результат кешируется в Redis с ключом {@code user-deposits::<userId>:<productType>}.
     *
     * @param userId      Идентификатор пользователя
     * @param productType Тип банковского продукта
     * @return Сумма пополнений или {@code BigDecimal.ZERO}, если транзакций не найдено
     */
    @Cacheable(value = "user-deposits", key = "#userId.toString() + ':' + #productType.name()")
    public BigDecimal sumOfDepositsByType(UUID userId, ProductType productType) {
        String sql = """
                SELECT COALESCE(SUM(t.amount), 0)
                FROM TRANSACTIONS t
                JOIN products p ON t.PRODUCT_ID = p.ID
                WHERE t.USER_ID = ? AND p.TYPE = ? AND t.TYPE = 'DEPOSIT'
                """;
        return jdbcTemplate.queryForObject(
                sql, BigDecimal.class, userId, productType.toString()
        );
    }

    /**
     * Вычисляет общую сумму списаний (WITHDRAW) пользователя по конкретному типу продукта.
     * <p>
     * Результат кешируется в Redis с ключом {@code user-withdrawals::<userId>:<productType>}.
     *
     * @param userId      Идентификатор пользователя
     * @param productType Тип банковского продукта
     * @return Сумма списаний или {@code BigDecimal.ZERO}, если транзакций не найдено
     */
    @Cacheable(value = "user-withdrawals", key = "#userId.toString() + ':' + #productType.name()")
    public BigDecimal sumOfWithdrawalsByType(UUID userId, ProductType productType) {
        String sql = """
                SELECT COALESCE(SUM(t.AMOUNT), 0)
                FROM TRANSACTIONS t
                JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
                WHERE t.USER_ID = ? AND p.type = ? AND t.TYPE = 'WITHDRAW'
                """;
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, productType.toString());
    }
}