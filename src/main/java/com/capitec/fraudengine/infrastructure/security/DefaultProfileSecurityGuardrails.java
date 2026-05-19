package com.capitec.fraudengine.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Startup guardrail messaging for intentionally-open default profile behavior.
 */
@Component
@Profile("default")
public class DefaultProfileSecurityGuardrails implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProfileSecurityGuardrails.class);
	private final Environment environment;

	public DefaultProfileSecurityGuardrails(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void run(ApplicationArguments args) {
		validateLocalOnlyDefaultProfile(environment);
		LOGGER.warn(
			"default_profile_security_guardrail message='The active profile is open-by-design for local/reviewer workflows. Do not use this mode outside local development.' openSurfaces='/api/**,/swagger-ui/**,/v3/api-docs/**,/actuator/**' recommendation='Use SPRING_PROFILES_ACTIVE=secure,production,or hardened for protected runtime behavior.'"
		);
	}

	/**
	 * Enforces default-profile local-only usage by rejecting hosted runtime signals.
	 *
	 * @param environment Spring environment for runtime property checks
	 */
	static void validateLocalOnlyDefaultProfile(Environment environment) {
		String hostedPort = environment.getProperty("PORT");
		if (StringUtils.hasText(hostedPort)) {
			throw new IllegalStateException(
				"Default profile cannot run in hosted runtime (PORT detected). Set SPRING_PROFILES_ACTIVE to secure, production, or hardened."
			);
		}
	}
}
