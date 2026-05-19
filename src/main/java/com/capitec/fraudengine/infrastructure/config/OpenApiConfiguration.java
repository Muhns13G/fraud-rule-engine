package com.capitec.fraudengine.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * OpenAPI metadata for the Capitec fraud rule engine take-home service.
 */
@Configuration
public class OpenApiConfiguration {

	@Bean
	public OpenAPI fraudRuleEngineOpenApi() {
		return new OpenAPI().info(new Info()
			.title("Fraud Rule Engine API")
			.version("v1")
			.description(
				"API for evaluating categorized transaction events, retrieving persisted fraud evaluations, and administering governed rule metadata."
			)
			.contact(new Contact()
				.name("Mansoer Gallie")
				.email("fraudengine@octothorp.co.za")));
	}
}
