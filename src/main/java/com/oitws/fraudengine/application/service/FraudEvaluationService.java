package com.oitws.fraudengine.application.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.oitws.fraudengine.api.dto.FraudEvaluationRequestDto;
import com.oitws.fraudengine.application.mapper.FraudEvaluationApplicationMapper;
import com.oitws.fraudengine.domain.model.FraudEvaluation;
import com.oitws.fraudengine.domain.model.RuleEvaluationResult;
import com.oitws.fraudengine.domain.model.TransactionEvent;
import com.oitws.fraudengine.domain.policy.FraudDecisionPolicy;
import com.oitws.fraudengine.domain.policy.FraudDecisionPolicyResult;
import com.oitws.fraudengine.domain.rule.FraudRule;
import com.oitws.fraudengine.domain.rule.FraudRuleContext;
import com.oitws.fraudengine.infrastructure.config.FraudRuleProperties;
import com.oitws.fraudengine.infrastructure.persistence.mapper.FraudEvaluationPersistenceMapper;
import com.oitws.fraudengine.infrastructure.persistence.repository.FraudEvaluationJpaRepository;

/**
 * Application service that orchestrates rule execution and decision aggregation for one request.
 */
@Service
public class FraudEvaluationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FraudEvaluationService.class);

	private final List<FraudRule> fraudRules;
	private final FraudDecisionPolicy fraudDecisionPolicy;
	private final FraudEvaluationApplicationMapper fraudEvaluationApplicationMapper;
	private final FraudEvaluationPersistenceMapper fraudEvaluationPersistenceMapper;
	private final FraudEvaluationJpaRepository fraudEvaluationJpaRepository;
	private final MeterRegistry meterRegistry;
	private final Timer evaluationDurationTimer;
	private final Duration velocityWindow;

	public FraudEvaluationService(
		List<FraudRule> fraudRules,
		FraudDecisionPolicy fraudDecisionPolicy,
		FraudEvaluationApplicationMapper fraudEvaluationApplicationMapper,
		FraudEvaluationPersistenceMapper fraudEvaluationPersistenceMapper,
		FraudEvaluationJpaRepository fraudEvaluationJpaRepository,
		MeterRegistry meterRegistry,
		FraudRuleProperties fraudRuleProperties
	) {
		this.fraudRules = fraudRules;
		this.fraudDecisionPolicy = fraudDecisionPolicy;
		this.fraudEvaluationApplicationMapper = fraudEvaluationApplicationMapper;
		this.fraudEvaluationPersistenceMapper = fraudEvaluationPersistenceMapper;
		this.fraudEvaluationJpaRepository = fraudEvaluationJpaRepository;
		this.meterRegistry = meterRegistry;
		this.velocityWindow = Duration.ofMinutes(fraudRuleProperties.getVelocity().getWindowMinutes());
		this.evaluationDurationTimer = Timer.builder("fraud.evaluation.duration")
			.description("End-to-end duration of one fraud evaluation request.")
			.register(meterRegistry);
	}

	/**
	 * Evaluates a request, loads recent persisted transaction history, and saves the aggregated result.
	 *
	 * @param request request DTO to evaluate
	 * @return persisted fraud evaluation result
	 */
	public FraudEvaluation evaluate(FraudEvaluationRequestDto request) {
		Timer.Sample timerSample = Timer.start(meterRegistry);
		try {
			TransactionEvent transactionEvent = fraudEvaluationApplicationMapper.toDomain(request);
			LOGGER.info(
				"fraud_evaluation_started transactionId={} accountId={} customerId={} channel={} merchantCategory={} eventTimestamp={}",
				transactionEvent.transactionId(),
				transactionEvent.accountId(),
				transactionEvent.customerId(),
				transactionEvent.channel(),
				transactionEvent.merchantCategory(),
				transactionEvent.eventTimestamp()
			);

			List<TransactionEvent> recentTransactions = loadRecentTransactions(transactionEvent);
			FraudRuleContext context = new FraudRuleContext(transactionEvent, recentTransactions);

			List<RuleEvaluationResult> ruleResults = fraudRules.stream()
				.map(rule -> rule.evaluate(context))
				.toList();

			FraudDecisionPolicyResult decision = fraudDecisionPolicy.evaluate(ruleResults);

			FraudEvaluation evaluation = new FraudEvaluation(
				UUID.randomUUID(),
				transactionEvent,
				decision.decision(),
				decision.decisionScore(),
				OffsetDateTime.now(),
				decision.traceSummary(),
				ruleResults
			);

			FraudEvaluation persistedEvaluation = fraudEvaluationPersistenceMapper.toDomain(
				fraudEvaluationJpaRepository.save(fraudEvaluationPersistenceMapper.toEntity(evaluation))
			);

			long triggeredRuleCount = ruleResults.stream()
				.filter(RuleEvaluationResult::triggered)
				.count();

			LOGGER.info(
				"fraud_evaluation_completed evaluationId={} transactionId={} accountId={} decision={} decisionScore={} triggeredRuleCount={} evaluatedAt={}",
				persistedEvaluation.evaluationId(),
				transactionEvent.transactionId(),
				transactionEvent.accountId(),
				persistedEvaluation.decision(),
				persistedEvaluation.decisionScore(),
				triggeredRuleCount,
				persistedEvaluation.evaluatedAt()
			);

			recordEvaluationMetrics(persistedEvaluation);
			return persistedEvaluation;
		}
		finally {
			timerSample.stop(evaluationDurationTimer);
		}
	}

	private List<TransactionEvent> loadRecentTransactions(TransactionEvent transactionEvent) {
		OffsetDateTime windowStart = transactionEvent.eventTimestamp().minus(velocityWindow);

		List<TransactionEvent> recentTransactions = fraudEvaluationJpaRepository.findByAccountIdAndEventTimestampBetween(
			transactionEvent.accountId(),
			windowStart,
			transactionEvent.eventTimestamp()
		).stream()
			.map(fraudEvaluationPersistenceMapper::toDomain)
			.map(FraudEvaluation::transactionEvent)
			.toList();

		LOGGER.debug(
			"fraud_evaluation_history_loaded transactionId={} accountId={} windowStart={} windowEnd={} historyCount={}",
			transactionEvent.transactionId(),
			transactionEvent.accountId(),
			windowStart,
			transactionEvent.eventTimestamp(),
			recentTransactions.size()
		);

		return recentTransactions;
	}

	private void recordEvaluationMetrics(FraudEvaluation evaluation) {
		meterRegistry.counter("fraud.evaluation.completed.total").increment();

		meterRegistry.counter(
			"fraud.evaluation.decision.count",
			"decision",
			evaluation.decision().name()
		).increment();

		evaluation.ruleResults().stream()
			.filter(RuleEvaluationResult::triggered)
			.forEach(ruleResult -> meterRegistry.counter(
				"fraud.evaluation.rule.triggered.count",
				"ruleCode",
				ruleResult.ruleCode(),
				"severity",
				ruleResult.severity().name()
			).increment());
	}
}
