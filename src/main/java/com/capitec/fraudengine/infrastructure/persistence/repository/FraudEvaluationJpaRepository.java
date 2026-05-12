package com.capitec.fraudengine.infrastructure.persistence.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capitec.fraudengine.domain.model.enums.FraudDecision;
import com.capitec.fraudengine.infrastructure.persistence.entity.FraudEvaluationEntity;

public interface FraudEvaluationJpaRepository extends JpaRepository<FraudEvaluationEntity, UUID> {

	List<FraudEvaluationEntity> findByDecision(FraudDecision decision);

	List<FraudEvaluationEntity> findByAccountId(String accountId);

	List<FraudEvaluationEntity> findByEvaluatedAtBetween(OffsetDateTime from, OffsetDateTime to);

	List<FraudEvaluationEntity> findByDecisionAndAccountIdAndEvaluatedAtBetween(
		FraudDecision decision,
		String accountId,
		OffsetDateTime from,
		OffsetDateTime to
	);
}
