package com.oitws.fraudengine.domain.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.oitws.fraudengine.domain.model.enums.FraudDecision;

/**
 * Aggregate domain result for evaluating one transaction event.
 *
 * @param evaluationId persisted or generated evaluation identifier
 * @param transactionEvent evaluated transaction event
 * @param decision outward fraud decision
 * @param decisionScore internal aggregate score
 * @param evaluatedAt point in time when the evaluation completed
 * @param traceSummary concise explanation of the final decision path
 * @param ruleResults detailed per-rule outcomes
 */
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
