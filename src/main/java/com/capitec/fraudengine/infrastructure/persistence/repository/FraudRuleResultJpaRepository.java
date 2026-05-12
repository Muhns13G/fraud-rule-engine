package com.capitec.fraudengine.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capitec.fraudengine.infrastructure.persistence.entity.FraudRuleResultEntity;

/**
 * Spring Data repository for persisted fraud rule results.
 */
public interface FraudRuleResultJpaRepository extends JpaRepository<FraudRuleResultEntity, Long> {

	/**
	 * Finds all rule results linked to a persisted evaluation.
	 *
	 * @param evaluationId persisted evaluation identifier
	 * @return matching rule result entities
	 */
	List<FraudRuleResultEntity> findByFraudEvaluation_EvaluationId(UUID evaluationId);
}
