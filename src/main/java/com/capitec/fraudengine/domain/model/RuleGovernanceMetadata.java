package com.capitec.fraudengine.domain.model;

import com.capitec.fraudengine.domain.model.enums.RuleExecutionSource;

/**
 * Governance metadata for one known fraud rule version.
 *
 * @param identity stable rule identity and version
 * @param ruleName reviewer-friendly rule name
 * @param lifecycleState lifecycle and activation state
 * @param executionSource source of executable logic
 */
public record RuleGovernanceMetadata(
	RuleIdentity identity,
	String ruleName,
	RuleLifecycleState lifecycleState,
	RuleExecutionSource executionSource
) {
}
