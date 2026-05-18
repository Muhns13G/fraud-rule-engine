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
import com.capitec.fraudengine.infrastructure.config.FraudRuleProperties;
import com.capitec.fraudengine.support.DomainTestFixtures;

class RiskyMerchantCategoryFraudRuleTest {

	private final RiskyMerchantCategoryFraudRule rule = new RiskyMerchantCategoryFraudRule(new FraudRuleProperties());

	@Test
	void shouldTriggerForGambling() {
		assertTriggeredCategory(MerchantCategory.GAMBLING);
	}

	@Test
	void shouldTriggerForCrypto() {
		assertTriggeredCategory(MerchantCategory.CRYPTO);
	}

	@Test
	void shouldTriggerForMoneyTransfer() {
		assertTriggeredCategory(MerchantCategory.MONEY_TRANSFER);
	}

	@Test
	void shouldNotTriggerForNonFlaggedCategory() {
		RuleEvaluationResult result = evaluate(MerchantCategory.GROCERY);

		assertFalse(result.triggered());
		assertEquals(RuleSeverity.INFO, result.severity());
		assertEquals(0, result.scoreContribution());
	}

	private void assertTriggeredCategory(MerchantCategory merchantCategory) {
		RuleEvaluationResult result = evaluate(merchantCategory);

		assertTrue(result.triggered());
		assertEquals(RuleSeverity.REVIEW, result.severity());
		assertEquals(40, result.scoreContribution());
		assertTrue(result.reason().contains(merchantCategory.name()));
	}

	private RuleEvaluationResult evaluate(MerchantCategory merchantCategory) {
		return rule.evaluate(
			DomainTestFixtures.fraudRuleContext(
				DomainTestFixtures.transactionEvent(
					"txn-1",
					"account-1",
					new BigDecimal("500.00"),
					merchantCategory,
					OffsetDateTime.parse("2026-05-12T10:15:30+02:00")
				),
				List.of()
			)
		);
	}
}
