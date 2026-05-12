package com.capitec.fraudengine.domain.rule.impl;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.enums.MerchantCategory;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.rule.AbstractFraudRule;
import com.capitec.fraudengine.domain.rule.FraudRuleContext;

/**
 * Flags transactions that occur in categories treated as higher-risk for the initial slice.
 */
@Component
public class RiskyMerchantCategoryFraudRule extends AbstractFraudRule {

	private static final Set<MerchantCategory> FLAGGED_CATEGORIES = Set.of(
		MerchantCategory.GAMBLING,
		MerchantCategory.CRYPTO,
		MerchantCategory.MONEY_TRANSFER
	);

	public RiskyMerchantCategoryFraudRule() {
		super("RISKY_MERCHANT_CATEGORY", "Risky Merchant Category Rule");
	}

	@Override
	public RuleEvaluationResult evaluate(FraudRuleContext context) {
		MerchantCategory merchantCategory = context.transactionEvent().merchantCategory();

		if (FLAGGED_CATEGORIES.contains(merchantCategory)) {
			return result(
				true,
				RuleSeverity.REVIEW,
				40,
				"Transaction belongs to a flagged merchant category: " + merchantCategory + "."
			);
		}

		return result(false, RuleSeverity.INFO, 0, "Merchant category is not flagged by the initial fraud rules.");
	}
}
