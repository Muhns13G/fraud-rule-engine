package com.capitec.fraudengine.domain.policy;

import org.springframework.stereotype.Component;

import com.capitec.fraudengine.common.error.InvalidRuleGovernanceStateException;
import com.capitec.fraudengine.domain.model.RuleGovernanceMetadata;
import com.capitec.fraudengine.domain.model.enums.RuleActivationState;
import com.capitec.fraudengine.domain.model.enums.RuleExecutionSource;
import com.capitec.fraudengine.domain.model.enums.RuleLifecycleStatus;

/**
 * Deterministic boundary and validation policy for governed rule metadata.
 */
@Component
public class RuleGovernancePolicy {

	/**
	 * Validates governance metadata for deterministic state combinations.
	 *
	 * @param metadata metadata candidate to validate
	 */
	public void validateState(RuleGovernanceMetadata metadata) {
		if (metadata.identity().ruleCode() == null || metadata.identity().ruleCode().isBlank()) {
			throw new InvalidRuleGovernanceStateException("Rule code must be present.");
		}
		if (metadata.identity().version() == null || metadata.identity().version().isBlank()) {
			throw new InvalidRuleGovernanceStateException("Rule version must be present.");
		}
		if (metadata.ruleName() == null || metadata.ruleName().isBlank()) {
			throw new InvalidRuleGovernanceStateException("Rule name must be present.");
		}

		RuleLifecycleStatus lifecycleStatus = metadata.lifecycleState().lifecycleStatus();
		RuleActivationState activationState = metadata.lifecycleState().activationState();

		if (lifecycleStatus == RuleLifecycleStatus.ACTIVE && activationState != RuleActivationState.ACTIVE) {
			throw new InvalidRuleGovernanceStateException(
				"Lifecycle status ACTIVE requires activation state ACTIVE."
			);
		}

		if ((lifecycleStatus == RuleLifecycleStatus.DRAFT || lifecycleStatus == RuleLifecycleStatus.RETIRED)
			&& activationState != RuleActivationState.INACTIVE) {
			throw new InvalidRuleGovernanceStateException(
				"Lifecycle statuses DRAFT and RETIRED require activation state INACTIVE."
			);
		}
	}

	/**
	 * Enforces the current runtime boundary for code-defined rules.
	 *
	 * @param metadata metadata candidate
	 */
	public void validateExecutionBoundary(RuleGovernanceMetadata metadata) {
		if (metadata.executionSource() != RuleExecutionSource.CODE_DEFINED) {
			throw new InvalidRuleGovernanceStateException(
				"Only CODE_DEFINED execution source is permitted in the current runtime boundary."
			);
		}
	}
}
