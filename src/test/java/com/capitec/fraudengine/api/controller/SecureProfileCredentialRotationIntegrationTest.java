package com.capitec.fraudengine.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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

import com.capitec.fraudengine.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("secure")
@TestPropertySource(properties = {
	"app.security.secure-profile.username=secure-user-primary",
	"app.security.secure-profile.password=change-me-secure-primary",
	"app.security.secure-profile.rotation-enabled=true",
	"app.security.secure-profile.rotation-username=secure-user-rotating",
	"app.security.secure-profile.rotation-password=change-me-secure-rotating",
	"app.security.secure-profile.role=API_CLIENT",
	"app.security.secure-profile.admin-role=GOVERNANCE_ADMIN"
})
class SecureProfileCredentialRotationIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldAllowApiAuthenticationWithPrimaryCredentialDuringRotationWindow() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations/{evaluationId}", UUID.randomUUID())
				.with(httpBasic("secure-user-primary", "change-me-secure-primary")))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldAllowApiAuthenticationWithRotationCredentialDuringRotationWindow() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations/{evaluationId}", UUID.randomUUID())
				.with(httpBasic("secure-user-rotating", "change-me-secure-rotating")))
			.andExpect(status().isNotFound());
	}
}
