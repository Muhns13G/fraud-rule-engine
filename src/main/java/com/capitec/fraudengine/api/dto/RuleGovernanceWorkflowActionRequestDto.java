package com.capitec.fraudengine.api.dto;

import jakarta.validation.constraints.NotNull;

import com.capitec.fraudengine.domain.model.enums.RuleGovernanceWorkflowAction;

/**
 * Request payload for semantic governance workflow actions.
 *
 * @param action semantic workflow action
 */
public record RuleGovernanceWorkflowActionRequestDto(
	@NotNull RuleGovernanceWorkflowAction action
) {
}
