package com.github.bgrebennikov.recommendationservice.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class PostgresDataSourceConfiguration {

    @Primary
    @Bean(name = "postgresDataSource")
    DataSource dataSource(
            @Value("${application.postgres-db.url}") String url,
            @Value("${application.postgres-db.username}") String username,
            @Value("${application.postgres-db.password}") String password
    ) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(hikariConfig);
    }

    @Primary
    @Bean(name = "postgresJdbcTemplate")
    JdbcTemplate jdbcTemplate(
            @Qualifier("postgresDataSource") DataSource dataSource
    ) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    SpringLiquibase springLiquibase(
            @Qualifier("postgresDataSource") DataSource dataSource
    ) {
        SpringLiquibase lb = new SpringLiquibase();
        lb.setDataSource(dataSource);
        lb.setChangeLog("classpath:db/changelog/db.changelog-master.yaml");
        return lb;
    }


}
