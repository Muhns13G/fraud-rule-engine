package com.capitec.fraudengine.domain.rule.impl;

import java.time.Duration;
import java.time.OffsetDateTime;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.TransactionEvent;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.rule.AbstractFraudRule;
import com.capitec.fraudengine.domain.rule.FraudRuleContext;

/**
 * Flags accounts that have too many recent transactions within the configured time window.
 */
public class VelocityFraudRule extends AbstractFraudRule {

	private static final int VELOCITY_LIMIT = 3;
	private static final Duration VELOCITY_WINDOW = Duration.ofMinutes(5);

	public VelocityFraudRule() {
		super("VELOCITY", "Velocity Rule");
	}

	@Override
	public RuleEvaluationResult evaluate(FraudRuleContext context) {
		TransactionEvent currentTransaction = context.transactionEvent();
		OffsetDateTime currentTimestamp = currentTransaction.eventTimestamp();

		long matchingRecentTransactions = context.recentTransactions().stream()
			.filter(previousTransaction -> previousTransaction.accountId().equals(currentTransaction.accountId()))
			.filter(previousTransaction -> !previousTransaction.transactionId().equals(currentTransaction.transactionId()))
			.filter(previousTransaction -> {
				Duration age = Duration.between(previousTransaction.eventTimestamp(), currentTimestamp).abs();
				return age.compareTo(VELOCITY_WINDOW) <= 0;
			})
			.count();

		long totalTransactionsInWindow = matchingRecentTransactions + 1;

		if (totalTransactionsInWindow >= VELOCITY_LIMIT) {
			return result(
				true,
				RuleSeverity.REVIEW,
				40,
				"Account has " + totalTransactionsInWindow + " transactions within 5 minutes."
			);
		}

		return result(false, RuleSeverity.INFO, 0, "Transaction velocity is within the configured threshold.");
	}
}
