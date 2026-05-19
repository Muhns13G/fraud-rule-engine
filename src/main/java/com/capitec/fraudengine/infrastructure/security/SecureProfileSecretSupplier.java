package com.capitec.fraudengine.infrastructure.security;

/**
 * Seam for plugging in external secret-manager integrations for secure profile credentials.
 */
public interface SecureProfileSecretSupplier {

	SecureProfileResolvedSecrets resolve(String externalSecretRef);
}
