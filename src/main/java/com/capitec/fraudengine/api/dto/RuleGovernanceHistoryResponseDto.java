package com.capitec.fraudengine.api.dto;

import java.time.OffsetDateTime;

import com.capitec.fraudengine.domain.model.enums.RuleActivationState;
import com.capitec.fraudengine.domain.model.enums.RuleLifecycleStatus;

/**
 * API projection for one persisted governance history trail event.
 */
public record RuleGovernanceHistoryResponseDto(
	String ruleCode,
	String version,
	String actionType,
	String actor,
	String requestId,
	RuleLifecycleStatus fromLifecycleStatus,
	RuleActivationState fromActivationState,
	RuleLifecycleStatus toLifecycleStatus,
	RuleActivationState toActivationState,
	OffsetDateTime createdAt
) {
}
