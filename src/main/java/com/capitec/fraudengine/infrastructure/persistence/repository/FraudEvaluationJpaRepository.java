package com.capitec.fraudengine.infrastructure.persistence.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.capitec.fraudengine.domain.model.enums.FraudDecision;
import com.capitec.fraudengine.infrastructure.persistence.entity.FraudEvaluationEntity;

/**
 * Spring Data repository for persisted fraud evaluations.
 */
public interface FraudEvaluationJpaRepository
	extends JpaRepository<FraudEvaluationEntity, UUID>, JpaSpecificationExecutor<FraudEvaluationEntity> {

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
	 * Finds evaluations for a specific customer identifier.
	 *
	 * @param customerId customer identifier
	 * @return matching evaluation entities
	 */
	List<FraudEvaluationEntity> findByCustomerId(String customerId);

	/**
	 * Finds evaluations for a specific transaction identifier.
	 *
	 * @param transactionId transaction identifier
	 * @return matching evaluation entities
	 */
	List<FraudEvaluationEntity> findByTransactionId(String transactionId);

	/**
	 * Finds evaluations completed within a time range.
	 *
	 * @param from range start
	 * @param to range end
	 * @return matching evaluation entities
	 */
	List<FraudEvaluationEntity> findByEvaluatedAtBetween(OffsetDateTime from, OffsetDateTime to);

	/**
	 * Finds evaluations for a specific account completed within a time range.
	 *
	 * @param accountId account identifier
	 * @param from range start
	 * @param to range end
	 * @return matching evaluation entities
	 */
	List<FraudEvaluationEntity> findByAccountIdAndEvaluatedAtBetween(String accountId, OffsetDateTime from, OffsetDateTime to);

	/**
	 * Finds evaluations for a specific decision completed within a time range.
	 *
	 * @param decision final fraud decision
	 * @param from range start
	 * @param to range end
	 * @return matching evaluation entities
	 */
	List<FraudEvaluationEntity> findByDecisionAndEvaluatedAtBetween(FraudDecision decision, OffsetDateTime from, OffsetDateTime to);

	/**
	 * Finds evaluations matching decision and account filters.
	 *
	 * @param decision final fraud decision
	 * @param accountId account identifier
	 * @return matching evaluation entities
	 */
	List<FraudEvaluationEntity> findByDecisionAndAccountId(FraudDecision decision, String accountId);

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

	/**
	 * Finds evaluation snapshots for an account based on the original transaction timestamp.
	 * This supports history-based rules such as velocity checks.
	 *
	 * @param accountId account identifier
	 * @param from transaction time range start
	 * @param to transaction time range end
	 * @return matching evaluation entities
	 */
	List<FraudEvaluationEntity> findByAccountIdAndEventTimestampBetween(String accountId, OffsetDateTime from, OffsetDateTime to);
}
