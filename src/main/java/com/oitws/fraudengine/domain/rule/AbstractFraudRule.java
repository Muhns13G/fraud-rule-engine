package com.oitws.fraudengine.domain.rule;

import com.oitws.fraudengine.domain.model.RuleEvaluationResult;
import com.oitws.fraudengine.domain.model.enums.RuleSeverity;

/**
 * Base support for fraud rules that share standard metadata and result creation.
 */
public abstract class AbstractFraudRule implements FraudRule {

	private final String ruleCode;
	private final String ruleName;

	protected AbstractFraudRule(String ruleCode, String ruleName) {
		this.ruleCode = ruleCode;
		this.ruleName = ruleName;
	}

	@Override
	public String ruleCode() {
		return ruleCode;
	}

	@Override
	public String ruleName() {
		return ruleName;
	}

	protected RuleEvaluationResult result(boolean triggered, RuleSeverity severity, int scoreContribution, String reason) {
		return new RuleEvaluationResult(ruleCode(), ruleName(), triggered, severity, scoreContribution, reason);
	}
}
