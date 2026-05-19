package com.capitec.fraudengine.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class SecureProfileSecurityConfigurationTest {

	private final SecureProfileSecurityConfiguration configuration = new SecureProfileSecurityConfiguration();
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Test
	void shouldAllowEnvSecretSourceWhenUsernameAndRawPasswordArePresent() {
		SecureProfileSecurityProperties properties = baseInMemoryProperties();
		properties.setSecretSource(SecureProfileSecurityProperties.SecretSource.ENV);
		properties.setPasswordEncoded(null);

		ObjectProvider<SecureProfileSecretSupplier> secretSupplierProvider = mock(ObjectProvider.class);
		when(secretSupplierProvider.getIfAvailable()).thenReturn(null);

		assertDoesNotThrow(() -> configuration.userDetailsService(properties, secretSupplierProvider, passwordEncoder));
	}

	@Test
	void shouldRejectEnvSecretSourceWhenPasswordEncodedIsProvided() {
		SecureProfileSecurityProperties properties = baseInMemoryProperties();
		properties.setSecretSource(SecureProfileSecurityProperties.SecretSource.ENV);
		properties.setPasswordEncoded("$2a$10$abcdefghijklmnopqrstuv");

		ObjectProvider<SecureProfileSecretSupplier> secretSupplierProvider = mock(ObjectProvider.class);
		when(secretSupplierProvider.getIfAvailable()).thenReturn(null);

		assertThrows(
			IllegalStateException.class,
			() -> configuration.userDetailsService(properties, secretSupplierProvider, passwordEncoder)
		);
	}

	@Test
	void shouldRejectPreEncodedSecretSourceWhenRawPasswordIsAlsoProvided() {
		SecureProfileSecurityProperties properties = baseInMemoryProperties();
		properties.setSecretSource(SecureProfileSecurityProperties.SecretSource.PRE_ENCODED);
		properties.setPasswordEncoded("$2a$10$abcdefghijklmnopqrstuv");

		ObjectProvider<SecureProfileSecretSupplier> secretSupplierProvider = mock(ObjectProvider.class);
		when(secretSupplierProvider.getIfAvailable()).thenReturn(null);

		assertThrows(
			IllegalStateException.class,
			() -> configuration.userDetailsService(properties, secretSupplierProvider, passwordEncoder)
		);
	}

	@Test
	void shouldRejectExternalManagerSecretSourceWhenNoSupplierBeanExists() {
		SecureProfileSecurityProperties properties = baseInMemoryProperties();
		properties.setSecretSource(SecureProfileSecurityProperties.SecretSource.EXTERNAL_MANAGER);
		properties.setPassword(null);
		properties.setPasswordEncoded(null);
		properties.setExternalSecretRef("vault://fraud-engine/secure-profile");

		ObjectProvider<SecureProfileSecretSupplier> secretSupplierProvider = mock(ObjectProvider.class);
		when(secretSupplierProvider.getIfAvailable()).thenReturn(null);

		assertThrows(
			IllegalStateException.class,
			() -> configuration.userDetailsService(properties, secretSupplierProvider, passwordEncoder)
		);
	}

	@Test
	void shouldAllowExternalManagerSecretSourceWhenSupplierReturnsEncodedCredentials() {
		SecureProfileSecurityProperties properties = baseInMemoryProperties();
		properties.setSecretSource(SecureProfileSecurityProperties.SecretSource.EXTERNAL_MANAGER);
		properties.setPassword(null);
		properties.setPasswordEncoded(null);
		properties.setExternalSecretRef("vault://fraud-engine/secure-profile");

		SecureProfileSecretSupplier secretSupplier = externalSecretRef -> new SecureProfileResolvedSecrets(
			"secure-user",
			null,
			"$2a$10$abcdefghijklmnopqrstuv"
		);

		ObjectProvider<SecureProfileSecretSupplier> secretSupplierProvider = mock(ObjectProvider.class);
		when(secretSupplierProvider.getIfAvailable()).thenReturn(secretSupplier);

		assertDoesNotThrow(() -> configuration.userDetailsService(properties, secretSupplierProvider, passwordEncoder));
	}

	private static SecureProfileSecurityProperties baseInMemoryProperties() {
		SecureProfileSecurityProperties properties = new SecureProfileSecurityProperties();
		properties.setIdentityProvider(SecureProfileSecurityProperties.IdentityProvider.IN_MEMORY);
		properties.setUsername("secure-user");
		properties.setPassword("change-me-secure");
		properties.setRole("API_CLIENT");
		return properties;
	}
}
