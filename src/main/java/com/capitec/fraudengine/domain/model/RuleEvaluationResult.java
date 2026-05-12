package com.capitec.fraudengine.domain.model;

import com.capitec.fraudengine.domain.model.enums.RuleSeverity;

public record RuleEvaluationResult(
	String ruleCode,
	String ruleName,
	boolean triggered,
	RuleSeverity severity,
	int scoreContribution,
	String reason
) {
}
