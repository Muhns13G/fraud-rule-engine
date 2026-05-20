package com.capitec.fraudengine.domain.rule.impl;

import java.time.Duration;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.TransactionEvent;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.rule.AbstractFraudRule;
import com.capitec.fraudengine.domain.rule.FraudRuleContext;
import com.capitec.fraudengine.infrastructure.config.FraudRuleProperties;

/**
 * Flags accounts that have too many recent transactions within the configured time window.
 */
@Component
public class VelocityFraudRule extends AbstractFraudRule {

	private final int velocityLimit;
	private final Duration velocityWindow;
	private final int velocityWindowMinutes;

	public VelocityFraudRule(FraudRuleProperties fraudRuleProperties) {
		super("VELOCITY", "Velocity Rule");
		this.velocityLimit = fraudRuleProperties.getVelocity().getThresholdCount();
		this.velocityWindowMinutes = fraudRuleProperties.getVelocity().getWindowMinutes();
		this.velocityWindow = Duration.ofMinutes(velocityWindowMinutes);
	}

	@Override
	public RuleEvaluationResult evaluate(FraudRuleContext context) {
		TransactionEvent currentTransaction = context.transactionEvent();
		OffsetDateTime currentTimestamp = currentTransaction.eventTimestamp();

		long matchingRecentTransactions = context.recentTransactions().stream()
			.filter(previousTransaction -> previousTransaction.accountId().equals(currentTransaction.accountId()))
			.filter(previousTransaction -> !previousTransaction.transactionId().equals(currentTransaction.transactionId()))
			.filter(previousTransaction -> {
				if (previousTransaction.eventTimestamp().isAfter(currentTimestamp)) {
					return false;
				}
				Duration age = Duration.between(previousTransaction.eventTimestamp(), currentTimestamp);
				return age.compareTo(velocityWindow) <= 0;
			})
			.count();

		long totalTransactionsInWindow = matchingRecentTransactions + 1;

		if (totalTransactionsInWindow >= velocityLimit) {
			return result(
				true,
				RuleSeverity.REVIEW,
				40,
				"Account has " + totalTransactionsInWindow + " transactions within " + velocityWindowMinutes + " minutes."
			);
		}

		return result(false, RuleSeverity.INFO, 0, "Transaction velocity is within the configured threshold.");
	}
}
