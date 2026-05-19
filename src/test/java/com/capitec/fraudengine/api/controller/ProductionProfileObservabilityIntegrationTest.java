package com.capitec.fraudengine.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.capitec.fraudengine.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("production")
@TestPropertySource(properties = {
	"app.security.hardened-profile.jwk-set-uri=https://issuer.example/.well-known/jwks.json"
})
class ProductionProfileObservabilityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldExposeOnlyHealthEndpointForActuatorInProductionProfile() throws Exception {
		mockMvc.perform(get("/actuator/health"))
			.andExpect(status().isUnauthorized());

		mockMvc.perform(get("/actuator/health")
				.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPS_READER"))))
			.andExpect(status().isOk());

		mockMvc.perform(get("/actuator/metrics")
				.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPS_READER"))))
			.andExpect(status().isNotFound());

		mockMvc.perform(get("/actuator/info")
				.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPS_READER"))))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldDisableSwaggerAndOpenApiInProductionProfile() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
			.andExpect(status().isUnauthorized());

		mockMvc.perform(get("/v3/api-docs"))
			.andExpect(status().isUnauthorized());

		mockMvc.perform(get("/swagger-ui.html")
				.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPS_READER"))))
			.andExpect(status().isNotFound());

		mockMvc.perform(get("/v3/api-docs")
				.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPS_READER"))))
			.andExpect(status().isNotFound());
	}
}
