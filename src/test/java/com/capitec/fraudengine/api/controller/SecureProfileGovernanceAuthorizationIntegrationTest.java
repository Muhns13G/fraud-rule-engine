package com.capitec.fraudengine.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
	"app.security.secure-profile.username=secure-user",
	"app.security.secure-profile.password=change-me-secure",
	"app.security.secure-profile.role=OPS_READER",
	"app.security.secure-profile.ops-reader-role=OPS_READER",
	"app.security.secure-profile.admin-role=GOVERNANCE_ADMIN"
})
class SecureProfileGovernanceAuthorizationIntegrationTest {

	private static final String SECURE_USERNAME = "secure-user";
	private static final String SECURE_PASSWORD = "change-me-secure";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldAllowGovernanceReadEndpointForOpsReaderUser() throws Exception {
		mockMvc.perform(get("/api/admin/rules")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD)))
			.andExpect(status().isOk());
	}

	@Test
	void shouldAllowGovernanceVersionReadEndpointForOpsReaderUser() throws Exception {
		mockMvc.perform(get("/api/admin/rules/DOES_NOT_EXIST/versions")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD)))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowGovernanceHistoryReadEndpointForOpsReaderUser() throws Exception {
		mockMvc.perform(get("/api/admin/rules/DOES_NOT_EXIST/versions/1.0.0/history")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD)))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowActuatorEndpointForOpsReaderUser() throws Exception {
		mockMvc.perform(get("/actuator/health")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD)))
			.andExpect(status().isOk());
	}

	@Test
	void shouldExposeRedactedSecureCredentialDiagnosticsInActuatorInfoForOpsReaderUser() throws Exception {
		mockMvc.perform(get("/actuator/info")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.secureCredentialDiagnostics.activeProfile").value("secure"))
			.andExpect(jsonPath("$.secureCredentialDiagnostics.identityProvider").value("IN_MEMORY"))
			.andExpect(jsonPath("$.secureCredentialDiagnostics.secretSource").value("ENV"))
			.andExpect(jsonPath("$.secureCredentialDiagnostics.rotationPhase").exists())
			.andExpect(jsonPath("$.secureCredentialDiagnostics.password").doesNotExist())
			.andExpect(jsonPath("$.secureCredentialDiagnostics.passwordEncoded").doesNotExist())
			.andExpect(jsonPath("$.secureCredentialDiagnostics.username").doesNotExist())
			.andExpect(jsonPath("$.secureCredentialDiagnostics.externalSecretRef").doesNotExist());
	}

	@Test
	void shouldReturnNotFoundForActuatorMetricsWhenSecureExposureDoesNotIncludeMetrics() throws Exception {
		mockMvc.perform(get("/actuator/metrics")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD)))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowFraudEvaluationApiForOpsReaderUser() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD)))
			.andExpect(status().isOk());
	}

	@Test
	void shouldRejectGovernanceStateTransitionEndpointForNonAdminUser() throws Exception {
		mockMvc.perform(patch("/api/admin/rules/DOES_NOT_EXIST/versions/1.0.0/state")
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

	@Test
	void shouldRejectGovernanceVersionRegistrationEndpointForNonAdminUser() throws Exception {
		mockMvc.perform(post("/api/admin/rules/DOES_NOT_EXIST/versions")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "version": "2.0.0",
					  "lifecycleStatus": "DRAFT",
					  "activationState": "INACTIVE"
					}
					"""))
			.andExpect(status().isForbidden());
	}

	@Test
	void shouldRejectGovernanceWorkflowActionEndpointForNonAdminUser() throws Exception {
		mockMvc.perform(post("/api/admin/rules/DOES_NOT_EXIST/versions/1.0.0/actions")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "action": "RETIRE"
					}
					"""))
			.andExpect(status().isForbidden());
	}
}
