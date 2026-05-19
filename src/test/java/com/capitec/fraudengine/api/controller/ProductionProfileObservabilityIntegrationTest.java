package com.capitec.fraudengine.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.capitec.fraudengine.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("production")
class ProductionProfileObservabilityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldExposeOnlyHealthEndpointForActuatorInProductionProfile() throws Exception {
		mockMvc.perform(get("/actuator/health"))
			.andExpect(status().isOk());

		mockMvc.perform(get("/actuator/metrics"))
			.andExpect(status().isNotFound());

		mockMvc.perform(get("/actuator/info"))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldDisableSwaggerAndOpenApiInProductionProfile() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
			.andExpect(status().isNotFound());

		mockMvc.perform(get("/v3/api-docs"))
			.andExpect(status().isNotFound());
	}
}
