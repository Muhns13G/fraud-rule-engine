package com.capitec.fraudengine.domain.model;

/**
 * Stable identity of one fraud rule version.
 *
 * @param ruleCode machine-readable rule identifier
 * @param version semantic version label for governance
 */
public record RuleIdentity(
	String ruleCode,
	String version
) {
}
