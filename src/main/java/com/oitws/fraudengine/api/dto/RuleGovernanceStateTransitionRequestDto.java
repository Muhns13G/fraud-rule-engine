package com.oitws.fraudengine.api.dto;

import com.oitws.fraudengine.domain.model.enums.RuleActivationState;
import com.oitws.fraudengine.domain.model.enums.RuleLifecycleStatus;

import jakarta.validation.constraints.NotNull;

/**
 * Request body for governed lifecycle and activation state transitions.
 */
public record RuleGovernanceStateTransitionRequestDto(
	@NotNull RuleLifecycleStatus lifecycleStatus,
	@NotNull RuleActivationState activationState
) {
}
