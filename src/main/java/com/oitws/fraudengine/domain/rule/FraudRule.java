package com.oitws.fraudengine.domain.rule;

import com.oitws.fraudengine.domain.model.RuleEvaluationResult;

/**
 * Contract for a single deterministic fraud rule.
 */
public interface FraudRule {

	/**
	 * @return stable machine-readable rule identifier
	 */
	String ruleCode();

	/**
	 * @return human-readable rule name
	 */
	String ruleName();

	/**
	 * Evaluates the supplied context and returns a rule-specific outcome.
	 *
	 * @param context transaction and history context for evaluation
	 * @return rule evaluation result
	 */
	RuleEvaluationResult evaluate(FraudRuleContext context);
}
