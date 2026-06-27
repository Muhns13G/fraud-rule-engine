package com.oitws.fraudengine.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Fail-fast startup validation for secure-profile security and observability posture.
 */
@Component
@Profile("secure")
public class SecureProfileConfigurationGuardrails implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecureProfileConfigurationGuardrails.class);

	private final Environment environment;
	private final SecureProfileSecurityProperties properties;

	public SecureProfileConfigurationGuardrails(
		Environment environment,
		SecureProfileSecurityProperties properties
	) {
		this.environment = environment;
		this.properties = properties;
	}

	@Override
	public void run(ApplicationArguments args) {
		validateRoleContract(properties);
		validateSecureActuatorExposure(environment);

		LOGGER.info(
			"secure_profile_configuration_guardrail status=validated identityProvider={} secretSource={} actuatorExposure={}",
			properties.getIdentityProvider(),
			properties.getSecretSource(),
			environment.getProperty("management.endpoints.web.exposure.include")
		);

		Map<String, Object> diagnostics = SecureProfileCredentialDiagnostics.build(properties, environment);
		LOGGER.info(
			"secure_profile_credential_diagnostics phase={} rotationEnabled={} primaryCredentialMode={} externalSecretRefConfigured={} diagnosticsVersion={}",
			diagnostics.get("rotationPhase"),
			diagnostics.get("rotationEnabled"),
			diagnostics.get("primaryCredentialMode"),
			diagnostics.get("externalSecretRefConfigured"),
			diagnostics.get("diagnosticsVersion")
		);
	}

	static void validateRoleContract(SecureProfileSecurityProperties properties) {
		String apiClientRole = normalized(properties.getRole());
		String opsReaderRole = normalized(properties.getOpsReaderRole());
		String governanceAdminRole = normalized(properties.getAdminRole());

		if (apiClientRole == null || opsReaderRole == null || governanceAdminRole == null) {
			throw new IllegalStateException(
				"Secure profile role contract is incomplete. Configure role, ops-reader-role, and admin-role."
			);
		}
	}

	static void validateSecureActuatorExposure(Environment environment) {
		String exposure = normalized(environment.getProperty("management.endpoints.web.exposure.include"));
		if (exposure == null) {
			throw new IllegalStateException(
				"Secure profile actuator exposure is missing. Configure management.endpoints.web.exposure.include."
			);
		}

		if (exposure.contains("*")) {
			throw new IllegalStateException(
				"Secure profile actuator exposure must not use wildcard '*'. Explicitly list allowed endpoints."
			);
		}
	}

	private static String normalized(String value) {
		if (value == null) {
			return null;
		}
		String normalized = value.trim();
		return normalized.isEmpty() ? null : normalized;
	}
}
