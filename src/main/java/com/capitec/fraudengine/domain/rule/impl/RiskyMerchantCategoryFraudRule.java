package com.capitec.fraudengine.domain.rule.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.enums.MerchantCategory;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.rule.AbstractFraudRule;
import com.capitec.fraudengine.domain.rule.FraudRuleContext;
import com.capitec.fraudengine.infrastructure.config.FraudRuleProperties;

/**
 * Flags transactions that occur in categories treated as higher-risk for the initial slice.
 */
@Component
public class RiskyMerchantCategoryFraudRule extends AbstractFraudRule {

	private final Set<MerchantCategory> flaggedCategories;

	public RiskyMerchantCategoryFraudRule(FraudRuleProperties fraudRuleProperties) {
		super("RISKY_MERCHANT_CATEGORY", "Risky Merchant Category Rule");
		this.flaggedCategories = fraudRuleProperties.getRiskyMerchantCategory()
			.getFlaggedCategories()
			.stream()
			.collect(Collectors.toUnmodifiableSet());
	}

	@Override
	public RuleEvaluationResult evaluate(FraudRuleContext context) {
		MerchantCategory merchantCategory = context.transactionEvent().merchantCategory();

		if (flaggedCategories.contains(merchantCategory)) {
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
