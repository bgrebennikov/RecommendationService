package com.github.bgrebennikov.recommendationservice.repository;

import com.github.bgrebennikov.recommendationservice.data.persistence.RuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для доступа к сущностям динамических правил рекомендаций {@link RuleEntity}.
 *
 * @author Konstantin
 * @version 1.0
 */
@Repository
public interface RuleRepository extends JpaRepository<RuleEntity, UUID> {
    @Query("SELECT DISTINCT r FROM RuleEntity r LEFT JOIN FETCH r.queries")
    List<RuleEntity> findAllWithQueries();
}