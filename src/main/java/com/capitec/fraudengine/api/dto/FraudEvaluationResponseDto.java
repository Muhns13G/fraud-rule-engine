package com.capitec.fraudengine.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

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
