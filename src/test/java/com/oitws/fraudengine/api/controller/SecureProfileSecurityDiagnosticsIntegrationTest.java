package com.oitws.fraudengine.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.oitws.fraudengine.TestcontainersConfiguration;

import io.micrometer.core.instrument.MeterRegistry;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("secure")
@TestPropertySource(properties = {
	SecureProfileTestCredentials.USERNAME_PROPERTY,
	SecureProfileTestCredentials.PASSWORD_PROPERTY,
	"app.security.secure-profile.role=API_CLIENT",
	"app.security.secure-profile.admin-role=GOVERNANCE_ADMIN"
})
@ExtendWith(OutputCaptureExtension.class)
class SecureProfileSecurityDiagnosticsIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MeterRegistry meterRegistry;

	@Test
	void shouldEmitUnauthorizedDiagnosticsMetricAndLog(CapturedOutput output) throws Exception {
		double before = counterValue(
			"fraud.security.authn.denied.total",
			"outcome",
			"unauthorized",
			"method",
			"GET"
		);

		mockMvc.perform(get("/api/fraud-evaluations"))
			.andExpect(status().isUnauthorized());

		assertThat(counterValue(
			"fraud.security.authn.denied.total",
			"outcome",
			"unauthorized",
			"method",
			"GET"
		)).isEqualTo(before + 1);
		assertThat(output.getOut()).contains("security_authn_denied");
	}

	@Test
	void shouldEmitAccessDeniedDiagnosticsMetricAndLog(CapturedOutput output) throws Exception {
		double before = counterValue(
			"fraud.security.authz.denied.total",
			"outcome",
			"access_denied",
			"method",
			"PATCH"
		);

		mockMvc.perform(patch("/api/admin/rules/HIGH_AMOUNT/versions/1.0.0/state")
				.with(SecureProfileTestCredentials.secureBasicAuth())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "lifecycleStatus": "ACTIVE",
					  "activationState": "ACTIVE"
					}
					"""))
			.andExpect(status().isForbidden());

		assertThat(counterValue(
			"fraud.security.authz.denied.total",
			"outcome",
			"access_denied",
			"method",
			"PATCH"
		)).isEqualTo(before + 1);
		assertThat(output.getOut()).contains("security_authz_denied");
	}

	private double counterValue(String name, String... tags) {
		if (meterRegistry.find(name).tags(tags).counter() == null) {
			return 0.0d;
		}
		return meterRegistry.find(name).tags(tags).counter().count();
	}
}
