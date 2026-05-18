package com.capitec.fraudengine.api.dto;

import com.capitec.fraudengine.domain.model.enums.RuleActivationState;
import com.capitec.fraudengine.domain.model.enums.RuleLifecycleStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for registering a new governed metadata version for an existing rule code.
 */
public record RuleGovernanceVersionRegistrationRequestDto(
	@NotBlank String version,
	@NotNull RuleLifecycleStatus lifecycleStatus,
	@NotNull RuleActivationState activationState
) {
}
