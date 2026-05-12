package com.capitec.fraudengine.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Full response payload for a stored fraud evaluation.
 *
 * @param evaluationId persisted evaluation identifier
 * @param transactionId source transaction identifier
 * @param decision outward fraud decision
 * @param decisionScore internal aggregate score used for traceability
 * @param evaluatedAt point in time when the evaluation completed
 * @param traceSummary concise summary of the final decision path
 * @param ruleResults detailed per-rule outcomes
 */
public record FraudEvaluationResponseDto(
	UUID evaluationId,
	String transactionId,
	String decision,
	int decisionScore,
	OffsetDateTime evaluatedAt,
	String traceSummary,
	List<RuleResultResponseDto> ruleResults
) {
}
