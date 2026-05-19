package com.capitec.fraudengine.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
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
@ActiveProfiles("hardened")
@TestPropertySource(properties = {
	"app.security.hardened-profile.jwk-set-uri=https://issuer.example/.well-known/jwks.json"
})
class HardenedProfileSecurityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldRejectApiRequestWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldRejectApiRequestWithBasicAuthentication() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations")
				.with(httpBasic("secure-user", "change-me-secure")))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldRejectSwaggerUiWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldAllowApiRequestWhenJwtAuthenticationIsPresent() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations/{evaluationId}", UUID.randomUUID())
				.with(jwt()))
			.andExpect(status().isNotFound());
	}
}
