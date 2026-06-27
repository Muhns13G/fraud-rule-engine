package com.oitws.fraudengine.infrastructure.security;

/**
 * Seam for plugging in external secret-manager integrations for secure profile credentials.
 */
public interface SecureProfileSecretSupplier {

	/**
	 * Resolves secure-profile credentials from an external secret reference.
	 *
	 * @param externalSecretRef external secret identifier/reference
	 * @return resolved credentials containing username and exactly one password representation
	 */
	SecureProfileResolvedSecrets resolve(String externalSecretRef);
}
