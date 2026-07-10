package com.github.bgrebennikov.recommendationservice.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class RecommendationsDataSourceConfiguration {

    @Bean(name = "recommendationsDataSource")
    public DataSource recommendationsDataSource(
            @Value("${application.recommendations-db.url}") String recommendationsUrl
    ) {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(recommendationsUrl);
        hikariConfig.setDriverClassName("org.h2.Driver");
        hikariConfig.setReadOnly(true);
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "recommendationsJdbcTemplate")
    public JdbcTemplate recommendationsJdbcTemplate(
            @Qualifier("recommendationsDataSource")
            DataSource dataSource
    ) {
        return new JdbcTemplate(dataSource);
    }
}