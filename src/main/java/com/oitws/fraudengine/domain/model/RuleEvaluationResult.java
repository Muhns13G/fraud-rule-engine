package com.oitws.fraudengine.domain.model;

import com.oitws.fraudengine.domain.model.enums.RuleSeverity;

/**
 * Outcome of evaluating one fraud rule against a transaction.
 *
 * @param ruleCode stable machine-readable rule identifier
 * @param ruleName human-readable rule name
 * @param triggered whether the rule was triggered
 * @param severity severity assigned by the rule
 * @param scoreContribution score contributed by the rule
 * @param reason explanation of the rule outcome
 */
public record RuleEvaluationResult(
	String ruleCode,
	String ruleName,
	boolean triggered,
	RuleSeverity severity,
	int scoreContribution,
	String reason
) {
}
