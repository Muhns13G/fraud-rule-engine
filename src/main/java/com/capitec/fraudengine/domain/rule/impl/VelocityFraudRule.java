package com.capitec.fraudengine.domain.rule.impl;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.rule.AbstractFraudRule;
import com.capitec.fraudengine.domain.rule.FraudRuleContext;

/**
 * Placeholder rule for evaluating suspicious transaction frequency.
 */
public class VelocityFraudRule extends AbstractFraudRule {

	public VelocityFraudRule() {
		super("VELOCITY", "Velocity Rule");
	}

	@Override
	public RuleEvaluationResult evaluate(FraudRuleContext context) {
		return result(false, RuleSeverity.INFO, 0, "Velocity rule logic not implemented yet.");
	}
}
