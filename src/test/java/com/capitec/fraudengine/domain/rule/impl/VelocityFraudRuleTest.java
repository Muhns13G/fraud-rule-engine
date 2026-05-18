package com.capitec.fraudengine.domain.rule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.TransactionEvent;
import com.capitec.fraudengine.domain.model.enums.MerchantCategory;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.infrastructure.config.FraudRuleProperties;
import com.capitec.fraudengine.support.DomainTestFixtures;

class VelocityFraudRuleTest {

	private final VelocityFraudRule rule = new VelocityFraudRule(new FraudRuleProperties());

	@Test
	void shouldNotTriggerWhenOnlyCurrentTransactionExists() {
		TransactionEvent currentTransaction = transaction("txn-1", "account-1", "2026-05-12T10:00:00+02:00");

		RuleEvaluationResult result = rule.evaluate(
			DomainTestFixtures.fraudRuleContext(currentTransaction, List.of())
		);

		assertFalse(result.triggered());
		assertEquals(RuleSeverity.INFO, result.severity());
		assertEquals(0, result.scoreContribution());
	}

	@Test
	void shouldNotTriggerWhenRecentTransactionsBelongToDifferentAccount() {
		TransactionEvent currentTransaction = transaction("txn-1", "account-1", "2026-05-12T10:00:00+02:00");
		TransactionEvent otherAccountTransactionOne = transaction("txn-2", "account-2", "2026-05-12T09:58:00+02:00");
		TransactionEvent otherAccountTransactionTwo = transaction("txn-3", "account-2", "2026-05-12T09:57:00+02:00");

		RuleEvaluationResult result = rule.evaluate(
			DomainTestFixtures.fraudRuleContext(
				currentTransaction,
				List.of(otherAccountTransactionOne, otherAccountTransactionTwo)
			)
		);

		assertFalse(result.triggered());
	}

	@Test
	void shouldIgnoreMatchingTransactionWithSameTransactionId() {
		TransactionEvent currentTransaction = transaction("txn-1", "account-1", "2026-05-12T10:00:00+02:00");
		TransactionEvent duplicateTransaction = transaction("txn-1", "account-1", "2026-05-12T09:58:00+02:00");
		TransactionEvent matchingTransaction = transaction("txn-2", "account-1", "2026-05-12T09:57:00+02:00");

		RuleEvaluationResult result = rule.evaluate(
			DomainTestFixtures.fraudRuleContext(
				currentTransaction,
				List.of(duplicateTransaction, matchingTransaction)
			)
		);

		assertFalse(result.triggered());
	}

	@Test
	void shouldTriggerReviewWhenCurrentPlusTwoRecentTransactionsFallWithinWindow() {
		TransactionEvent currentTransaction = transaction("txn-1", "account-1", "2026-05-12T10:00:00+02:00");
		TransactionEvent matchingTransactionOne = transaction("txn-2", "account-1", "2026-05-12T09:58:00+02:00");
		TransactionEvent matchingTransactionTwo = transaction("txn-3", "account-1", "2026-05-12T09:56:00+02:00");

		RuleEvaluationResult result = rule.evaluate(
			DomainTestFixtures.fraudRuleContext(
				currentTransaction,
				List.of(matchingTransactionOne, matchingTransactionTwo)
			)
		);

		assertTrue(result.triggered());
		assertEquals(RuleSeverity.REVIEW, result.severity());
		assertEquals(40, result.scoreContribution());
		assertTrue(result.reason().contains("3 transactions within 5 minutes"));
	}

	@Test
	void shouldNotTriggerWhenThirdTransactionFallsOutsideWindow() {
		TransactionEvent currentTransaction = transaction("txn-1", "account-1", "2026-05-12T10:00:00+02:00");
		TransactionEvent matchingTransactionOne = transaction("txn-2", "account-1", "2026-05-12T09:58:00+02:00");
		TransactionEvent outsideWindowTransaction = transaction("txn-3", "account-1", "2026-05-12T09:54:59+02:00");

		RuleEvaluationResult result = rule.evaluate(
			DomainTestFixtures.fraudRuleContext(
				currentTransaction,
				List.of(matchingTransactionOne, outsideWindowTransaction)
			)
		);

		assertFalse(result.triggered());
	}

	@Test
	void shouldTreatLaterTransactionsWithinWindowAsMatchingBecauseAgeUsesAbsoluteDifference() {
		TransactionEvent currentTransaction = transaction("txn-1", "account-1", "2026-05-12T10:00:00+02:00");
		TransactionEvent laterTransactionOne = transaction("txn-2", "account-1", "2026-05-12T10:02:00+02:00");
		TransactionEvent laterTransactionTwo = transaction("txn-3", "account-1", "2026-05-12T10:04:00+02:00");

		RuleEvaluationResult result = rule.evaluate(
			DomainTestFixtures.fraudRuleContext(
				currentTransaction,
				List.of(laterTransactionOne, laterTransactionTwo)
			)
		);

		assertTrue(result.triggered());
		assertEquals(RuleSeverity.REVIEW, result.severity());
	}

	private TransactionEvent transaction(String transactionId, String accountId, String offsetDateTime) {
		return DomainTestFixtures.transactionEvent(
			transactionId,
			accountId,
			new BigDecimal("250.00"),
			MerchantCategory.RETAIL,
			OffsetDateTime.parse(offsetDateTime)
		);
	}
}
