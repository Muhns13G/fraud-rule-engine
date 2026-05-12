package com.capitec.fraudengine.domain.rule.impl;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.rule.AbstractFraudRule;
import com.capitec.fraudengine.domain.rule.FraudRuleContext;

/**
 * Placeholder rule for evaluating unusually large transaction amounts.
 */
public class HighAmountFraudRule extends AbstractFraudRule {

	public HighAmountFraudRule() {
		super("HIGH_AMOUNT", "High Amount Rule");
	}

	@Override
	public RuleEvaluationResult evaluate(FraudRuleContext context) {
		return result(false, RuleSeverity.INFO, 0, "High amount rule logic not implemented yet.");
	}
}
