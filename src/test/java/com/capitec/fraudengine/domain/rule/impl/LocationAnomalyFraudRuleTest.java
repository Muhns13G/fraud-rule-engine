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
import com.capitec.fraudengine.domain.model.TransactionLocation;
import com.capitec.fraudengine.domain.model.enums.MerchantCategory;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.infrastructure.config.FraudRuleProperties;
import com.capitec.fraudengine.support.DomainTestFixtures;

class LocationAnomalyFraudRuleTest {

	private final LocationAnomalyFraudRule rule = new LocationAnomalyFraudRule(new FraudRuleProperties());

	@Test
	void shouldTriggerWhenCountryChangesComparedWithMostRecentTransaction() {
		TransactionEvent currentTransaction = transaction(
			"txn-current",
			"account-1",
			"2026-05-12T10:00:00+02:00",
			"NG",
			"Lagos"
		);
		TransactionEvent previousTransaction = transaction(
			"txn-prev",
			"account-1",
			"2026-05-12T09:58:00+02:00",
			"ZA",
			"Cape Town"
		);

		RuleEvaluationResult result = rule.evaluate(
			DomainTestFixtures.fraudRuleContext(currentTransaction, List.of(previousTransaction))
		);

		assertTrue(result.triggered());
		assertEquals(RuleSeverity.REVIEW, result.severity());
		assertEquals(40, result.scoreContribution());
		assertTrue(result.reason().contains("Location country changed"));
	}

	@Test
	void shouldTriggerWhenCityChangesInsideSameCountry() {
		TransactionEvent currentTransaction = transaction(
			"txn-current",
			"account-1",
			"2026-05-12T10:00:00+02:00",
			"ZA",
			"Johannesburg"
		);
		TransactionEvent previousTransaction = transaction(
			"txn-prev",
			"account-1",
			"2026-05-12T09:59:00+02:00",
			"ZA",
			"Cape Town"
		);

		RuleEvaluationResult result = rule.evaluate(
			DomainTestFixtures.fraudRuleContext(currentTransaction, List.of(previousTransaction))
		);

		assertTrue(result.triggered());
		assertEquals(RuleSeverity.REVIEW, result.severity());
		assertTrue(result.reason().contains("Location city changed"));
	}

	@Test
	void shouldNotTriggerWhenLocationMatchesMostRecentComparableTransaction() {
		TransactionEvent currentTransaction = transaction(
			"txn-current",
			"account-1",
			"2026-05-12T10:00:00+02:00",
			"ZA",
			"Cape Town"
		);
		TransactionEvent previousTransaction = transaction(
			"txn-prev",
			"account-1",
			"2026-05-12T09:59:00+02:00",
			"ZA",
			"Cape Town"
		);

		RuleEvaluationResult result = rule.evaluate(
			DomainTestFixtures.fraudRuleContext(currentTransaction, List.of(previousTransaction))
		);

		assertFalse(result.triggered());
		assertEquals(RuleSeverity.INFO, result.severity());
		assertEquals(0, result.scoreContribution());
	}

	@Test
	void shouldNotTriggerWhenCurrentLocationIsMissing() {
		TransactionEvent currentTransaction = new TransactionEvent(
			"txn-current",
			"account-1",
			"customer-123",
			new BigDecimal("250.00"),
			"ZAR",
			"merchant-123",
			MerchantCategory.RETAIL,
			com.capitec.fraudengine.domain.model.enums.TransactionType.PURCHASE,
			com.capitec.fraudengine.domain.model.enums.TransactionChannel.ONLINE,
			OffsetDateTime.parse("2026-05-12T10:00:00+02:00"),
			null,
			"test-reference"
		);
		TransactionEvent previousTransaction = transaction(
			"txn-prev",
			"account-1",
			"2026-05-12T09:59:00+02:00",
			"ZA",
			"Cape Town"
		);

		RuleEvaluationResult result = rule.evaluate(
			DomainTestFixtures.fraudRuleContext(currentTransaction, List.of(previousTransaction))
		);

		assertFalse(result.triggered());
		assertEquals(RuleSeverity.INFO, result.severity());
	}

	private TransactionEvent transaction(
		String transactionId,
		String accountId,
		String offsetDateTime,
		String countryCode,
		String city
	) {
		TransactionEvent base = DomainTestFixtures.transactionEvent(
			transactionId,
			accountId,
			new BigDecimal("250.00"),
			MerchantCategory.RETAIL,
			OffsetDateTime.parse(offsetDateTime)
		);

		return new TransactionEvent(
			base.transactionId(),
			base.accountId(),
			base.customerId(),
			base.amount(),
			base.currency(),
			base.merchantId(),
			base.merchantCategory(),
			base.transactionType(),
			base.channel(),
			base.eventTimestamp(),
			new TransactionLocation(countryCode, city),
			base.reference()
		);
	}
}
