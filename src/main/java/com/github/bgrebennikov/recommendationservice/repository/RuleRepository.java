package com.github.bgrebennikov.recommendationservice.repository;

import com.github.bgrebennikov.recommendationservice.model.RuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для доступа к сущностям динамических правил рекомендаций {@link RuleEntity}.
 *
 * @author Konstantin
 * @version 1.0
 */
@Repository
public interface RuleRepository extends JpaRepository<RuleEntity, UUID> {

}