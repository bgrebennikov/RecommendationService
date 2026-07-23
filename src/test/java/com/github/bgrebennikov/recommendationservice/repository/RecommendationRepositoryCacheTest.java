package com.github.bgrebennikov.recommendationservice.repository;

import com.github.bgrebennikov.recommendationservice.data.types.ProductType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
public class RecommendationRepositoryCacheTest {

    static GenericContainer<?> redis = new GenericContainer<>("redis:alpine")
            .withExposedPorts(6379);

    static {
        redis.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private RecommendationRepository repository;

    @MockitoBean(name = "recommendationsJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCacheProductCheckInRedis() {
        UUID userId = UUID.randomUUID();
        Mockito.when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), any(), any()))
                .thenReturn(1);

        boolean q1 = repository.hasProductType(userId, ProductType.DEBIT);
        boolean q2 = repository.hasProductType(userId, ProductType.DEBIT);

        assertTrue(q1);
        assertTrue(q2);

        Mockito.verify(
                jdbcTemplate,
                Mockito.times(1)
        ).queryForObject(anyString(), eq(Integer.class), any(), any());

    }


}
