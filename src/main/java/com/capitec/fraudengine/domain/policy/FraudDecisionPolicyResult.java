package com.capitec.fraudengine.domain.policy;

import java.util.List;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.enums.FraudDecision;

/**
 * Aggregated fraud decision outcome derived from a set of rule results.
 *
 * @param decision outward fraud decision
 * @param decisionScore internal aggregate score
 * @param traceSummary concise human-readable explanation of the final outcome
 * @param triggeredRuleResults subset of rule results that materially contributed to the final decision
 */
public record FraudDecisionPolicyResult(
	FraudDecision decision,
	int decisionScore,
	String traceSummary,
	List<RuleEvaluationResult> triggeredRuleResults
) {
	public FraudDecisionPolicyResult {
		triggeredRuleResults = List.copyOf(triggeredRuleResults);
	}
}
