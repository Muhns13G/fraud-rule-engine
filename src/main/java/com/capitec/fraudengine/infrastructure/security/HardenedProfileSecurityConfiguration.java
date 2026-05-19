package com.capitec.fraudengine.infrastructure.security;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.http.HttpMethod;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

/**
 * Hardened profile security baseline for token-based authentication.
 */
@Configuration
@Profile("hardened")
@EnableConfigurationProperties(HardenedProfileSecurityProperties.class)
public class HardenedProfileSecurityConfiguration {

	@Bean
	public SecurityFilterChain hardenedSecurityFilterChain(
		HttpSecurity http,
		HardenedProfileSecurityProperties properties,
		Converter<Jwt, org.springframework.security.authentication.AbstractAuthenticationToken> jwtAuthenticationConverter,
		SecurityDiagnosticsHandlers securityDiagnosticsHandlers
	) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.exceptionHandling(exceptionHandling -> exceptionHandling
				.authenticationEntryPoint(securityDiagnosticsHandlers.authenticationEntryPoint())
				.accessDeniedHandler(securityDiagnosticsHandlers.accessDeniedHandler())
			)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(HttpMethod.PATCH, "/api/admin/rules/**")
				.hasAnyRole(uniqueRoles("GOVERNANCE_ADMIN", "PLATFORM_ADMIN"))
				.requestMatchers(HttpMethod.POST, "/api/admin/rules/**")
				.hasAnyRole(uniqueRoles("GOVERNANCE_ADMIN", "PLATFORM_ADMIN"))
				.requestMatchers(HttpMethod.GET, "/api/admin/rules/**")
				.hasAnyRole(uniqueRoles("OPS_READER", "GOVERNANCE_ADMIN", "PLATFORM_ADMIN"))
				.requestMatchers("/actuator/**")
				.hasAnyRole(uniqueRoles("OPS_READER", "GOVERNANCE_ADMIN", "PLATFORM_ADMIN"))
				.requestMatchers("/api/**")
				.hasAnyRole(uniqueRoles("API_CLIENT", "OPS_READER", "GOVERNANCE_ADMIN", "PLATFORM_ADMIN"))
				.requestMatchers(
					"/swagger-ui.html",
					"/swagger-ui/**",
					"/v3/api-docs/**"
				)
				.hasAnyRole(uniqueRoles("OPS_READER", "GOVERNANCE_ADMIN", "PLATFORM_ADMIN"))
				.anyRequest().permitAll()
			)
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
			.headers(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public Converter<Jwt, org.springframework.security.authentication.AbstractAuthenticationToken> jwtAuthenticationConverter(
		HardenedProfileSecurityProperties properties
	) {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		String rolesClaim = normalize(properties.getRolesClaim());
		if (rolesClaim != null) {
			grantedAuthoritiesConverter.setAuthoritiesClaimName(rolesClaim);
		}
		grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		String principalClaim = normalize(properties.getPrincipalClaim());
		if (principalClaim != null) {
			jwtAuthenticationConverter.setPrincipalClaimName(principalClaim);
		}
		return jwtAuthenticationConverter;
	}

	@Bean
	public JwtDecoder jwtDecoder(HardenedProfileSecurityProperties properties) {
		String jwkSetUri = properties.getJwkSetUri();
		if (!StringUtils.hasText(jwkSetUri)) {
			throw new IllegalStateException(
				"Hardened profile requires app.security.hardened-profile.jwk-set-uri for JWT validation."
			);
		}

		return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
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

	private static String normalize(String value) {
		if (value == null) {
			return null;
		}
		String normalized = value.trim();
		return normalized.isEmpty() ? null : normalized;
	}
}
