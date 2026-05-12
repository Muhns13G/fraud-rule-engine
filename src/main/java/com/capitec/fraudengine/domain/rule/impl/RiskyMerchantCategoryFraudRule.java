package com.capitec.fraudengine.domain.rule.impl;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.rule.AbstractFraudRule;
import com.capitec.fraudengine.domain.rule.FraudRuleContext;

/**
 * Placeholder rule for evaluating transactions in flagged merchant categories.
 */
public class RiskyMerchantCategoryFraudRule extends AbstractFraudRule {

	public RiskyMerchantCategoryFraudRule() {
		super("RISKY_MERCHANT_CATEGORY", "Risky Merchant Category Rule");
	}

	@Override
	public RuleEvaluationResult evaluate(FraudRuleContext context) {
		return result(false, RuleSeverity.INFO, 0, "Risky merchant category rule logic not implemented yet.");
	}
}
