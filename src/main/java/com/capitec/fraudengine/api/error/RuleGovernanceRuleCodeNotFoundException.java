package com.capitec.fraudengine.api.error;

/**
 * Raised when rule-governance metadata is requested for a rule code that has no registered versions.
 */
public class RuleGovernanceRuleCodeNotFoundException extends RuntimeException {

	public RuleGovernanceRuleCodeNotFoundException(String ruleCode) {
		super("Rule governance metadata not found for ruleCode '" + ruleCode + "'.");
	}
}
