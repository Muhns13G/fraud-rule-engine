package com.oitws.fraudengine.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.oitws.fraudengine.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("secure")
@TestPropertySource(properties = {
	SecureProfileTestCredentials.USERNAME_PROPERTY,
	SecureProfileTestCredentials.PASSWORD_PROPERTY,
	"app.security.secure-profile.role=GOVERNANCE_ADMIN",
	"app.security.secure-profile.admin-role=GOVERNANCE_ADMIN"
})
class SecureProfileGovernanceAdminIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldAllowGovernanceStateTransitionEndpointForAdminUser() throws Exception {
		mockMvc.perform(patch("/api/admin/rules/DOES_NOT_EXIST/versions/1.0.0/state")
				.with(SecureProfileTestCredentials.secureBasicAuth())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "lifecycleStatus": "ACTIVE",
					  "activationState": "ACTIVE"
					}
					"""))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowGovernanceVersionRegistrationEndpointForAdminUser() throws Exception {
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
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowGovernanceWorkflowActionEndpointForAdminUser() throws Exception {
		mockMvc.perform(post("/api/admin/rules/DOES_NOT_EXIST/versions/1.0.0/actions")
				.with(SecureProfileTestCredentials.secureBasicAuth())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "action": "RETIRE"
					}
					"""))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowGovernanceReadEndpointForAdminUser() throws Exception {
		mockMvc.perform(get("/api/admin/rules")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isOk());
	}

	@Test
	void shouldAllowGovernanceVersionReadEndpointForAdminUser() throws Exception {
		mockMvc.perform(get("/api/admin/rules/DOES_NOT_EXIST/versions")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowGovernanceHistoryReadEndpointForAdminUser() throws Exception {
		mockMvc.perform(get("/api/admin/rules/DOES_NOT_EXIST/versions/1.0.0/history")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowActuatorEndpointForAdminUser() throws Exception {
		mockMvc.perform(get("/actuator/health")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isOk());
	}

	@Test
	void shouldAllowFraudEvaluationApiForAdminUser() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isOk());
	}
}
