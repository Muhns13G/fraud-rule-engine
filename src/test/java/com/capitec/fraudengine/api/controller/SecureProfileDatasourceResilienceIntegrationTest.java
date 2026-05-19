package com.capitec.fraudengine.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.capitec.fraudengine.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("secure")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {
	"app.security.secure-profile.username=secure-user",
	"app.security.secure-profile.password=change-me-secure",
	"app.security.secure-profile.role=OPS_READER",
	"app.security.secure-profile.ops-reader-role=OPS_READER",
	"app.security.secure-profile.admin-role=GOVERNANCE_ADMIN"
})
class SecureProfileDatasourceResilienceIntegrationTest {

	private static final String SECURE_USERNAME = "secure-user";
	private static final String SECURE_PASSWORD = "change-me-secure";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private HikariDataSource dataSource;

	@Test
	void shouldReportHealthDownWhenDatasourceBecomesUnavailable() throws Exception {
		dataSource.close();

		MvcResult result = mockMvc.perform(get("/actuator/health")
				.with(httpBasic(SECURE_USERNAME, SECURE_PASSWORD)))
			.andExpect(status().isServiceUnavailable())
			.andReturn();

		String responseBody = result.getResponse().getContentAsString();
		assertThat(responseBody).contains("\"status\":\"DOWN\"");
	}
}
