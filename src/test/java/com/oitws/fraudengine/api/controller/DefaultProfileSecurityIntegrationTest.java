package com.oitws.fraudengine.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.oitws.fraudengine.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class DefaultProfileSecurityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldAllowFraudEvaluationListWithoutAuthenticationInDefaultProfile() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations"))
			.andExpect(status().isOk());
	}

	@Test
	void shouldAllowSwaggerUiWithoutAuthenticationInDefaultProfile() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void shouldAllowActuatorMetricsWithoutAuthenticationInDefaultProfile() throws Exception {
		mockMvc.perform(get("/actuator/metrics"))
			.andExpect(status().isOk());
	}

	@Test
	void shouldAllowGovernanceMutationPathWithoutAuthenticationInDefaultProfile() throws Exception {
		mockMvc.perform(patch("/api/admin/rules/DOES_NOT_EXIST/versions/1.0.0/state")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "lifecycleStatus": "ACTIVE",
					  "activationState": "ACTIVE"
					}
					"""))
			.andExpect(status().isNotFound());
	}
}
