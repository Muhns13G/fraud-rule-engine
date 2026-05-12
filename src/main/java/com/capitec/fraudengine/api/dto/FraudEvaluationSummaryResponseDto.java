package com.capitec.fraudengine.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Lightweight list view of a fraud evaluation for review queries.
 *
 * @param evaluationId persisted evaluation identifier
 * @param transactionId source transaction identifier
 * @param accountId account associated with the evaluation
 * @param decision outward fraud decision
 * @param decisionScore internal aggregate score used for ranking or triage
 * @param evaluatedAt point in time when the evaluation completed
 */
public record FraudEvaluationSummaryResponseDto(
	UUID evaluationId,
	String transactionId,
	String accountId,
	String decision,
	int decisionScore,
	OffsetDateTime evaluatedAt
) {
}
