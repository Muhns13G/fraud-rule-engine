package com.capitec.fraudengine.domain.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.capitec.fraudengine.domain.model.enums.FraudDecision;

public record FraudEvaluation(
	UUID evaluationId,
	TransactionEvent transactionEvent,
	FraudDecision decision,
	int decisionScore,
	OffsetDateTime evaluatedAt,
	String traceSummary,
	List<RuleEvaluationResult> ruleResults
) {
	public FraudEvaluation {
		ruleResults = List.copyOf(ruleResults);
	}
}
