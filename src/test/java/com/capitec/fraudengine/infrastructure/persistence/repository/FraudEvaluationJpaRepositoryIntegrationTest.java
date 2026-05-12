package com.capitec.fraudengine.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.capitec.fraudengine.TestcontainersConfiguration;
import com.capitec.fraudengine.domain.model.FraudEvaluation;
import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.TransactionEvent;
import com.capitec.fraudengine.domain.model.TransactionLocation;
import com.capitec.fraudengine.domain.model.enums.FraudDecision;
import com.capitec.fraudengine.domain.model.enums.MerchantCategory;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.model.enums.TransactionChannel;
import com.capitec.fraudengine.domain.model.enums.TransactionType;
import com.capitec.fraudengine.infrastructure.persistence.entity.FraudEvaluationEntity;
import com.capitec.fraudengine.infrastructure.persistence.mapper.FraudEvaluationPersistenceMapper;

@DataJpaTest
@Import({ TestcontainersConfiguration.class, FraudEvaluationPersistenceMapper.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FraudEvaluationJpaRepositoryIntegrationTest {

	@Autowired
	private FraudEvaluationJpaRepository fraudEvaluationJpaRepository;

	@Autowired
	private FraudEvaluationPersistenceMapper fraudEvaluationPersistenceMapper;

	@BeforeEach
	void setUp() {
		fraudEvaluationJpaRepository.deleteAll();
	}

	@Test
	void shouldPersistEvaluationWithRuleResultsAndLoadItBack() {
		FraudEvaluation evaluation = fraudEvaluation(
			UUID.randomUUID(),
			"txn-001",
			"account-123",
			FraudDecision.REVIEW,
			40,
			OffsetDateTime.parse("2026-05-12T10:05:00+02:00"),
			OffsetDateTime.parse("2026-05-12T10:06:00+02:00"),
			List.of(ruleResult("HIGH_AMOUNT", true, RuleSeverity.REVIEW, 40))
		);

		FraudEvaluationEntity saved = fraudEvaluationJpaRepository.save(
			fraudEvaluationPersistenceMapper.toEntity(evaluation)
		);

		FraudEvaluationEntity reloaded = fraudEvaluationJpaRepository.findById(saved.getEvaluationId()).orElseThrow();

		assertThat(reloaded.getEvaluationId()).isEqualTo(evaluation.evaluationId());
		assertThat(reloaded.getTransactionId()).isEqualTo("txn-001");
		assertThat(reloaded.getDecision()).isEqualTo(FraudDecision.REVIEW);
		assertThat(reloaded.getRuleResults())
			.hasSize(1)
			.first()
			.satisfies(ruleResult -> {
				assertThat(ruleResult.getRuleCode()).isEqualTo("HIGH_AMOUNT");
				assertThat(ruleResult.isTriggered()).isTrue();
				assertThat(ruleResult.getSeverity()).isEqualTo(RuleSeverity.REVIEW);
			});
	}

	@Test
	void shouldFilterByAccountIdAndEventTimestampRangeForVelocityHistoryLookups() {
		fraudEvaluationJpaRepository.save(fraudEvaluationPersistenceMapper.toEntity(
			fraudEvaluation(
				UUID.randomUUID(),
				"txn-100",
				"account-velocity",
				FraudDecision.ALLOW,
				0,
				OffsetDateTime.parse("2026-05-12T10:00:00+02:00"),
				OffsetDateTime.parse("2026-05-12T10:00:30+02:00"),
				List.of(ruleResult("HIGH_AMOUNT", false, RuleSeverity.INFO, 0))
			)
		));
		fraudEvaluationJpaRepository.save(fraudEvaluationPersistenceMapper.toEntity(
			fraudEvaluation(
				UUID.randomUUID(),
				"txn-101",
				"account-velocity",
				FraudDecision.REVIEW,
				40,
				OffsetDateTime.parse("2026-05-12T10:03:00+02:00"),
				OffsetDateTime.parse("2026-05-12T10:03:30+02:00"),
				List.of(ruleResult("VELOCITY", true, RuleSeverity.REVIEW, 40))
			)
		));
		fraudEvaluationJpaRepository.save(fraudEvaluationPersistenceMapper.toEntity(
			fraudEvaluation(
				UUID.randomUUID(),
				"txn-102",
				"account-velocity",
				FraudDecision.ALLOW,
				0,
				OffsetDateTime.parse("2026-05-12T10:06:00+02:00"),
				OffsetDateTime.parse("2026-05-12T10:06:30+02:00"),
				List.of(ruleResult("HIGH_AMOUNT", false, RuleSeverity.INFO, 0))
			)
		));
		fraudEvaluationJpaRepository.save(fraudEvaluationPersistenceMapper.toEntity(
			fraudEvaluation(
				UUID.randomUUID(),
				"txn-999",
				"other-account",
				FraudDecision.ALLOW,
				0,
				OffsetDateTime.parse("2026-05-12T10:02:00+02:00"),
				OffsetDateTime.parse("2026-05-12T10:02:30+02:00"),
				List.of(ruleResult("HIGH_AMOUNT", false, RuleSeverity.INFO, 0))
			)
		));

		List<FraudEvaluationEntity> matches = fraudEvaluationJpaRepository.findByAccountIdAndEventTimestampBetween(
			"account-velocity",
			OffsetDateTime.parse("2026-05-12T10:00:00+02:00"),
			OffsetDateTime.parse("2026-05-12T10:05:00+02:00")
		);

		assertThat(matches)
			.extracting(FraudEvaluationEntity::getTransactionId)
			.containsExactlyInAnyOrder("txn-100", "txn-101");
	}

	@Test
	void shouldFilterByDecisionAccountIdAndEvaluatedAtRange() {
		fraudEvaluationJpaRepository.save(fraudEvaluationPersistenceMapper.toEntity(
			fraudEvaluation(
				UUID.randomUUID(),
				"txn-201",
				"account-filter",
				FraudDecision.REVIEW,
				40,
				OffsetDateTime.parse("2026-05-12T08:55:00+02:00"),
				OffsetDateTime.parse("2026-05-12T09:00:00+02:00"),
				List.of(ruleResult("HIGH_AMOUNT", true, RuleSeverity.REVIEW, 40))
			)
		));
		fraudEvaluationJpaRepository.save(fraudEvaluationPersistenceMapper.toEntity(
			fraudEvaluation(
				UUID.randomUUID(),
				"txn-202",
				"account-filter",
				FraudDecision.BLOCK,
				100,
				OffsetDateTime.parse("2026-05-12T09:55:00+02:00"),
				OffsetDateTime.parse("2026-05-12T10:00:00+02:00"),
				List.of(ruleResult("HIGH_AMOUNT", true, RuleSeverity.BLOCK, 100))
			)
		));
		fraudEvaluationJpaRepository.save(fraudEvaluationPersistenceMapper.toEntity(
			fraudEvaluation(
				UUID.randomUUID(),
				"txn-203",
				"other-account",
				FraudDecision.REVIEW,
				40,
				OffsetDateTime.parse("2026-05-12T09:55:00+02:00"),
				OffsetDateTime.parse("2026-05-12T10:00:00+02:00"),
				List.of(ruleResult("HIGH_AMOUNT", true, RuleSeverity.REVIEW, 40))
			)
		));

		List<FraudEvaluationEntity> matches = fraudEvaluationJpaRepository.findByDecisionAndAccountIdAndEvaluatedAtBetween(
			FraudDecision.REVIEW,
			"account-filter",
			OffsetDateTime.parse("2026-05-12T08:59:00+02:00"),
			OffsetDateTime.parse("2026-05-12T09:01:00+02:00")
		);

		assertThat(matches)
			.singleElement()
			.extracting(FraudEvaluationEntity::getTransactionId)
			.isEqualTo("txn-201");
	}

	private FraudEvaluation fraudEvaluation(
		UUID evaluationId,
		String transactionId,
		String accountId,
		FraudDecision decision,
		int decisionScore,
		OffsetDateTime eventTimestamp,
		OffsetDateTime evaluatedAt,
		List<RuleEvaluationResult> ruleResults
	) {
		TransactionEvent transactionEvent = new TransactionEvent(
			transactionId,
			accountId,
			"customer-" + accountId,
			new BigDecimal("1500.00"),
			"ZAR",
			"merchant-123",
			MerchantCategory.RETAIL,
			TransactionType.PURCHASE,
			TransactionChannel.ONLINE,
			eventTimestamp,
			new TransactionLocation("ZA", "Cape Town"),
			"repository-test"
		);

		return new FraudEvaluation(
			evaluationId,
			transactionEvent,
			decision,
			decisionScore,
			evaluatedAt,
			"Repository integration test trace",
			ruleResults
		);
	}

	private RuleEvaluationResult ruleResult(String ruleCode, boolean triggered, RuleSeverity severity, int scoreContribution) {
		return new RuleEvaluationResult(
			ruleCode,
			ruleCode + " Rule",
			triggered,
			severity,
			scoreContribution,
			"Reason for " + ruleCode
		);
	}
}
