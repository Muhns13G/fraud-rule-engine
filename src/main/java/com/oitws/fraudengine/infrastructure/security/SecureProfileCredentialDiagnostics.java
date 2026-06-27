package com.oitws.fraudengine.infrastructure.security;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.env.Environment;

/**
 * Produces safe, redacted secure-profile credential diagnostics.
 */
public final class SecureProfileCredentialDiagnostics {

	private static final String DIAGNOSTICS_VERSION = "5.2.3";

	private SecureProfileCredentialDiagnostics() {
	}

	/**
	 * Builds a redacted diagnostics map for secure-profile credential posture.
	 *
	 * @param properties secure-profile security properties
	 * @param environment Spring environment for profile/config lookup
	 * @return non-sensitive diagnostics map
	 */
	public static Map<String, Object> build(
		SecureProfileSecurityProperties properties,
		Environment environment
	) {
		Map<String, Object> diagnostics = new LinkedHashMap<>();
		diagnostics.put("activeProfile", "secure");
		diagnostics.put("identityProvider", nameOf(properties.getIdentityProvider()));
		diagnostics.put("secretSource", nameOf(properties.getSecretSource()));
		diagnostics.put("externalManagerAdapter", normalized(
			environment.getProperty("app.security.secure-profile.external-manager-adapter")
		));
		diagnostics.put("externalSecretRefConfigured", normalized(properties.getExternalSecretRef()) != null);
		diagnostics.put("rotationPhase", resolveRotationPhase(properties));
		diagnostics.put("rotationEnabled", isRotationEnabled(properties));
		diagnostics.put("rotationUsernameConfigured", normalized(properties.getRotationUsername()) != null);
		diagnostics.put("primaryCredentialMode", resolvePrimaryCredentialMode(properties));
		diagnostics.put("diagnosticsVersion", DIAGNOSTICS_VERSION);
		diagnostics.put("generatedAt", OffsetDateTime.now().toString());
		return diagnostics;
	}

	private static String resolveRotationPhase(SecureProfileSecurityProperties properties) {
		SecureProfileSecurityProperties.RotationPhase phase = properties.getRotationPhase();
		if (phase != null) {
			return phase.name();
		}
		if (properties.isRotationEnabled()) {
			return "LEGACY_OVERLAP";
		}
		return "NONE";
	}

	private static boolean isRotationEnabled(SecureProfileSecurityProperties properties) {
		return properties.getRotationPhase() != null || properties.isRotationEnabled();
	}

	private static String resolvePrimaryCredentialMode(SecureProfileSecurityProperties properties) {
		SecureProfileSecurityProperties.IdentityProvider identityProvider = properties.getIdentityProvider();
		if (identityProvider == SecureProfileSecurityProperties.IdentityProvider.JDBC) {
			return "jdbc";
		}

		SecureProfileSecurityProperties.SecretSource secretSource = properties.getSecretSource();
		if (secretSource == SecureProfileSecurityProperties.SecretSource.EXTERNAL_MANAGER) {
			return "external";
		}
		if (secretSource == SecureProfileSecurityProperties.SecretSource.PRE_ENCODED) {
			return "encoded";
		}

		return normalized(properties.getPasswordEncoded()) != null ? "encoded" : "raw";
	}

	private static String nameOf(Enum<?> value) {
		return value == null ? "UNKNOWN" : value.name();
	}

	private static String normalized(String value) {
		if (value == null) {
			return null;
		}
		String normalized = value.trim();
		return normalized.isEmpty() ? null : normalized;
	}
}
