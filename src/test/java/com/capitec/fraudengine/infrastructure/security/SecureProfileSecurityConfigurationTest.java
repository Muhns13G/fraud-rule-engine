package com.capitec.fraudengine.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.sql.DataSource;

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

		IllegalStateException exception = assertThrows(
			IllegalStateException.class,
			() -> configuration.userDetailsService(properties, secretSupplierProvider, passwordEncoder)
		);
		assertTrue(exception.getMessage().contains("secret-source ENV does not allow password-encoded"));
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

		IllegalStateException exception = assertThrows(
			IllegalStateException.class,
			() -> configuration.userDetailsService(properties, secretSupplierProvider, passwordEncoder)
		);
		assertTrue(
			exception.getMessage().contains("requires a SecureProfileSecretSupplier bean"),
			"Expected explicit guidance about missing SecureProfileSecretSupplier bean."
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

	@Test
	void shouldAllowRotationCandidateWhenConfiguredWithDistinctUsername() {
		SecureProfileSecurityProperties properties = baseInMemoryProperties();
		properties.setRotationEnabled(true);
		properties.setRotationUsername("secure-user-rotating");
		properties.setRotationPassword("change-me-rotating");

		ObjectProvider<SecureProfileSecretSupplier> secretSupplierProvider = mock(ObjectProvider.class);
		when(secretSupplierProvider.getIfAvailable()).thenReturn(null);

		assertDoesNotThrow(() -> configuration.userDetailsService(properties, secretSupplierProvider, passwordEncoder));
	}

	@Test
	void shouldRejectRotationCandidateWhenUsernameMatchesPrimaryUsername() {
		SecureProfileSecurityProperties properties = baseInMemoryProperties();
		properties.setRotationEnabled(true);
		properties.setRotationUsername("secure-user");
		properties.setRotationPassword("change-me-rotating");

		ObjectProvider<SecureProfileSecretSupplier> secretSupplierProvider = mock(ObjectProvider.class);
		when(secretSupplierProvider.getIfAvailable()).thenReturn(null);

		assertThrows(
			IllegalStateException.class,
			() -> configuration.userDetailsService(properties, secretSupplierProvider, passwordEncoder)
		);
	}

	@Test
	void shouldRejectRotationCandidateWhenBothRawAndEncodedPasswordsAreConfigured() {
		SecureProfileSecurityProperties properties = baseInMemoryProperties();
		properties.setRotationEnabled(true);
		properties.setRotationUsername("secure-user-rotating");
		properties.setRotationPassword("change-me-rotating");
		properties.setRotationPasswordEncoded("$2a$10$abcdefghijklmnopqrstuv");

		ObjectProvider<SecureProfileSecretSupplier> secretSupplierProvider = mock(ObjectProvider.class);
		when(secretSupplierProvider.getIfAvailable()).thenReturn(null);

		IllegalStateException exception = assertThrows(
			IllegalStateException.class,
			() -> configuration.userDetailsService(properties, secretSupplierProvider, passwordEncoder)
		);
		assertTrue(
			exception.getMessage().contains("must provide exactly one of rotation-password or rotation-password-encoded"),
			"Expected explicit guidance for invalid rotation credential combination."
		);
	}

	@Test
	void shouldUseDefaultJdbcQueriesWhenNoneAreConfigured() {
		SecureProfileSecurityProperties properties = new SecureProfileSecurityProperties();
		properties.setIdentityProvider(SecureProfileSecurityProperties.IdentityProvider.JDBC);

		assertDoesNotThrow(() -> configuration.jdbcUserDetailsService(mock(DataSource.class), properties));
	}

	@Test
	void shouldRejectJdbcUsersQueryWithoutPlaceholder() {
		SecureProfileSecurityProperties properties = new SecureProfileSecurityProperties();
		properties.setIdentityProvider(SecureProfileSecurityProperties.IdentityProvider.JDBC);
		properties.setUsersByUsernameQuery("select username, password, enabled from users");

		assertThrows(
			IllegalStateException.class,
			() -> configuration.jdbcUserDetailsService(mock(DataSource.class), properties)
		);
	}

	@Test
	void shouldRejectJdbcAuthoritiesQueryWithoutAuthorityColumn() {
		SecureProfileSecurityProperties properties = new SecureProfileSecurityProperties();
		properties.setIdentityProvider(SecureProfileSecurityProperties.IdentityProvider.JDBC);
		properties.setAuthoritiesByUsernameQuery("select username, role from users where username = ?");

		IllegalStateException exception = assertThrows(
			IllegalStateException.class,
			() -> configuration.jdbcUserDetailsService(mock(DataSource.class), properties)
		);
		assertTrue(
			exception.getMessage().contains("must select an authority column"),
			"Expected explicit guidance about missing authority column."
		);
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
