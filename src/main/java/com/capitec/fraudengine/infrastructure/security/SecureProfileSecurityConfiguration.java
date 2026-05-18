package com.capitec.fraudengine.infrastructure.security;

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
	public SecurityFilterChain secureSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(Customizer.withDefaults())
			.logout(AbstractHttpConfigurer::disable)
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
			.headers(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService(
		SecureProfileSecurityProperties properties,
		PasswordEncoder passwordEncoder
	) {
		UserDetails secureUser = User.withUsername(properties.getUsername())
			.password(passwordEncoder.encode(properties.getPassword()))
			.roles(properties.getRole())
			.build();

		return new InMemoryUserDetailsManager(secureUser);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
