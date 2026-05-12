package com.capitec.fraudengine.domain.rule.impl;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.rule.AbstractFraudRule;
import com.capitec.fraudengine.domain.rule.FraudRuleContext;

/**
 * Flags transactions occurring during the configured suspicious overnight window.
 */
@Component
public class UnusualTimeFraudRule extends AbstractFraudRule {

	private static final LocalTime WINDOW_START = LocalTime.MIDNIGHT;
	private static final LocalTime WINDOW_END = LocalTime.of(4, 0);

	public UnusualTimeFraudRule() {
		super("UNUSUAL_TIME", "Unusual Time Rule");
	}

	@Override
	public RuleEvaluationResult evaluate(FraudRuleContext context) {
		LocalTime transactionTime = context.transactionEvent().eventTimestamp().toLocalTime();

		if (!transactionTime.isBefore(WINDOW_START) && transactionTime.isBefore(WINDOW_END)) {
			return result(
				true,
				RuleSeverity.REVIEW,
				40,
				"Transaction occurred during the unusual time window between 00:00 and 04:00."
			);
		}

		return result(false, RuleSeverity.INFO, 0, "Transaction time is outside the unusual overnight window.");
	}
}
