package com.oitws.fraudengine.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.oitws.fraudengine.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("secure")
@TestPropertySource(properties = {
	SecureProfileTestCredentials.PRIMARY_USERNAME_PROPERTY,
	SecureProfileTestCredentials.PRIMARY_PASSWORD_PROPERTY,
	"app.security.secure-profile.rotation-phase=CUTOVER",
	SecureProfileTestCredentials.ROTATION_USERNAME_PROPERTY,
	SecureProfileTestCredentials.ROTATION_PASSWORD_PROPERTY,
	"app.security.secure-profile.role=API_CLIENT",
	"app.security.secure-profile.admin-role=GOVERNANCE_ADMIN"
})
class SecureProfileCredentialCutoverIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldAllowApiAuthenticationWithPrimaryCredentialDuringCutoverPhase() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations/{evaluationId}", UUID.randomUUID())
				.with(SecureProfileTestCredentials.primaryBasicAuth()))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowApiAuthenticationWithRotationCredentialDuringCutoverPhase() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations/{evaluationId}", UUID.randomUUID())
				.with(SecureProfileTestCredentials.rotationBasicAuth()))
			.andExpect(status().isNotFound());
	}
}
