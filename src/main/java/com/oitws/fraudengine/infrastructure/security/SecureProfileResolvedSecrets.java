package com.oitws.fraudengine.infrastructure.security;

/**
 * Resolved secure-profile credentials from an external secret source.
 */
public record SecureProfileResolvedSecrets(
	String username,
	String password,
	String passwordEncoded
) {
}
