package com.capitec.fraudengine.api.dto;

import com.capitec.fraudengine.domain.model.enums.RuleActivationState;
import com.capitec.fraudengine.domain.model.enums.RuleExecutionSource;
import com.capitec.fraudengine.domain.model.enums.RuleLifecycleStatus;

/**
 * API projection for one governance-aware fraud rule definition.
 */
public record RuleGovernanceMetadataResponseDto(
	String ruleCode,
	String version,
	String ruleName,
	RuleLifecycleStatus lifecycleStatus,
	RuleActivationState activationState,
	RuleExecutionSource executionSource
) {
}
