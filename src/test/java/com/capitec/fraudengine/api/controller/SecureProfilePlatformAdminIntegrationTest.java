package com.capitec.fraudengine.api.controller;

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

import com.capitec.fraudengine.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("secure")
@TestPropertySource(properties = {
	SecureProfileTestCredentials.USERNAME_PROPERTY,
	SecureProfileTestCredentials.PASSWORD_PROPERTY,
	"app.security.secure-profile.role=PLATFORM_ADMIN",
	"app.security.secure-profile.ops-reader-role=OPS_READER",
	"app.security.secure-profile.admin-role=GOVERNANCE_ADMIN",
	"app.security.secure-profile.platform-admin-role=PLATFORM_ADMIN"
})
class SecureProfilePlatformAdminIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldAllowGovernanceReadEndpointForPlatformAdminUser() throws Exception {
		mockMvc.perform(get("/api/admin/rules")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isOk());
	}

	@Test
	void shouldAllowGovernanceStateTransitionEndpointForPlatformAdminUser() throws Exception {
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
	void shouldAllowGovernanceVersionRegistrationEndpointForPlatformAdminUser() throws Exception {
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
	void shouldAllowActuatorEndpointForPlatformAdminUser() throws Exception {
		mockMvc.perform(get("/actuator/health")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isOk());
	}

	@Test
	void shouldAllowFraudEvaluationApiForPlatformAdminUser() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations")
				.with(SecureProfileTestCredentials.secureBasicAuth()))
			.andExpect(status().isOk());
	}
}
