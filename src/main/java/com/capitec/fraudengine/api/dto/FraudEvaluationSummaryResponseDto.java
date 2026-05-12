package com.capitec.fraudengine.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FraudEvaluationSummaryResponseDto(
	UUID evaluationId,
	String transactionId,
	String accountId,
	String decision,
	int decisionScore,
	OffsetDateTime evaluatedAt
) {
}
