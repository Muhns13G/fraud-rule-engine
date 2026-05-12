package com.capitec.fraudengine.api.dto;

/**
 * Response view of one fraud rule evaluation result.
 *
 * @param ruleCode stable machine-readable rule identifier
 * @param ruleName human-readable rule name
 * @param triggered whether the rule was triggered
 * @param severity severity assigned by the rule
 * @param scoreContribution score contributed by the rule
 * @param reason explanation for the rule outcome
 */
public record RuleResultResponseDto(
	String ruleCode,
	String ruleName,
	boolean triggered,
	String severity,
	int scoreContribution,
	String reason
) {
}
