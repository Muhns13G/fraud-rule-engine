package com.oitws.fraudengine.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class SecureProfileCredentialDiagnosticsTest {

	@Test
	void shouldExposeRedactedDiagnosticsForExternalManagerMode() {
		SecureProfileSecurityProperties properties = new SecureProfileSecurityProperties();
		properties.setIdentityProvider(SecureProfileSecurityProperties.IdentityProvider.IN_MEMORY);
		properties.setSecretSource(SecureProfileSecurityProperties.SecretSource.EXTERNAL_MANAGER);
		properties.setExternalSecretRef("env:FRAUD_ENGINE_SECURE_SECRET_PAYLOAD");
		properties.setRotationPhase(SecureProfileSecurityProperties.RotationPhase.OVERLAP);
		properties.setRotationUsername("secure-user-rotating");

		MockEnvironment environment = new MockEnvironment()
			.withProperty("app.security.secure-profile.external-manager-adapter", "ENV");

		Map<String, Object> diagnostics = SecureProfileCredentialDiagnostics.build(properties, environment);

		assertThat(diagnostics.get("identityProvider")).isEqualTo("IN_MEMORY");
		assertThat(diagnostics.get("secretSource")).isEqualTo("EXTERNAL_MANAGER");
		assertThat(diagnostics.get("externalManagerAdapter")).isEqualTo("ENV");
		assertThat(diagnostics.get("externalSecretRefConfigured")).isEqualTo(true);
		assertThat(diagnostics.get("rotationPhase")).isEqualTo("OVERLAP");
		assertThat(diagnostics.get("rotationEnabled")).isEqualTo(true);
		assertThat(diagnostics.get("rotationUsernameConfigured")).isEqualTo(true);
		assertThat(diagnostics.get("primaryCredentialMode")).isEqualTo("external");
		assertThat(diagnostics).doesNotContainKeys("password", "passwordEncoded", "username", "externalSecretRef");
	}

	@Test
	void shouldMapLegacyRotationEnabledToLegacyOverlapPhase() {
		SecureProfileSecurityProperties properties = new SecureProfileSecurityProperties();
		properties.setIdentityProvider(SecureProfileSecurityProperties.IdentityProvider.IN_MEMORY);
		properties.setSecretSource(SecureProfileSecurityProperties.SecretSource.ENV);
		properties.setRotationEnabled(true);
		properties.setPassword("change-me");

		Map<String, Object> diagnostics = SecureProfileCredentialDiagnostics.build(properties, new MockEnvironment());

		assertThat(diagnostics.get("rotationPhase")).isEqualTo("LEGACY_OVERLAP");
		assertThat(diagnostics.get("rotationEnabled")).isEqualTo(true);
		assertThat(diagnostics.get("primaryCredentialMode")).isEqualTo("raw");
	}

	@Test
	void shouldMapJdbcIdentityProviderToJdbcCredentialMode() {
		SecureProfileSecurityProperties properties = new SecureProfileSecurityProperties();
		properties.setIdentityProvider(SecureProfileSecurityProperties.IdentityProvider.JDBC);
		properties.setSecretSource(SecureProfileSecurityProperties.SecretSource.ENV);

		Map<String, Object> diagnostics = SecureProfileCredentialDiagnostics.build(properties, new MockEnvironment());

		assertThat(diagnostics.get("primaryCredentialMode")).isEqualTo("jdbc");
		assertThat(diagnostics.get("rotationPhase")).isEqualTo("NONE");
	}
}
