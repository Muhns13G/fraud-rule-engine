package com.capitec.fraudengine.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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
	"app.security.secure-profile.role=GOVERNANCE_ADMIN",
	"app.security.secure-profile.admin-role=GOVERNANCE_ADMIN"
})
class SecureProfileGovernanceAdminIntegrationTest {

	private static final String SECURE_USERNAME = "secure-user";
	private static final String SECURE_PASSWORD = "change-me-secure";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldAllowGovernanceStateTransitionEndpointForAdminUser() throws Exception {
		mockMvc.perform(patch("/api/admin/rules/DOES_NOT_EXIST/versions/1.0.0/state")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD))
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
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD))
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
}
