package com.capitec.fraudengine.domain.rule.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.rule.AbstractFraudRule;
import com.capitec.fraudengine.domain.rule.FraudRuleContext;
import com.capitec.fraudengine.infrastructure.config.FraudRuleProperties;

/**
 * Flags transactions that cross the agreed Phase 1 amount thresholds.
 */
@Component
public class HighAmountFraudRule extends AbstractFraudRule {

	private final BigDecimal reviewThreshold;
	private final BigDecimal blockThreshold;

	public HighAmountFraudRule(FraudRuleProperties fraudRuleProperties) {
		super("HIGH_AMOUNT", "High Amount Rule");
		this.reviewThreshold = fraudRuleProperties.getHighAmount().getReviewThreshold();
		this.blockThreshold = fraudRuleProperties.getHighAmount().getBlockThreshold();
	}

	@Override
	public RuleEvaluationResult evaluate(FraudRuleContext context) {
		BigDecimal amount = context.transactionEvent().amount();

		if (amount.compareTo(blockThreshold) >= 0) {
			return result(
				true,
				RuleSeverity.BLOCK,
				100,
				"Transaction amount exceeds the block threshold of " + blockThreshold.toPlainString() + " ZAR."
			);
		}

		if (amount.compareTo(reviewThreshold) >= 0) {
			return result(
				true,
				RuleSeverity.REVIEW,
				40,
				"Transaction amount exceeds the review threshold of " + reviewThreshold.toPlainString() + " ZAR."
			);
		}

		return result(false, RuleSeverity.INFO, 0, "Transaction amount is below the configured fraud thresholds.");
	}
}
