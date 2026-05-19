package com.capitec.fraudengine.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class SecureProfileConfigurationGuardrailsTest {

	@Test
	void shouldAcceptDistinctSecureRoleContract() {
		SecureProfileSecurityProperties properties = secureProperties(
			"API_CLIENT",
			"OPS_READER",
			"GOVERNANCE_ADMIN",
			"PLATFORM_ADMIN"
		);

		assertThatCode(() -> SecureProfileConfigurationGuardrails.validateRoleContract(properties))
			.doesNotThrowAnyException();
	}

	@Test
	void shouldAllowOverlappingSecureRoleContractWhenExplicitlyConfigured() {
		SecureProfileSecurityProperties properties = secureProperties(
			"API_CLIENT",
			"API_CLIENT",
			"GOVERNANCE_ADMIN",
			"PLATFORM_ADMIN"
		);

		assertThatCode(() -> SecureProfileConfigurationGuardrails.validateRoleContract(properties))
			.doesNotThrowAnyException();
	}

	@Test
	void shouldRejectWildcardActuatorExposureInSecureProfile() {
		MockEnvironment environment = new MockEnvironment()
			.withProperty("management.endpoints.web.exposure.include", "*");

		assertThatThrownBy(() -> SecureProfileConfigurationGuardrails.validateSecureActuatorExposure(environment))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("must not use wildcard");
	}

	@Test
	void shouldAcceptExplicitActuatorExposureInSecureProfile() {
		MockEnvironment environment = new MockEnvironment()
			.withProperty("management.endpoints.web.exposure.include", "health,info");

		assertThatCode(() -> SecureProfileConfigurationGuardrails.validateSecureActuatorExposure(environment))
			.doesNotThrowAnyException();
	}

	private static SecureProfileSecurityProperties secureProperties(
		String role,
		String opsReaderRole,
		String adminRole,
		String platformAdminRole
	) {
		SecureProfileSecurityProperties properties = new SecureProfileSecurityProperties();
		properties.setRole(role);
		properties.setOpsReaderRole(opsReaderRole);
		properties.setAdminRole(adminRole);
		properties.setPlatformAdminRole(platformAdminRole);
		return properties;
	}
}
