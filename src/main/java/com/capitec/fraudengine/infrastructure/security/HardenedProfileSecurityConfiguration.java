package com.capitec.fraudengine.infrastructure.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
				.requestMatchers(
					"/api/**",
					"/swagger-ui.html",
					"/swagger-ui/**",
					"/v3/api-docs/**",
					"/actuator/**"
				).authenticated()
				.anyRequest().permitAll()
			)
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
			.headers(Customizer.withDefaults());

		return http.build();
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
}
