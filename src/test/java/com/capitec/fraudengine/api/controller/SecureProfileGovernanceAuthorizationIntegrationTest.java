package com.capitec.fraudengine.api.controller;

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
	SecureProfileTestCredentials.USERNAME_PROPERTY,
	SecureProfileTestCredentials.PASSWORD_PROPERTY,
	"app.security.secure-profile.role=OPS_READER",
	"app.security.secure-profile.ops-reader-role=OPS_READER",
	"app.security.secure-profile.admin-role=GOVERNANCE_ADMIN"
})
class SecureProfileGovernanceAuthorizationIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldAllowGovernanceReadEndpointForOpsReaderUser() throws Exception {
		mockMvc.perform(get("/api/admin/rules")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isOk());
	}

	@Test
	void shouldAllowGovernanceVersionReadEndpointForOpsReaderUser() throws Exception {
		mockMvc.perform(get("/api/admin/rules/DOES_NOT_EXIST/versions")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowGovernanceHistoryReadEndpointForOpsReaderUser() throws Exception {
		mockMvc.perform(get("/api/admin/rules/DOES_NOT_EXIST/versions/1.0.0/history")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowActuatorEndpointForOpsReaderUser() throws Exception {
		mockMvc.perform(get("/actuator/health")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isOk());
	}

	@Test
	void shouldExposeRedactedSecureCredentialDiagnosticsInActuatorInfoForOpsReaderUser() throws Exception {
		mockMvc.perform(get("/actuator/info")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
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
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowFraudEvaluationApiForOpsReaderUser() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isOk());
	}

	@Test
	void shouldRejectGovernanceStateTransitionEndpointForNonAdminUser() throws Exception {
		mockMvc.perform(patch("/api/admin/rules/DOES_NOT_EXIST/versions/1.0.0/state")
				.with(SecureProfileTestCredentials.secureBasicAuth())
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
				.with(SecureProfileTestCredentials.secureBasicAuth())
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
				.with(SecureProfileTestCredentials.secureBasicAuth())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "action": "RETIRE"
					}
					"""))
			.andExpect(status().isForbidden());
	}
}
