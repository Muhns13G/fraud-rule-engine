package com.capitec.fraudengine.infrastructure.security;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Fail-fast startup validation for production-profile observability safety defaults.
 */
@Component
@Profile("production")
public class ProductionProfileConfigurationGuardrails implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductionProfileConfigurationGuardrails.class);

	private final Environment environment;

	public ProductionProfileConfigurationGuardrails(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void run(ApplicationArguments args) {
		validateProductionActuatorExposure(environment);
		validateProductionDocsExposure(environment);
		validateProductionHealthDetails(environment);

		LOGGER.info(
			"production_profile_configuration_guardrail status=validated actuatorExposure={} healthDetails={} swaggerEnabled={} apiDocsEnabled={}",
			environment.getProperty("management.endpoints.web.exposure.include"),
			environment.getProperty("management.endpoint.health.show-details"),
			environment.getProperty("springdoc.swagger-ui.enabled"),
			environment.getProperty("springdoc.api-docs.enabled")
		);
	}

	static void validateProductionActuatorExposure(Environment environment) {
		String exposure = environment.getProperty("management.endpoints.web.exposure.include", "");
		Set<String> includedEndpoints = Stream.of(exposure.split(","))
			.map(String::trim)
			.filter(value -> !value.isEmpty())
			.collect(Collectors.toSet());

		if (!includedEndpoints.equals(Set.of("health"))) {
			throw new IllegalStateException(
				"Production profile must expose only actuator health endpoint."
			);
		}
	}

	static void validateProductionDocsExposure(Environment environment) {
		boolean swaggerEnabled = environment.getProperty("springdoc.swagger-ui.enabled", Boolean.class, false);
		boolean apiDocsEnabled = environment.getProperty("springdoc.api-docs.enabled", Boolean.class, false);

		if (swaggerEnabled || apiDocsEnabled) {
			throw new IllegalStateException(
				"Production profile must keep Swagger/OpenAPI endpoints disabled."
			);
		}
	}

	static void validateProductionHealthDetails(Environment environment) {
		String showDetails = environment.getProperty("management.endpoint.health.show-details", "");
		if (!"never".equalsIgnoreCase(showDetails.trim())) {
			throw new IllegalStateException(
				"Production profile must set management.endpoint.health.show-details=never."
			);
		}
	}
}
