package com.github.bgrebennikov.recommendationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


@Configuration
public class PostgresDataSourceConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties defaultDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "defaultDataSource")
    public DataSource defaultDataSource() {
        return defaultDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }
}