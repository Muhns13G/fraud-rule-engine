package com.capitec.fraudengine.infrastructure.security;

import javax.sql.DataSource;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

	@Bean
	public SecurityFilterChain secureSecurityFilterChain(
		HttpSecurity http,
		SecureProfileSecurityProperties properties
	) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(Customizer.withDefaults())
			.logout(AbstractHttpConfigurer::disable)
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

	@Bean
	@ConditionalOnProperty(
		prefix = "app.security.secure-profile",
		name = "identity-provider",
		havingValue = "IN_MEMORY",
		matchIfMissing = true
	)
	public UserDetailsService userDetailsService(
		SecureProfileSecurityProperties properties,
		PasswordEncoder passwordEncoder
	) {
		String rawPassword = normalize(properties.getPassword());
		String encodedPassword = normalize(properties.getPasswordEncoded());

		if (rawPassword == null && encodedPassword == null) {
			throw new IllegalStateException(
				"Secure profile requires either app.security.secure-profile.password or password-encoded when identity-provider=IN_MEMORY."
			);
		}

		UserDetails secureUser = User.withUsername(properties.getUsername())
			.password(resolveStoredPassword(rawPassword, encodedPassword, passwordEncoder))
			.roles(properties.getRole())
			.build();

		return new InMemoryUserDetailsManager(secureUser);
	}

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

		String usersByUsernameQuery = normalize(properties.getUsersByUsernameQuery());
		if (usersByUsernameQuery != null) {
			manager.setUsersByUsernameQuery(usersByUsernameQuery);
		}

		String authoritiesByUsernameQuery = normalize(properties.getAuthoritiesByUsernameQuery());
		if (authoritiesByUsernameQuery != null) {
			manager.setAuthoritiesByUsernameQuery(authoritiesByUsernameQuery);
		}

		return manager;
	}

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
}
