package com.capitec.fraudengine.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.capitec.fraudengine.TestcontainersConfiguration;
import com.capitec.fraudengine.infrastructure.config.RequestCorrelationFilter;

import io.micrometer.core.instrument.MeterRegistry;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ObservabilityContractIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MeterRegistry meterRegistry;

	@Test
	void shouldEmitEvaluationMetricsWhenEvaluationCompletes() throws Exception {
		double totalBefore = counterValue("fraud.evaluation.completed.total");
		double decisionBefore = counterValue("fraud.evaluation.decision.count", "decision", "BLOCK");
		double highAmountBefore = counterValue(
			"fraud.evaluation.rule.triggered.count",
			"ruleCode",
			"HIGH_AMOUNT",
			"severity",
			"BLOCK"
		);
		long timerCountBefore = timerCount("fraud.evaluation.duration");

		mockMvc.perform(post("/api/fraud-evaluations")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "transactionId": "txn-observability-001",
					  "accountId": "account-observability-001",
					  "customerId": "customer-observability-001",
					  "amount": 30000.00,
					  "currency": "ZAR",
					  "merchantId": "merchant-observability-001",
					  "merchantCategory": "RETAIL",
					  "transactionType": "PURCHASE",
					  "channel": "ONLINE",
					  "eventTimestamp": "2026-05-12T18:00:00+02:00",
					  "location": {
					    "countryCode": "ZA",
					    "city": "Cape Town"
					  },
					  "reference": "observability-metrics-test"
					}
					"""))
			.andExpect(status().isCreated());

		assertThat(counterValue("fraud.evaluation.completed.total")).isEqualTo(totalBefore + 1);
		assertThat(counterValue("fraud.evaluation.decision.count", "decision", "BLOCK")).isEqualTo(decisionBefore + 1);
		assertThat(counterValue(
			"fraud.evaluation.rule.triggered.count",
			"ruleCode",
			"HIGH_AMOUNT",
			"severity",
			"BLOCK"
		)).isEqualTo(highAmountBefore + 1);
		assertThat(timerCount("fraud.evaluation.duration")).isEqualTo(timerCountBefore + 1);
	}

	@Test
	void shouldReuseValidRequestIdFromHeader() throws Exception {
		String requestId = UUID.randomUUID().toString();

		mockMvc.perform(post("/api/fraud-evaluations")
				.header(RequestCorrelationFilter.REQUEST_ID_HEADER, requestId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "transactionId": "txn-observability-002",
					  "accountId": "account-observability-002",
					  "customerId": "customer-observability-002",
					  "amount": 1200.00,
					  "currency": "ZAR",
					  "merchantId": "merchant-observability-002",
					  "merchantCategory": "RETAIL",
					  "transactionType": "PURCHASE",
					  "channel": "ONLINE",
					  "eventTimestamp": "2026-05-12T18:05:00+02:00",
					  "location": {
					    "countryCode": "ZA",
					    "city": "Cape Town"
					  },
					  "reference": "observability-correlation-valid"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(header().string(RequestCorrelationFilter.REQUEST_ID_HEADER, requestId));
	}

	@Test
	void shouldGenerateNewRequestIdWhenHeaderIsInvalid() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/fraud-evaluations")
				.header(RequestCorrelationFilter.REQUEST_ID_HEADER, "invalid-request-id")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "transactionId": "txn-observability-003",
					  "accountId": "account-observability-003",
					  "customerId": "customer-observability-003",
					  "amount": 1500.00,
					  "currency": "ZAR",
					  "merchantId": "merchant-observability-003",
					  "merchantCategory": "RETAIL",
					  "transactionType": "PURCHASE",
					  "channel": "ONLINE",
					  "eventTimestamp": "2026-05-12T18:10:00+02:00",
					  "location": {
					    "countryCode": "ZA",
					    "city": "Cape Town"
					  },
					  "reference": "observability-correlation-invalid"
					}
					"""))
			.andExpect(status().isCreated())
			.andReturn();

		String responseRequestId = result.getResponse().getHeader(RequestCorrelationFilter.REQUEST_ID_HEADER);
		assertThat(responseRequestId).isNotBlank();
		assertThat(responseRequestId).isNotEqualTo("invalid-request-id");
		assertThat(UUID.fromString(responseRequestId)).isNotNull();
	}

	private double counterValue(String meterName, String... tags) {
		var counter = meterRegistry.find(meterName)
			.tags(tags)
			.counter();
		return counter == null ? 0 : counter.count();
	}

	private long timerCount(String meterName) {
		var timer = meterRegistry.find(meterName).timer();
		return timer == null ? 0 : timer.count();
	}
}
