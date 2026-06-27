package com.oitws.fraudengine.domain.policy;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.oitws.fraudengine.domain.model.RuleEvaluationResult;
import com.oitws.fraudengine.domain.model.enums.FraudDecision;
import com.oitws.fraudengine.domain.model.enums.RuleSeverity;

/**
 * Aggregates individual rule results into the Phase 1 outward fraud decision, score, and trace summary.
 */
@Component
public class FraudDecisionPolicy {

	/**
	 * Applies the Phase 1 aggregation rules to the supplied rule results.
	 *
	 * @param ruleResults full rule result set for a single transaction
	 * @return aggregated decision output
	 */
	public FraudDecisionPolicyResult evaluate(List<RuleEvaluationResult> ruleResults) {
		List<RuleEvaluationResult> triggeredRuleResults = ruleResults.stream()
			.filter(RuleEvaluationResult::triggered)
			.toList();

		int decisionScore = triggeredRuleResults.stream()
			.mapToInt(RuleEvaluationResult::scoreContribution)
			.sum();

		boolean hasBlockingRule = triggeredRuleResults.stream()
			.anyMatch(ruleResult -> ruleResult.severity() == RuleSeverity.BLOCK);

		FraudDecision decision;
		if (hasBlockingRule || decisionScore >= 100) {
			decision = FraudDecision.BLOCK;
		} else if (decisionScore > 0) {
			decision = FraudDecision.REVIEW;
		} else {
			decision = FraudDecision.ALLOW;
		}

		String traceSummary = buildTraceSummary(decision, decisionScore, triggeredRuleResults);
		return new FraudDecisionPolicyResult(decision, decisionScore, traceSummary, triggeredRuleResults);
	}

	private String buildTraceSummary(
		FraudDecision decision,
		int decisionScore,
		List<RuleEvaluationResult> triggeredRuleResults
	) {
		if (triggeredRuleResults.isEmpty()) {
			return "No fraud rules were triggered; transaction is allowed.";
		}

		String triggeredRuleCodes = triggeredRuleResults.stream()
			.map(RuleEvaluationResult::ruleCode)
			.collect(Collectors.joining(", "));

		return "Decision " + decision + " with score " + decisionScore
			+ " based on triggered rules: " + triggeredRuleCodes + ".";
	}
}
