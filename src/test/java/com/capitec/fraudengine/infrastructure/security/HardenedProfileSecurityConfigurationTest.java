package com.capitec.fraudengine.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;

class HardenedProfileSecurityConfigurationTest {

	private final HardenedProfileSecurityConfiguration configuration = new HardenedProfileSecurityConfiguration();

	@Test
	void shouldMapConfiguredRolesClaimToRoleAuthorities() {
		HardenedProfileSecurityProperties properties = baseProperties();
		properties.setRolesClaim("roles");
		properties.setPrincipalClaim("preferred_username");

		Converter<Jwt, AbstractAuthenticationToken> converter = configuration.jwtAuthenticationConverter(properties);
		Jwt jwt = jwtWithClaims(Map.of(
			"preferred_username", "token-user",
			"roles", List.of("OPS_READER", "GOVERNANCE_ADMIN")
		));

		AbstractAuthenticationToken authentication = converter.convert(jwt);
		Set<String> authorities = authentication.getAuthorities().stream()
			.map(grantedAuthority -> grantedAuthority.getAuthority())
			.collect(Collectors.toSet());

		assertEquals("token-user", authentication.getName());
		assertTrue(authorities.contains("ROLE_OPS_READER"));
		assertTrue(authorities.contains("ROLE_GOVERNANCE_ADMIN"));
	}

	@Test
	void shouldSupportAlternateRolesClaimName() {
		HardenedProfileSecurityProperties properties = baseProperties();
		properties.setRolesClaim("authorities");
		properties.setPrincipalClaim("sub");

		Converter<Jwt, AbstractAuthenticationToken> converter = configuration.jwtAuthenticationConverter(properties);
		Jwt jwt = jwtWithClaims(Map.of(
			"sub", "subject-user",
			"authorities", List.of("API_CLIENT", "PLATFORM_ADMIN")
		));

		AbstractAuthenticationToken authentication = converter.convert(jwt);
		Set<String> authorities = authentication.getAuthorities().stream()
			.map(grantedAuthority -> grantedAuthority.getAuthority())
			.collect(Collectors.toSet());

		assertEquals("subject-user", authentication.getName());
		assertTrue(authorities.contains("ROLE_API_CLIENT"));
		assertTrue(authorities.contains("ROLE_PLATFORM_ADMIN"));
	}

	@Test
	void shouldFailFastWhenIssuerUriIsMissing() {
		HardenedProfileSecurityProperties properties = baseProperties();
		properties.setIssuerUri("  ");

		IllegalStateException exception = assertThrows(
			IllegalStateException.class,
			() -> configuration.jwtDecoder(properties)
		);

		assertTrue(exception.getMessage().contains("issuer-uri"));
	}

	@Test
	void shouldFailFastWhenAudienceIsMissing() {
		HardenedProfileSecurityProperties properties = baseProperties();
		properties.setAudience("  ");

		IllegalStateException exception = assertThrows(
			IllegalStateException.class,
			() -> configuration.jwtDecoder(properties)
		);

		assertTrue(exception.getMessage().contains("audience"));
	}

	@Test
	void shouldRejectJwtWhenAudienceDoesNotMatchConfiguredAudience() {
		HardenedProfileSecurityProperties properties = baseProperties();
		OAuth2TokenValidator<Jwt> validator =
			configuration.jwtValidator(properties.getIssuerUri(), properties.getAudience(), properties.getClockSkewSeconds());
		Jwt jwt = jwtWithClaims(Map.of(
			"iss", properties.getIssuerUri(),
			"aud", List.of("other-audience"),
			"sub", "token-user",
			"roles", List.of("API_CLIENT")
		));

		OAuth2TokenValidatorResult result = validator.validate(jwt);

		assertTrue(result.hasErrors());
	}

	@Test
	void shouldRejectJwtWhenIssuerDoesNotMatchConfiguredIssuer() {
		HardenedProfileSecurityProperties properties = baseProperties();
		OAuth2TokenValidator<Jwt> validator =
			configuration.jwtValidator(properties.getIssuerUri(), properties.getAudience(), properties.getClockSkewSeconds());
		Jwt jwt = jwtWithClaims(Map.of(
			"iss", "https://other-issuer.example",
			"aud", List.of(properties.getAudience()),
			"sub", "token-user",
			"roles", List.of("API_CLIENT")
		));

		OAuth2TokenValidatorResult result = validator.validate(jwt);

		assertTrue(result.hasErrors());
	}

	private static HardenedProfileSecurityProperties baseProperties() {
		HardenedProfileSecurityProperties properties = new HardenedProfileSecurityProperties();
		properties.setAuthMechanism("JWT_OIDC");
		properties.setIssuerUri("https://issuer.example");
		properties.setJwkSetUri("https://issuer.example/.well-known/jwks.json");
		properties.setAudience("fraud-api");
		properties.setClockSkewSeconds(60);
		return properties;
	}

	private static Jwt jwtWithClaims(Map<String, Object> claims) {
		return new Jwt(
			"token-value",
			Instant.now(),
			Instant.now().plusSeconds(300),
			Map.of("alg", "none"),
			claims
		);
	}
}
