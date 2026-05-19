package com.capitec.fraudengine.infrastructure.security;

import javax.sql.DataSource;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security baseline for the secure profile using HTTP Basic authentication.
 */
@Configuration
@Profile("secure")
@EnableConfigurationProperties(SecureProfileSecurityProperties.class)
public class SecureProfileSecurityConfiguration {

	private static final String DEFAULT_USERS_BY_USERNAME_QUERY =
		"select username, password, enabled from users where username = ?";
	private static final String DEFAULT_AUTHORITIES_BY_USERNAME_QUERY =
		"select username, authority from authorities where username = ?";

	/**
	 * Configures secure-profile authorization using HTTP Basic authentication.
	 *
	 * @param http Spring Security HTTP builder
	 * @param properties secure profile identity and role properties
	 * @param securityDiagnosticsHandlers handlers for structured authn/authz denial diagnostics
	 * @return configured secure-profile security filter chain
	 * @throws Exception if the filter chain cannot be built
	 */
	@Bean
	public SecurityFilterChain secureSecurityFilterChain(
		HttpSecurity http,
		SecureProfileSecurityProperties properties,
		SecurityDiagnosticsHandlers securityDiagnosticsHandlers
	) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(Customizer.withDefaults())
			.logout(AbstractHttpConfigurer::disable)
			.exceptionHandling(exceptionHandling -> exceptionHandling
				.authenticationEntryPoint(securityDiagnosticsHandlers.authenticationEntryPoint())
				.accessDeniedHandler(securityDiagnosticsHandlers.accessDeniedHandler())
			)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(HttpMethod.PATCH, "/api/admin/rules/**")
				.hasAnyRole(uniqueRoles(properties.getAdminRole(), properties.getPlatformAdminRole()))
				.requestMatchers(HttpMethod.POST, "/api/admin/rules/**")
				.hasAnyRole(uniqueRoles(properties.getAdminRole(), properties.getPlatformAdminRole()))
				.requestMatchers(HttpMethod.GET, "/api/admin/rules/**")
				.hasAnyRole(uniqueRoles(
					properties.getOpsReaderRole(),
					properties.getAdminRole(),
					properties.getPlatformAdminRole()
				))
				.requestMatchers("/actuator/**")
				.hasAnyRole(uniqueRoles(
					properties.getOpsReaderRole(),
					properties.getAdminRole(),
					properties.getPlatformAdminRole()
				))
				.requestMatchers("/api/**")
				.hasAnyRole(uniqueRoles(
					properties.getRole(),
					properties.getOpsReaderRole(),
					properties.getAdminRole(),
					properties.getPlatformAdminRole()
				))
				.requestMatchers(
					"/swagger-ui.html",
					"/swagger-ui/**",
					"/v3/api-docs/**"
				)
				.hasAnyRole(uniqueRoles(
					properties.getOpsReaderRole(),
					properties.getAdminRole(),
					properties.getPlatformAdminRole()
				))
				.anyRequest().permitAll()
			)
			.headers(Customizer.withDefaults());

		return http.build();
	}

	/**
	 * Builds an in-memory secure-profile identity provider from configured secret sources.
	 *
	 * @param properties secure profile identity properties
	 * @param secretSupplierProvider provider for optional external secret supplier integration
	 * @param passwordEncoder encoder used for raw password secret sources
	 * @return configured user details service for HTTP Basic authentication
	 */
	@Bean
	@ConditionalOnProperty(
		prefix = "app.security.secure-profile",
		name = "identity-provider",
		havingValue = "IN_MEMORY",
		matchIfMissing = true
	)
	public UserDetailsService userDetailsService(
		SecureProfileSecurityProperties properties,
		ObjectProvider<SecureProfileSecretSupplier> secretSupplierProvider,
		PasswordEncoder passwordEncoder
	) {
		ResolvedInMemorySecrets resolvedSecrets = resolveInMemorySecrets(properties, secretSupplierProvider);

		UserDetails primaryUser = User.withUsername(resolvedSecrets.username())
			.password(resolveStoredPassword(
				resolvedSecrets.password(),
				resolvedSecrets.passwordEncoded(),
				passwordEncoder
			))
			.roles(properties.getRole())
			.build();

		List<UserDetails> secureUsers = new ArrayList<>();
		secureUsers.add(primaryUser);

		RotationCandidateSecrets rotationCandidate = resolveRotationCandidate(properties, resolvedSecrets.username());
		if (rotationCandidate != null) {
			UserDetails rotationUser = User.withUsername(rotationCandidate.username())
				.password(resolveStoredPassword(
					rotationCandidate.password(),
					rotationCandidate.passwordEncoded(),
					passwordEncoder
				))
				.roles(properties.getRole())
				.build();
			secureUsers.add(rotationUser);
		}

		return new InMemoryUserDetailsManager(secureUsers);
	}

	/**
	 * Builds a JDBC-backed secure-profile identity provider.
	 *
	 * @param dataSource application datasource
	 * @param properties secure profile identity properties
	 * @return configured JDBC user details manager
	 */
	@Bean
	@ConditionalOnProperty(
		prefix = "app.security.secure-profile",
		name = "identity-provider",
		havingValue = "JDBC"
	)
	public UserDetailsService jdbcUserDetailsService(
		DataSource dataSource,
		SecureProfileSecurityProperties properties
	) {
		JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);

		String usersByUsernameQuery = resolveJdbcUsersByUsernameQuery(properties);
		String authoritiesByUsernameQuery = resolveJdbcAuthoritiesByUsernameQuery(properties);
		manager.setUsersByUsernameQuery(usersByUsernameQuery);
		manager.setAuthoritiesByUsernameQuery(authoritiesByUsernameQuery);

		return manager;
	}

	/**
	 * Provides the password encoder used by secure-profile identity sources.
	 *
	 * @return BCrypt password encoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private static String resolveStoredPassword(
		String rawPassword,
		String encodedPassword,
		PasswordEncoder passwordEncoder
	) {
		if (encodedPassword != null) {
			return encodedPassword;
		}

		return passwordEncoder.encode(rawPassword);
	}

	private static String normalize(String value) {
		if (value == null) {
			return null;
		}

		String normalized = value.trim();
		return normalized.isEmpty() ? null : normalized;
	}

	private static String[] uniqueRoles(String... roles) {
		Set<String> uniqueRoles = new LinkedHashSet<>();
		for (String role : roles) {
			String normalizedRole = normalize(role);
			if (normalizedRole != null) {
				uniqueRoles.add(normalizedRole);
			}
		}
		return uniqueRoles.toArray(String[]::new);
	}

	private static String resolveJdbcUsersByUsernameQuery(SecureProfileSecurityProperties properties) {
		String configuredQuery = normalize(properties.getUsersByUsernameQuery());
		String query = configuredQuery != null ? configuredQuery : DEFAULT_USERS_BY_USERNAME_QUERY;
		validateJdbcUsersByUsernameQuery(query);
		return query;
	}

	private static String resolveJdbcAuthoritiesByUsernameQuery(SecureProfileSecurityProperties properties) {
		String configuredQuery = normalize(properties.getAuthoritiesByUsernameQuery());
		String query = configuredQuery != null ? configuredQuery : DEFAULT_AUTHORITIES_BY_USERNAME_QUERY;
		validateJdbcAuthoritiesByUsernameQuery(query);
		return query;
	}

	private static void validateJdbcUsersByUsernameQuery(String query) {
		String normalizedQuery = query.toLowerCase();
		if (!normalizedQuery.startsWith("select ")) {
			throw new IllegalStateException(
				"Secure profile JDBC users-by-username-query must be a SELECT statement."
			);
		}
		if (!normalizedQuery.contains("?")) {
			throw new IllegalStateException(
				"Secure profile JDBC users-by-username-query must contain a username parameter placeholder '?'."
			);
		}
		if (!normalizedQuery.contains("password")) {
			throw new IllegalStateException(
				"Secure profile JDBC users-by-username-query must select a password column."
			);
		}
		if (!normalizedQuery.contains("enabled")) {
			throw new IllegalStateException(
				"Secure profile JDBC users-by-username-query must select an enabled column."
			);
		}
	}

	private static void validateJdbcAuthoritiesByUsernameQuery(String query) {
		String normalizedQuery = query.toLowerCase();
		if (!normalizedQuery.startsWith("select ")) {
			throw new IllegalStateException(
				"Secure profile JDBC authorities-by-username-query must be a SELECT statement."
			);
		}
		if (!normalizedQuery.contains("?")) {
			throw new IllegalStateException(
				"Secure profile JDBC authorities-by-username-query must contain a username parameter placeholder '?'."
			);
		}
		if (!normalizedQuery.contains("authority")) {
			throw new IllegalStateException(
				"Secure profile JDBC authorities-by-username-query must select an authority column."
			);
		}
	}

	private static ResolvedInMemorySecrets resolveInMemorySecrets(
		SecureProfileSecurityProperties properties,
		ObjectProvider<SecureProfileSecretSupplier> secretSupplierProvider
	) {
		SecureProfileSecurityProperties.SecretSource secretSource = properties.getSecretSource();
		if (secretSource == null) {
			secretSource = SecureProfileSecurityProperties.SecretSource.ENV;
		}

		return switch (secretSource) {
			case ENV -> resolveEnvSecrets(properties);
			case PRE_ENCODED -> resolvePreEncodedSecrets(properties);
			case EXTERNAL_MANAGER -> resolveExternalManagerSecrets(properties, secretSupplierProvider);
		};
	}

	private static ResolvedInMemorySecrets resolveEnvSecrets(SecureProfileSecurityProperties properties) {
		String username = normalize(properties.getUsername());
		String rawPassword = normalize(properties.getPassword());
		String encodedPassword = normalize(properties.getPasswordEncoded());

		if (username == null || rawPassword == null) {
			throw new IllegalStateException(
				"Secure profile secret-source ENV requires username and password."
			);
		}

		if (encodedPassword != null) {
			throw new IllegalStateException(
				"Secure profile secret-source ENV does not allow password-encoded."
			);
		}

		return new ResolvedInMemorySecrets(username, rawPassword, null);
	}

	private static ResolvedInMemorySecrets resolvePreEncodedSecrets(SecureProfileSecurityProperties properties) {
		String username = normalize(properties.getUsername());
		String rawPassword = normalize(properties.getPassword());
		String encodedPassword = normalize(properties.getPasswordEncoded());

		if (username == null || encodedPassword == null) {
			throw new IllegalStateException(
				"Secure profile secret-source PRE_ENCODED requires username and password-encoded."
			);
		}

		if (rawPassword != null) {
			throw new IllegalStateException(
				"Secure profile secret-source PRE_ENCODED does not allow raw password."
			);
		}

		return new ResolvedInMemorySecrets(username, null, encodedPassword);
	}

	private static ResolvedInMemorySecrets resolveExternalManagerSecrets(
		SecureProfileSecurityProperties properties,
		ObjectProvider<SecureProfileSecretSupplier> secretSupplierProvider
	) {
		String externalSecretRef = normalize(properties.getExternalSecretRef());
		if (externalSecretRef == null) {
			throw new IllegalStateException(
				"Secure profile secret-source EXTERNAL_MANAGER requires external-secret-ref."
			);
		}

		if (normalize(properties.getPassword()) != null || normalize(properties.getPasswordEncoded()) != null) {
			throw new IllegalStateException(
				"Secure profile secret-source EXTERNAL_MANAGER does not allow local password properties."
			);
		}

		SecureProfileSecretSupplier secretSupplier = secretSupplierProvider.getIfAvailable();
		if (secretSupplier == null) {
			throw new IllegalStateException(
				"Secure profile secret-source EXTERNAL_MANAGER requires a SecureProfileSecretSupplier bean."
			);
		}

		SecureProfileResolvedSecrets resolvedSecrets = secretSupplier.resolve(externalSecretRef);
		if (resolvedSecrets == null) {
			throw new IllegalStateException(
				"Secure profile external secret supplier returned null credentials."
			);
		}

		String username = normalize(resolvedSecrets.username());
		String rawPassword = normalize(resolvedSecrets.password());
		String encodedPassword = normalize(resolvedSecrets.passwordEncoded());

		if (username == null) {
			throw new IllegalStateException(
				"Secure profile external secret supplier must provide username."
			);
		}

		if ((rawPassword == null && encodedPassword == null) || (rawPassword != null && encodedPassword != null)) {
			throw new IllegalStateException(
				"Secure profile external secret supplier must provide exactly one of password or passwordEncoded."
			);
		}

		return new ResolvedInMemorySecrets(username, rawPassword, encodedPassword);
	}

	private static RotationCandidateSecrets resolveRotationCandidate(
		SecureProfileSecurityProperties properties,
		String primaryUsername
	) {
		if (!properties.isRotationEnabled()) {
			return null;
		}

		String rotationUsername = normalize(properties.getRotationUsername());
		String rotationPassword = normalize(properties.getRotationPassword());
		String rotationPasswordEncoded = normalize(properties.getRotationPasswordEncoded());

		if (rotationUsername == null) {
			throw new IllegalStateException(
				"Secure profile rotation requires rotation-username when rotation-enabled=true."
			);
		}

		if (rotationUsername.equals(primaryUsername)) {
			throw new IllegalStateException(
				"Secure profile rotation-username must differ from the primary username."
			);
		}

		if ((rotationPassword == null && rotationPasswordEncoded == null)
			|| (rotationPassword != null && rotationPasswordEncoded != null)) {
			throw new IllegalStateException(
				"Secure profile rotation credentials must provide exactly one of rotation-password or rotation-password-encoded."
			);
		}

		return new RotationCandidateSecrets(rotationUsername, rotationPassword, rotationPasswordEncoded);
	}

	private record ResolvedInMemorySecrets(
		String username,
		String password,
		String passwordEncoded
	) {
	}

	private record RotationCandidateSecrets(
		String username,
		String password,
		String passwordEncoded
	) {
	}
}
