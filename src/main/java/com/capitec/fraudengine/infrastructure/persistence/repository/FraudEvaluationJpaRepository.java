package com.capitec.fraudengine.infrastructure.persistence.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capitec.fraudengine.domain.model.enums.FraudDecision;
import com.capitec.fraudengine.infrastructure.persistence.entity.FraudEvaluationEntity;

/**
 * Spring Data repository for persisted fraud evaluations.
 */
public interface FraudEvaluationJpaRepository extends JpaRepository<FraudEvaluationEntity, UUID> {

	/**
	 * Finds evaluations by final decision.
	 *
	 * @param decision final fraud decision
	 * @return matching evaluation entities
	 */
	List<FraudEvaluationEntity> findByDecision(FraudDecision decision);

	/**
	 * Finds evaluations for a specific account identifier.
	 *
	 * @param accountId account identifier
	 * @return matching evaluation entities
	 */
	List<FraudEvaluationEntity> findByAccountId(String accountId);

	/**
	 * Finds evaluations completed within a time range.
	 *
	 * @param from range start
	 * @param to range end
	 * @return matching evaluation entities
	 */
	List<FraudEvaluationEntity> findByEvaluatedAtBetween(OffsetDateTime from, OffsetDateTime to);

	/**
	 * Finds evaluations matching all Phase 1 list filter dimensions.
	 *
	 * @param decision final fraud decision
	 * @param accountId account identifier
	 * @param from range start
	 * @param to range end
	 * @return matching evaluation entities
	 */
	List<FraudEvaluationEntity> findByDecisionAndAccountIdAndEvaluatedAtBetween(
		FraudDecision decision,
		String accountId,
		OffsetDateTime from,
		OffsetDateTime to
	);
}
