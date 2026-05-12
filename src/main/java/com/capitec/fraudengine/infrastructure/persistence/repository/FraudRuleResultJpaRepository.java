package com.capitec.fraudengine.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capitec.fraudengine.infrastructure.persistence.entity.FraudRuleResultEntity;

public interface FraudRuleResultJpaRepository extends JpaRepository<FraudRuleResultEntity, Long> {

	List<FraudRuleResultEntity> findByFraudEvaluation_EvaluationId(UUID evaluationId);
}
