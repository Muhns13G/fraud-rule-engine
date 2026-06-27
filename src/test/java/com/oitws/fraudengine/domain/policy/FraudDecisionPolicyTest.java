package com.oitws.fraudengine.domain.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.oitws.fraudengine.domain.model.RuleEvaluationResult;
import com.oitws.fraudengine.domain.model.enums.FraudDecision;
import com.oitws.fraudengine.domain.model.enums.RuleSeverity;
import com.oitws.fraudengine.support.DomainTestFixtures;

class FraudDecisionPolicyTest {

	private final FraudDecisionPolicy policy = new FraudDecisionPolicy();

	@Test
	void shouldReturnAllowWhenNoRulesTrigger() {
		FraudDecisionPolicyResult result = policy.evaluate(
			List.of(
				ruleResult("HIGH_AMOUNT", false, RuleSeverity.INFO, 0),
				ruleResult("VELOCITY", false, RuleSeverity.INFO, 0)
			)
		);

		assertEquals(FraudDecision.ALLOW, result.decision());
		assertEquals(0, result.decisionScore());
		assertEquals(0, result.triggeredRuleResults().size());
		assertTrue(result.traceSummary().contains("No fraud rules were triggered"));
	}

	@Test
	void shouldReturnReviewWhenOnlyReviewRulesTriggerBelowBlockThreshold() {
		FraudDecisionPolicyResult result = policy.evaluate(
			List.of(
				ruleResult("HIGH_AMOUNT", true, RuleSeverity.REVIEW, 40),
				ruleResult("RISKY_MERCHANT_CATEGORY", true, RuleSeverity.REVIEW, 40),
				ruleResult("UNUSUAL_TIME", false, RuleSeverity.INFO, 0)
			)
		);

		assertEquals(FraudDecision.REVIEW, result.decision());
		assertEquals(80, result.decisionScore());
		assertEquals(2, result.triggeredRuleResults().size());
		assertTrue(result.traceSummary().contains("HIGH_AMOUNT"));
		assertTrue(result.traceSummary().contains("RISKY_MERCHANT_CATEGORY"));
	}

	@Test
	void shouldReturnBlockWhenAnyBlockingRuleTriggers() {
		FraudDecisionPolicyResult result = policy.evaluate(
			List.of(
				ruleResult("HIGH_AMOUNT", true, RuleSeverity.BLOCK, 100),
				ruleResult("VELOCITY", false, RuleSeverity.INFO, 0)
			)
		);

		assertEquals(FraudDecision.BLOCK, result.decision());
		assertEquals(100, result.decisionScore());
		assertEquals(1, result.triggeredRuleResults().size());
	}

	@Test
	void shouldReturnBlockWhenCumulativeScoreReachesOneHundredWithoutBlockingSeverity() {
		FraudDecisionPolicyResult result = policy.evaluate(
			List.of(
				ruleResult("HIGH_AMOUNT", true, RuleSeverity.REVIEW, 40),
				ruleResult("VELOCITY", true, RuleSeverity.REVIEW, 40),
				ruleResult("RISKY_MERCHANT_CATEGORY", true, RuleSeverity.REVIEW, 40)
			)
		);

		assertEquals(FraudDecision.BLOCK, result.decision());
		assertEquals(120, result.decisionScore());
		assertEquals(3, result.triggeredRuleResults().size());
		assertTrue(result.traceSummary().contains("Decision BLOCK with score 120"));
	}

	private RuleEvaluationResult ruleResult(String ruleCode, boolean triggered, RuleSeverity severity, int scoreContribution) {
		return DomainTestFixtures.ruleResult(ruleCode, triggered, severity, scoreContribution);
	}
}
