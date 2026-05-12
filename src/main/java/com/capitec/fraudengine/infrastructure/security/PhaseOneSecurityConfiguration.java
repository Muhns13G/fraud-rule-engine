package com.capitec.fraudengine.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Temporary Phase 1 security configuration for local development and reviewer usability.
 * This intentionally avoids authentication while the take-home focuses on fraud evaluation behavior.
 */
@Configuration
public class PhaseOneSecurityConfiguration {

	/**
	 * Configures permissive local access for the Phase 1 API surface and documentation endpoints.
	 *
	 * @param http Spring Security HTTP builder
	 * @return configured security filter chain
	 * @throws Exception if the filter chain cannot be built
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(
					"/api/**",
					"/swagger-ui.html",
					"/swagger-ui/**",
					"/v3/api-docs/**",
					"/actuator/health"
				).permitAll()
				.anyRequest().permitAll()
			)
			.headers(Customizer.withDefaults());

		return http.build();
	}
}
