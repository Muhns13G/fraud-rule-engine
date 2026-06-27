package com.oitws.fraudengine.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

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
	@Schema(description = "Persisted fraud evaluation identifier.", example = "8d8bb978-13df-42b0-8e82-4eb809daef55")
	UUID evaluationId,
	@Schema(description = "Source transaction identifier.", example = "txn-20260512-0001")
	String transactionId,
	@Schema(description = "Account associated with the evaluation.", example = "account-123")
	String accountId,
	@Schema(description = "Outward fraud decision.", example = "REVIEW")
	String decision,
	@Schema(description = "Aggregate internal score used for triage.", example = "40")
	int decisionScore,
	@Schema(description = "Point in time when the evaluation completed.", example = "2026-05-12T10:00:01+02:00")
	OffsetDateTime evaluatedAt
) {
}
