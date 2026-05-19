package com.capitec.fraudengine.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Registers concrete external-manager adapters for secure-profile secret resolution.
 */
@Configuration
public class SecureProfileExternalSecretAdapterConfiguration {

	/**
	 * Environment-backed external-manager adapter.
	 *
	 * <p>Activation:
	 * {@code app.security.secure-profile.external-manager-adapter=ENV}
	 */
	@Bean
	@ConditionalOnProperty(
		prefix = "app.security.secure-profile",
		name = "external-manager-adapter",
		havingValue = "ENV"
	)
	public SecureProfileSecretSupplier envExternalManagerSecretSupplier(
		Environment environment,
		ObjectMapper objectMapper
	) {
		return new EnvExternalManagerSecretSupplier(environment, objectMapper);
	}
}
