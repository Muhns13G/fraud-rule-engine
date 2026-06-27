package com.oitws.fraudengine.api.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.oitws.fraudengine.TestcontainersConfiguration;
import com.oitws.fraudengine.infrastructure.persistence.repository.FraudEvaluationJpaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class PerformanceReliabilitySmokeIntegrationTest {

	private static final int SAMPLE_COUNT = 10;
	private static final long EVALUATION_P95_THRESHOLD_MS = 1500;
	private static final long RETRIEVAL_P95_THRESHOLD_MS = 800;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FraudEvaluationJpaRepository fraudEvaluationJpaRepository;

	@BeforeEach
	void setUp() {
		fraudEvaluationJpaRepository.deleteAll();
	}

	@Test
	void shouldMeetEvaluationAndRetrievalLatencySmokeThresholds() throws Exception {
		List<Long> evaluationDurationsMs = new ArrayList<>();
		for (int i = 0; i < SAMPLE_COUNT; i++) {
			long startedAt = System.nanoTime();
			mockMvc.perform(post("/api/fraud-evaluations")
					.contentType(MediaType.APPLICATION_JSON)
					.content(evaluationPayload(i)))
				.andExpect(status().isCreated());
			evaluationDurationsMs.add(toMillis(System.nanoTime() - startedAt));
		}

		List<Long> retrievalDurationsMs = new ArrayList<>();
		for (int i = 0; i < SAMPLE_COUNT; i++) {
			long startedAt = System.nanoTime();
			mockMvc.perform(get("/api/fraud-evaluations")
					.param("accountId", "account-smoke-" + i)
					.param("size", "5"))
				.andExpect(status().isOk());
			retrievalDurationsMs.add(toMillis(System.nanoTime() - startedAt));
		}

		long evaluationP95 = p95(evaluationDurationsMs);
		long retrievalP95 = p95(retrievalDurationsMs);

		assertTrue(
			evaluationP95 <= EVALUATION_P95_THRESHOLD_MS,
			"Evaluation p95 latency " + evaluationP95 + "ms exceeded threshold " + EVALUATION_P95_THRESHOLD_MS + "ms"
		);
		assertTrue(
			retrievalP95 <= RETRIEVAL_P95_THRESHOLD_MS,
			"Retrieval p95 latency " + retrievalP95 + "ms exceeded threshold " + RETRIEVAL_P95_THRESHOLD_MS + "ms"
		);
	}

	private String evaluationPayload(int index) {
		return """
			{
			  "transactionId": "txn-smoke-%d",
			  "accountId": "account-smoke-%d",
			  "customerId": "customer-smoke-%d",
			  "amount": 1200.00,
			  "currency": "ZAR",
			  "merchantId": "merchant-smoke-%d",
			  "merchantCategory": "RETAIL",
			  "transactionType": "PURCHASE",
			  "channel": "ONLINE",
			  "eventTimestamp": "2026-05-12T10:%02d:00+02:00",
			  "location": {
			    "countryCode": "ZA",
			    "city": "Cape Town"
			  },
			  "reference": "perf-smoke-%d"
			}
			""".formatted(index, index, index, index, index % 60, index);
	}

	private long toMillis(long durationNanos) {
		return durationNanos / 1_000_000;
	}

	private long p95(List<Long> values) {
		List<Long> sorted = new ArrayList<>(values);
		Collections.sort(sorted);
		int index = (int) Math.ceil(sorted.size() * 0.95d) - 1;
		return sorted.get(Math.max(index, 0));
	}
}
