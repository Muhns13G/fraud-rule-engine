package com.oitws.fraudengine.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class EnvExternalManagerSecretSupplierTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void shouldResolveJsonSecretPayloadFromEnvReference() {
		MockEnvironment environment = new MockEnvironment();
		environment.setProperty("FRAUD_ENGINE_SECURE_SECRET_PAYLOAD", "{\"username\":\"reviewer\",\"password\":\"change-me\"}");

		EnvExternalManagerSecretSupplier supplier = new EnvExternalManagerSecretSupplier(environment, objectMapper);
		SecureProfileResolvedSecrets resolved = supplier.resolve("env:FRAUD_ENGINE_SECURE_SECRET_PAYLOAD");

		assertEquals("reviewer", resolved.username());
		assertEquals("change-me", resolved.password());
		assertEquals(null, resolved.passwordEncoded());
	}

	@Test
	void shouldResolveKeyValueSecretPayloadFromEnvReference() {
		MockEnvironment environment = new MockEnvironment();
		environment.setProperty(
			"FRAUD_ENGINE_SECURE_SECRET_PAYLOAD",
			"username=reviewer;passwordEncoded=$2a$10$abcdefghijklmnopqrstuv"
		);

		EnvExternalManagerSecretSupplier supplier = new EnvExternalManagerSecretSupplier(environment, objectMapper);
		SecureProfileResolvedSecrets resolved = supplier.resolve("env://FRAUD_ENGINE_SECURE_SECRET_PAYLOAD");

		assertEquals("reviewer", resolved.username());
		assertEquals(null, resolved.password());
		assertEquals("$2a$10$abcdefghijklmnopqrstuv", resolved.passwordEncoded());
	}

	@Test
	void shouldFailWhenEnvPayloadIsMissing() {
		MockEnvironment environment = new MockEnvironment();
		EnvExternalManagerSecretSupplier supplier = new EnvExternalManagerSecretSupplier(environment, objectMapper);

		assertThrows(
			IllegalStateException.class,
			() -> supplier.resolve("env:FRAUD_ENGINE_SECURE_SECRET_PAYLOAD")
		);
	}

	@Test
	void shouldFailWhenReferenceFormatIsUnsupported() {
		MockEnvironment environment = new MockEnvironment();
		environment.setProperty("FRAUD_ENGINE_SECURE_SECRET_PAYLOAD", "{\"username\":\"reviewer\",\"password\":\"change-me\"}");
		EnvExternalManagerSecretSupplier supplier = new EnvExternalManagerSecretSupplier(environment, objectMapper);

		assertThrows(
			IllegalStateException.class,
			() -> supplier.resolve("vault://fraud-engine/secure-profile")
		);
	}
}
