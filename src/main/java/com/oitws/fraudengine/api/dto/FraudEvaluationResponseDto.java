package com.oitws.fraudengine.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

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
	@Schema(description = "Persisted fraud evaluation identifier.", example = "8d8bb978-13df-42b0-8e82-4eb809daef55")
	UUID evaluationId,
	@Schema(description = "Source transaction identifier.", example = "txn-20260512-0001")
	String transactionId,
	@Schema(description = "Outward fraud decision.", example = "BLOCK")
	String decision,
	@Schema(description = "Aggregate internal score used for traceability.", example = "100")
	int decisionScore,
	@Schema(description = "Point in time when the evaluation completed.", example = "2026-05-12T10:00:01+02:00")
	OffsetDateTime evaluatedAt,
	@Schema(description = "Concise explanation of why the final decision was reached.", example = "Decision BLOCK with score 100 based on triggered rules: HIGH_AMOUNT.")
	String traceSummary,
	@Schema(description = "Detailed per-rule evaluation results.")
	List<RuleResultResponseDto> ruleResults
) {
}
