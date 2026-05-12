package com.capitec.fraudengine.domain.rule.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.rule.AbstractFraudRule;
import com.capitec.fraudengine.domain.rule.FraudRuleContext;

/**
 * Flags transactions that cross the agreed Phase 1 amount thresholds.
 */
@Component
public class HighAmountFraudRule extends AbstractFraudRule {

	private static final BigDecimal REVIEW_THRESHOLD = new BigDecimal("10000.00");
	private static final BigDecimal BLOCK_THRESHOLD = new BigDecimal("25000.00");

	public HighAmountFraudRule() {
		super("HIGH_AMOUNT", "High Amount Rule");
	}

	@Override
	public RuleEvaluationResult evaluate(FraudRuleContext context) {
		BigDecimal amount = context.transactionEvent().amount();

		if (amount.compareTo(BLOCK_THRESHOLD) >= 0) {
			return result(
				true,
				RuleSeverity.BLOCK,
				100,
				"Transaction amount exceeds the block threshold of 25000.00 ZAR."
			);
		}

		if (amount.compareTo(REVIEW_THRESHOLD) >= 0) {
			return result(
				true,
				RuleSeverity.REVIEW,
				40,
				"Transaction amount exceeds the review threshold of 10000.00 ZAR."
			);
		}

		return result(false, RuleSeverity.INFO, 0, "Transaction amount is below the configured fraud thresholds.");
	}
}
