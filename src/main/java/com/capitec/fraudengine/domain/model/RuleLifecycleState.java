package com.capitec.fraudengine.domain.model;

import com.capitec.fraudengine.domain.model.enums.RuleActivationState;
import com.capitec.fraudengine.domain.model.enums.RuleLifecycleStatus;

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
