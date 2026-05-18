package com.capitec.fraudengine.common.error;

/**
 * Raised when rule governance metadata violates deterministic lifecycle or boundary policies.
 */
public class InvalidRuleGovernanceStateException extends RuntimeException {

	public InvalidRuleGovernanceStateException(String message) {
		super(message);
	}
}
