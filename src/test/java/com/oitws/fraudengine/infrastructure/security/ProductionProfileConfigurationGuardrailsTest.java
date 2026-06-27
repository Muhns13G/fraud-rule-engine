package com.oitws.fraudengine.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class ProductionProfileConfigurationGuardrailsTest {

	@Test
	void shouldAcceptStrictProductionExposureContract() {
		MockEnvironment environment = productionEnvironment("health", false, false, "never");

		assertThatCode(() -> ProductionProfileConfigurationGuardrails.validateProductionActuatorExposure(environment))
			.doesNotThrowAnyException();
		assertThatCode(() -> ProductionProfileConfigurationGuardrails.validateProductionDocsExposure(environment))
			.doesNotThrowAnyException();
		assertThatCode(() -> ProductionProfileConfigurationGuardrails.validateProductionHealthDetails(environment))
			.doesNotThrowAnyException();
	}

	@Test
	void shouldRejectProductionActuatorExposureBeyondHealth() {
		MockEnvironment environment = productionEnvironment("health,info", false, false, "never");

		assertThatThrownBy(() -> ProductionProfileConfigurationGuardrails.validateProductionActuatorExposure(environment))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("only actuator health");
	}

	@Test
	void shouldRejectEnabledDocsInProduction() {
		MockEnvironment environment = productionEnvironment("health", true, false, "never");

		assertThatThrownBy(() -> ProductionProfileConfigurationGuardrails.validateProductionDocsExposure(environment))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Swagger/OpenAPI");
	}

	@Test
	void shouldRejectHealthDetailsVisibleInProduction() {
		MockEnvironment environment = productionEnvironment("health", false, false, "when_authorized");

		assertThatThrownBy(() -> ProductionProfileConfigurationGuardrails.validateProductionHealthDetails(environment))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("show-details=never");
	}

	private static MockEnvironment productionEnvironment(
		String actuatorExposure,
		boolean swaggerEnabled,
		boolean apiDocsEnabled,
		String healthShowDetails
	) {
		return new MockEnvironment()
			.withProperty("management.endpoints.web.exposure.include", actuatorExposure)
			.withProperty("springdoc.swagger-ui.enabled", Boolean.toString(swaggerEnabled))
			.withProperty("springdoc.api-docs.enabled", Boolean.toString(apiDocsEnabled))
			.withProperty("management.endpoint.health.show-details", healthShowDetails);
	}
}
