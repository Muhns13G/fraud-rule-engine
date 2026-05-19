package com.capitec.fraudengine.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

	private static HardenedProfileSecurityProperties baseProperties() {
		HardenedProfileSecurityProperties properties = new HardenedProfileSecurityProperties();
		properties.setAuthMechanism("JWT_OIDC");
		properties.setJwkSetUri("https://issuer.example/.well-known/jwks.json");
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
