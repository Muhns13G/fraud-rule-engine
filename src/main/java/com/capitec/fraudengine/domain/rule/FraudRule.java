package com.capitec.fraudengine.domain.rule;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;

public interface FraudRule {

	String ruleCode();

	String ruleName();

	RuleEvaluationResult evaluate(FraudRuleContext context);
}
