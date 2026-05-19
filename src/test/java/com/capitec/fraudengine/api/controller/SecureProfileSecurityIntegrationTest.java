package com.capitec.fraudengine.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.capitec.fraudengine.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("secure")
@TestPropertySource(properties = {
	"app.security.secure-profile.role=API_CLIENT",
	"app.security.secure-profile.admin-role=GOVERNANCE_ADMIN"
})
class SecureProfileSecurityIntegrationTest {

	private static final String SECURE_USERNAME = "secure-user";
	private static final String SECURE_PASSWORD = "change-me-secure";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldRejectApiRequestWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldRejectSwaggerUiWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldRejectActuatorMetricsWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/actuator/metrics"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldAllowApiRequestWithValidBasicAuth() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations/{evaluationId}", UUID.randomUUID())
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD)))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowActuatorHealthWithValidBasicAuth() throws Exception {
		mockMvc.perform(get("/actuator/health")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD)))
			.andExpect(status().isForbidden());
	}

	@Test
	void shouldRejectGovernanceReadWhenSecureUserLacksOpsRole() throws Exception {
		mockMvc.perform(get("/api/admin/rules")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD)))
			.andExpect(status().isForbidden());
	}

	@Test
	void shouldRejectApiRequestWithInvalidCredentials() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations")
				.with(httpBasic(SECURE_USERNAME, "wrong-password")))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldRejectGovernanceMutationWhenSecureUserLacksAdminRole() throws Exception {
		mockMvc.perform(patch("/api/admin/rules/HIGH_AMOUNT/versions/1.0.0/state")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "lifecycleStatus": "ACTIVE",
					  "activationState": "ACTIVE"
					}
					"""))
			.andExpect(status().isForbidden());
	}
}
