package com.capitec.fraudengine.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.capitec.fraudengine.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("secure")
@TestPropertySource(properties = {
	"app.security.secure-profile.identity-provider=JDBC",
	"app.security.secure-profile.role=API_CLIENT",
	"app.security.secure-profile.ops-reader-role=OPS_READER",
	"app.security.secure-profile.admin-role=GOVERNANCE_ADMIN",
	"app.security.secure-profile.platform-admin-role=PLATFORM_ADMIN"
})
class SecureProfileJdbcIdentityProviderIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void shouldBootSecureProfileWithJdbcUserDetailsService() {
		UserDetailsService userDetailsService = applicationContext.getBean(UserDetailsService.class);
		assertThat(userDetailsService).isInstanceOf(JdbcUserDetailsManager.class);
	}

	@Test
	void shouldKeepSecureEndpointsProtectedWhenUsingJdbcIdentityProvider() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations"))
			.andExpect(status().isUnauthorized());
	}
}
