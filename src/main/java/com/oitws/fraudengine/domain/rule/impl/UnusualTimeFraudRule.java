package com.oitws.fraudengine.domain.rule.impl;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

import com.oitws.fraudengine.domain.model.RuleEvaluationResult;
import com.oitws.fraudengine.domain.model.enums.RuleSeverity;
import com.oitws.fraudengine.domain.rule.AbstractFraudRule;
import com.oitws.fraudengine.domain.rule.FraudRuleContext;
import com.oitws.fraudengine.infrastructure.config.FraudRuleProperties;

/**
 * Flags transactions occurring during the configured suspicious overnight window.
 */
@Component
public class UnusualTimeFraudRule extends AbstractFraudRule {

	private final LocalTime windowStart;
	private final LocalTime windowEnd;

	public UnusualTimeFraudRule(FraudRuleProperties fraudRuleProperties) {
		super("UNUSUAL_TIME", "Unusual Time Rule");
		this.windowStart = fraudRuleProperties.getUnusualTime().getStart();
		this.windowEnd = fraudRuleProperties.getUnusualTime().getEnd();
	}

	@Override
	public RuleEvaluationResult evaluate(FraudRuleContext context) {
		LocalTime transactionTime = context.transactionEvent().eventTimestamp().toLocalTime();

		if (!transactionTime.isBefore(windowStart) && transactionTime.isBefore(windowEnd)) {
			return result(
				true,
				RuleSeverity.REVIEW,
				40,
				"Transaction occurred during the unusual time window between " + windowStart + " and " + windowEnd + "."
			);
		}

		return result(false, RuleSeverity.INFO, 0, "Transaction time is outside the unusual overnight window.");
	}
}
