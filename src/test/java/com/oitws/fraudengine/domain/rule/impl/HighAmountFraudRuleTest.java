package com.oitws.fraudengine.domain.rule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.oitws.fraudengine.domain.model.RuleEvaluationResult;
import com.oitws.fraudengine.domain.model.enums.MerchantCategory;
import com.oitws.fraudengine.domain.model.enums.RuleSeverity;
import com.oitws.fraudengine.infrastructure.config.FraudRuleProperties;
import com.oitws.fraudengine.support.DomainTestFixtures;

class HighAmountFraudRuleTest {

	private final HighAmountFraudRule rule = new HighAmountFraudRule(new FraudRuleProperties());

	@Test
	void shouldNotTriggerBelowReviewThreshold() {
		RuleEvaluationResult result = rule.evaluate(
			DomainTestFixtures.fraudRuleContext(
				DomainTestFixtures.transactionEvent(
					"txn-1",
					"account-1",
					new BigDecimal("9999.99"),
					MerchantCategory.RETAIL,
					OffsetDateTime.parse("2026-05-12T10:15:30+02:00")
				),
				List.of()
			)
		);

		assertEquals("HIGH_AMOUNT", result.ruleCode());
		assertFalse(result.triggered());
		assertEquals(RuleSeverity.INFO, result.severity());
		assertEquals(0, result.scoreContribution());
		assertTrue(result.reason().contains("below the configured fraud thresholds"));
	}

	@Test
	void shouldTriggerReviewAtExactReviewThreshold() {
		RuleEvaluationResult result = rule.evaluate(
			DomainTestFixtures.fraudRuleContext(
				DomainTestFixtures.transactionEvent(
					"txn-2",
					"account-1",
					new BigDecimal("10000.00"),
					MerchantCategory.RETAIL,
					OffsetDateTime.parse("2026-05-12T10:15:30+02:00")
				),
				List.of()
			)
		);

		assertTrue(result.triggered());
		assertEquals(RuleSeverity.REVIEW, result.severity());
		assertEquals(40, result.scoreContribution());
		assertTrue(result.reason().contains("review threshold"));
	}

	@Test
	void shouldTriggerBlockAtExactBlockThreshold() {
		RuleEvaluationResult result = rule.evaluate(
			DomainTestFixtures.fraudRuleContext(
				DomainTestFixtures.transactionEvent(
					"txn-3",
					"account-1",
					new BigDecimal("25000.00"),
					MerchantCategory.RETAIL,
					OffsetDateTime.parse("2026-05-12T10:15:30+02:00")
				),
				List.of()
			)
		);

		assertTrue(result.triggered());
		assertEquals(RuleSeverity.BLOCK, result.severity());
		assertEquals(100, result.scoreContribution());
		assertTrue(result.reason().contains("block threshold"));
	}
}
