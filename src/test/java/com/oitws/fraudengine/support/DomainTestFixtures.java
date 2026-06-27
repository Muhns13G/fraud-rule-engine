package com.oitws.fraudengine.support;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.oitws.fraudengine.domain.model.RuleEvaluationResult;
import com.oitws.fraudengine.domain.model.TransactionEvent;
import com.oitws.fraudengine.domain.model.TransactionLocation;
import com.oitws.fraudengine.domain.model.enums.MerchantCategory;
import com.oitws.fraudengine.domain.model.enums.RuleSeverity;
import com.oitws.fraudengine.domain.model.enums.TransactionChannel;
import com.oitws.fraudengine.domain.model.enums.TransactionType;
import com.oitws.fraudengine.domain.rule.FraudRuleContext;

/**
 * Shared test fixtures for pure domain-level unit tests.
 */
public final class DomainTestFixtures {

	private DomainTestFixtures() {
	}

	public static TransactionEvent transactionEvent(
		String transactionId,
		String accountId,
		BigDecimal amount,
		MerchantCategory merchantCategory,
		OffsetDateTime eventTimestamp
	) {
		return new TransactionEvent(
			transactionId,
			accountId,
			"customer-123",
			amount,
			"ZAR",
			"merchant-123",
			merchantCategory,
			TransactionType.PURCHASE,
			TransactionChannel.ONLINE,
			eventTimestamp,
			new TransactionLocation("ZA", "Cape Town"),
			"test-reference"
		);
	}

	public static FraudRuleContext fraudRuleContext(TransactionEvent transactionEvent, List<TransactionEvent> recentTransactions) {
		return new FraudRuleContext(transactionEvent, recentTransactions);
	}

	public static RuleEvaluationResult ruleResult(
		String ruleCode,
		boolean triggered,
		RuleSeverity severity,
		int scoreContribution
	) {
		return new RuleEvaluationResult(
			ruleCode,
			ruleCode + " Rule",
			triggered,
			severity,
			scoreContribution,
			"Reason for " + ruleCode
		);
	}
}
