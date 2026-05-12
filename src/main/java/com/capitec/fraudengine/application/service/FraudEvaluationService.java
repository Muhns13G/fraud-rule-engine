package com.capitec.fraudengine.application.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.capitec.fraudengine.api.dto.FraudEvaluationRequestDto;
import com.capitec.fraudengine.application.mapper.FraudEvaluationApplicationMapper;
import com.capitec.fraudengine.domain.model.FraudEvaluation;
import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.TransactionEvent;
import com.capitec.fraudengine.domain.policy.FraudDecisionPolicy;
import com.capitec.fraudengine.domain.policy.FraudDecisionPolicyResult;
import com.capitec.fraudengine.domain.rule.FraudRule;
import com.capitec.fraudengine.domain.rule.FraudRuleContext;
import com.capitec.fraudengine.infrastructure.persistence.mapper.FraudEvaluationPersistenceMapper;
import com.capitec.fraudengine.infrastructure.persistence.repository.FraudEvaluationJpaRepository;

/**
 * Application service that orchestrates rule execution and decision aggregation for one request.
 */
@Service
public class FraudEvaluationService {

	private static final Duration VELOCITY_WINDOW = Duration.ofMinutes(5);

	private final List<FraudRule> fraudRules;
	private final FraudDecisionPolicy fraudDecisionPolicy;
	private final FraudEvaluationApplicationMapper fraudEvaluationApplicationMapper;
	private final FraudEvaluationPersistenceMapper fraudEvaluationPersistenceMapper;
	private final FraudEvaluationJpaRepository fraudEvaluationJpaRepository;

	public FraudEvaluationService(
		List<FraudRule> fraudRules,
		FraudDecisionPolicy fraudDecisionPolicy,
		FraudEvaluationApplicationMapper fraudEvaluationApplicationMapper,
		FraudEvaluationPersistenceMapper fraudEvaluationPersistenceMapper,
		FraudEvaluationJpaRepository fraudEvaluationJpaRepository
	) {
		this.fraudRules = fraudRules;
		this.fraudDecisionPolicy = fraudDecisionPolicy;
		this.fraudEvaluationApplicationMapper = fraudEvaluationApplicationMapper;
		this.fraudEvaluationPersistenceMapper = fraudEvaluationPersistenceMapper;
		this.fraudEvaluationJpaRepository = fraudEvaluationJpaRepository;
	}

	/**
	 * Evaluates a request, loads recent persisted transaction history, and saves the aggregated result.
	 *
	 * @param request request DTO to evaluate
	 * @return persisted fraud evaluation result
	 */
	public FraudEvaluation evaluate(FraudEvaluationRequestDto request) {
		TransactionEvent transactionEvent = fraudEvaluationApplicationMapper.toDomain(request);
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

		return fraudEvaluationPersistenceMapper.toDomain(
			fraudEvaluationJpaRepository.save(fraudEvaluationPersistenceMapper.toEntity(evaluation))
		);
	}

	private List<TransactionEvent> loadRecentTransactions(TransactionEvent transactionEvent) {
		OffsetDateTime windowStart = transactionEvent.eventTimestamp().minus(VELOCITY_WINDOW);

		return fraudEvaluationJpaRepository.findByAccountIdAndEventTimestampBetween(
			transactionEvent.accountId(),
			windowStart,
			transactionEvent.eventTimestamp()
		).stream()
			.map(fraudEvaluationPersistenceMapper::toDomain)
			.map(FraudEvaluation::transactionEvent)
			.toList();
	}
}
