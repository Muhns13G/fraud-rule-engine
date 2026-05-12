package com.capitec.fraudengine.domain.rule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.enums.MerchantCategory;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.support.DomainTestFixtures;

class UnusualTimeFraudRuleTest {

	private final UnusualTimeFraudRule rule = new UnusualTimeFraudRule();

	@Test
	void shouldTriggerAtMidnight() {
		RuleEvaluationResult result = evaluate("2026-05-12T00:00:00+02:00");

		assertTrue(result.triggered());
		assertEquals(RuleSeverity.REVIEW, result.severity());
	}

	@Test
	void shouldTriggerDuringWindow() {
		RuleEvaluationResult result = evaluate("2026-05-12T03:59:00+02:00");

		assertTrue(result.triggered());
		assertEquals(40, result.scoreContribution());
	}

	@Test
	void shouldNotTriggerAtWindowEnd() {
		RuleEvaluationResult result = evaluate("2026-05-12T04:00:00+02:00");

		assertFalse(result.triggered());
	}

	@Test
	void shouldNotTriggerDuringDaytime() {
		RuleEvaluationResult result = evaluate("2026-05-12T12:30:00+02:00");

		assertFalse(result.triggered());
		assertEquals(RuleSeverity.INFO, result.severity());
		assertEquals(0, result.scoreContribution());
	}

	private RuleEvaluationResult evaluate(String offsetDateTime) {
		return rule.evaluate(
			DomainTestFixtures.fraudRuleContext(
				DomainTestFixtures.transactionEvent(
					"txn-1",
					"account-1",
					new BigDecimal("350.00"),
					MerchantCategory.RETAIL,
					OffsetDateTime.parse(offsetDateTime)
				),
				List.of()
			)
		);
	}
}
