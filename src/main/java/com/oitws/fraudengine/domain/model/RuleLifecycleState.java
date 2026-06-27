package com.oitws.fraudengine.domain.model;

import com.oitws.fraudengine.domain.model.enums.RuleActivationState;
import com.oitws.fraudengine.domain.model.enums.RuleLifecycleStatus;

/**
 * Combined governance and activation state for one rule version.
 *
 * @param lifecycleStatus governance lifecycle status
 * @param activationState runtime activation state
 */
public record RuleLifecycleState(
	RuleLifecycleStatus lifecycleStatus,
	RuleActivationState activationState
) {
}
