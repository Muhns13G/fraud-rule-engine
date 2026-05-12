package com.capitec.fraudengine.domain.rule.impl;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.rule.AbstractFraudRule;
import com.capitec.fraudengine.domain.rule.FraudRuleContext;

/**
 * Placeholder rule for evaluating transactions at suspicious times of day.
 */
public class UnusualTimeFraudRule extends AbstractFraudRule {

	public UnusualTimeFraudRule() {
		super("UNUSUAL_TIME", "Unusual Time Rule");
	}

	@Override
	public RuleEvaluationResult evaluate(FraudRuleContext context) {
		return result(false, RuleSeverity.INFO, 0, "Unusual time rule logic not implemented yet.");
	}
}
