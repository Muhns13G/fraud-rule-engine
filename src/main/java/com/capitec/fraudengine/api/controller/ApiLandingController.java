package com.capitec.fraudengine.api.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ApiLandingController {

	@GetMapping("/")
	Map<String, String> landing() {
		return Map.of(
			"service", "fraud-rule-engine",
			"message", "Fraud Rule Engine API is running. Use README/Postman for verification flows.",
			"health", "/actuator/health",
			"info", "/actuator/info"
		);
	}
}
