package com.oitws.fraudengine.api.error;

/**
 * Raised when no persisted rule governance metadata exists for a supplied rule identity.
 */
public class RuleGovernanceMetadataNotFoundException extends RuntimeException {

	public RuleGovernanceMetadataNotFoundException(String ruleCode, String version) {
		super("Rule governance metadata not found for ruleCode '" + ruleCode + "' and version '" + version + "'.");
	}
}
