package com.capitec.fraudengine.api.dto;

public record RuleResultResponseDto(
	String ruleCode,
	String ruleName,
	boolean triggered,
	String severity,
	int scoreContribution,
	String reason
) {
}
