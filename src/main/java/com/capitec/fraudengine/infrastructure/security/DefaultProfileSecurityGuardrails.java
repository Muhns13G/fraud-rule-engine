package com.capitec.fraudengine.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Startup guardrail messaging for intentionally-open default profile behavior.
 */
@Component
@Profile("!secure")
public class DefaultProfileSecurityGuardrails implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProfileSecurityGuardrails.class);

	@Override
	public void run(ApplicationArguments args) {
		LOGGER.warn(
			"default_profile_security_guardrail message='The active profile is open-by-design for local/reviewer workflows. Do not use this mode outside local development.' openSurfaces='/api/**,/swagger-ui/**,/v3/api-docs/**,/actuator/**' recommendation='Use SPRING_PROFILES_ACTIVE=secure for protected runtime behavior.'"
		);
	}
}
