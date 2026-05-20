package com.capitec.fraudengine.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.capitec.fraudengine.TestcontainersConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Import({
	TestcontainersConfiguration.class,
	SecureProfileExternalSecretSourceIntegrationTest.ObjectMapperTestConfiguration.class
})
@ActiveProfiles("secure")
@TestPropertySource(properties = {
	"app.security.secure-profile.identity-provider=IN_MEMORY",
	"app.security.secure-profile.secret-source=EXTERNAL_MANAGER",
	"app.security.secure-profile.external-manager-adapter=ENV",
	"app.security.secure-profile.external-secret-ref=env:FRAUD_ENGINE_SECURE_SECRET_PAYLOAD",
	"app.security.secure-profile.password=",
	"app.security.secure-profile.password-encoded=",
	"FRAUD_ENGINE_SECURE_SECRET_PAYLOAD={\"username\":\"secure-external-user\",\"password\":\"change-me-external\"}",
	"app.security.secure-profile.role=API_CLIENT",
	"app.security.secure-profile.admin-role=GOVERNANCE_ADMIN"
})
class SecureProfileExternalSecretSourceIntegrationTest {

	@TestConfiguration(proxyBeanMethods = false)
	static class ObjectMapperTestConfiguration {
		@Bean
		ObjectMapper objectMapper() {
			return new ObjectMapper();
		}
	}

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldAuthenticateUsingCredentialsResolvedFromExternalSecretSource() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations/{evaluationId}", UUID.randomUUID())
				.with(httpBasic("secure-external-user", "change-me-external")))
			.andExpect(status().isNotFound());
	}
}
