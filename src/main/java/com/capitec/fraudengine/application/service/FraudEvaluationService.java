package com.capitec.fraudengine.application.service;

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

/**
 * Application service that orchestrates rule execution and decision aggregation for one request.
 */
@Service
public class FraudEvaluationService {

	private final List<FraudRule> fraudRules;
	private final FraudDecisionPolicy fraudDecisionPolicy;
	private final FraudEvaluationApplicationMapper fraudEvaluationApplicationMapper;

	public FraudEvaluationService(
		List<FraudRule> fraudRules,
		FraudDecisionPolicy fraudDecisionPolicy,
		FraudEvaluationApplicationMapper fraudEvaluationApplicationMapper
	) {
		this.fraudRules = fraudRules;
		this.fraudDecisionPolicy = fraudDecisionPolicy;
		this.fraudEvaluationApplicationMapper = fraudEvaluationApplicationMapper;
	}

	/**
	 * Evaluates a request using the supplied recent transaction history.
	 *
	 * @param request request DTO to evaluate
	 * @param recentTransactions recent transaction history available to history-based rules
	 * @return aggregated fraud evaluation result
	 */
	public FraudEvaluation evaluate(FraudEvaluationRequestDto request, List<TransactionEvent> recentTransactions) {
		TransactionEvent transactionEvent = fraudEvaluationApplicationMapper.toDomain(request);
		FraudRuleContext context = new FraudRuleContext(transactionEvent, recentTransactions);

		List<RuleEvaluationResult> ruleResults = fraudRules.stream()
			.map(rule -> rule.evaluate(context))
			.toList();

		FraudDecisionPolicyResult decision = fraudDecisionPolicy.evaluate(ruleResults);

		return new FraudEvaluation(
			UUID.randomUUID(),
			transactionEvent,
			decision.decision(),
			decision.decisionScore(),
			OffsetDateTime.now(),
			decision.traceSummary(),
			ruleResults
		);
	}
}
