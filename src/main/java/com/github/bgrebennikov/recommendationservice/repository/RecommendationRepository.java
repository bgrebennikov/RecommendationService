package com.github.bgrebennikov.recommendationservice.repository;

import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class RecommendationRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationRepository(
            @Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
