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
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;
import java.time.Duration;
import java.util.List;

/**
 * Hardened profile security baseline for token-based authentication.
 */
@Configuration
@Profile("hardened | production")
@EnableConfigurationProperties(HardenedProfileSecurityProperties.class)
public class HardenedProfileSecurityConfiguration {

	/**
	 * Configures hardened-profile request authorization and token-based authentication.
	 *
	 * @param http Spring Security HTTP builder
	 * @param properties hardened profile identity properties
	 * @param jwtAuthenticationConverter converter that maps JWT claims to Spring authorities
	 * @param securityDiagnosticsHandlers handlers for structured authn/authz denial diagnostics
	 * @return configured hardened-profile security filter chain
	 * @throws Exception if the filter chain cannot be built
	 */
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

	/**
	 * Builds a JWT authentication converter that maps configured token claims to application roles.
	 *
	 * @param properties hardened profile identity properties
	 * @return converter that produces Spring authentication tokens from JWTs
	 */
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

	/**
	 * Creates the hardened-profile JWT decoder using the configured JWK set URI.
	 *
	 * @param properties hardened profile identity properties
	 * @return JWT decoder backed by the configured JWK set URI
	 */
	@Bean
	public JwtDecoder jwtDecoder(HardenedProfileSecurityProperties properties) {
		String issuerUri = properties.getIssuerUri();
		String jwkSetUri = properties.getJwkSetUri();
		String audience = properties.getAudience();
		if (!StringUtils.hasText(issuerUri)) {
			throw new IllegalStateException(
				"Hardened profile requires app.security.hardened-profile.issuer-uri for JWT validation."
			);
		}
		if (!StringUtils.hasText(jwkSetUri)) {
			throw new IllegalStateException(
				"Hardened profile requires app.security.hardened-profile.jwk-set-uri for JWT validation."
			);
		}
		if (!StringUtils.hasText(audience)) {
			throw new IllegalStateException(
				"Hardened profile requires app.security.hardened-profile.audience for JWT validation."
			);
		}

		NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
		decoder.setJwtValidator(jwtValidator(issuerUri, audience, properties.getClockSkewSeconds()));
		return decoder;
	}

	OAuth2TokenValidator<Jwt> jwtValidator(String issuerUri, String audience, int clockSkewSeconds) {
		JwtTimestampValidator timestampValidator = clockSkewSeconds > 0
			? new JwtTimestampValidator(Duration.ofSeconds(clockSkewSeconds))
			: new JwtTimestampValidator();

		return new DelegatingOAuth2TokenValidator<>(
			JwtValidators.createDefaultWithIssuer(issuerUri),
			audienceValidator(audience),
			timestampValidator
		);
	}

	private OAuth2TokenValidator<Jwt> audienceValidator(String audience) {
		return jwt -> {
			List<String> audiences = jwt.getAudience();
			if (audiences != null && audiences.contains(audience)) {
				return OAuth2TokenValidatorResult.success();
			}

			return OAuth2TokenValidatorResult.failure(new OAuth2Error(
				"invalid_token",
				"JWT audience claim does not include required audience.",
				null
			));
		};
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
