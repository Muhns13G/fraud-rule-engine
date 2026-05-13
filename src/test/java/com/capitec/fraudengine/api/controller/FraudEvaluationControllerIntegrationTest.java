package com.capitec.fraudengine.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import com.capitec.fraudengine.TestcontainersConfiguration;
import com.capitec.fraudengine.domain.model.FraudEvaluation;
import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.TransactionEvent;
import com.capitec.fraudengine.domain.model.TransactionLocation;
import com.capitec.fraudengine.domain.model.enums.FraudDecision;
import com.capitec.fraudengine.domain.model.enums.MerchantCategory;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.model.enums.TransactionChannel;
import com.capitec.fraudengine.domain.model.enums.TransactionType;
import com.capitec.fraudengine.infrastructure.persistence.mapper.FraudEvaluationPersistenceMapper;
import com.capitec.fraudengine.infrastructure.persistence.repository.FraudEvaluationJpaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class FraudEvaluationControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FraudEvaluationJpaRepository fraudEvaluationJpaRepository;

	@Autowired
	private FraudEvaluationPersistenceMapper fraudEvaluationPersistenceMapper;

	@BeforeEach
	void setUp() {
		fraudEvaluationJpaRepository.deleteAll();
	}

	@Test
	void shouldCreateFraudEvaluationViaPostEndpoint() throws Exception {
		mockMvc.perform(post("/api/fraud-evaluations")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "transactionId": "txn-post-001",
					  "accountId": "account-post-001",
					  "customerId": "customer-post-001",
					  "amount": 26000.00,
					  "currency": "ZAR",
					  "merchantId": "merchant-123",
					  "merchantCategory": "RETAIL",
					  "transactionType": "PURCHASE",
					  "channel": "ONLINE",
					  "eventTimestamp": "2026-05-12T10:00:00+02:00",
					  "location": {
					    "countryCode": "ZA",
					    "city": "Cape Town"
					  },
					  "reference": "post-integration-test"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", containsString("/api/fraud-evaluations/")))
			.andExpect(jsonPath("$.transactionId", is("txn-post-001")))
			.andExpect(jsonPath("$.decision", is("BLOCK")))
			.andExpect(jsonPath("$.decisionScore", is(100)))
			.andExpect(jsonPath("$.traceSummary", containsString("HIGH_AMOUNT")))
			.andExpect(jsonPath("$.ruleResults", hasSize(4)));

		assertThatStoredEvaluationCountIs(1);
	}

	@Test
	void shouldRetrieveStoredFraudEvaluationById() throws Exception {
		UUID evaluationId = UUID.randomUUID();
		fraudEvaluationJpaRepository.save(fraudEvaluationPersistenceMapper.toEntity(
			fraudEvaluation(
				evaluationId,
				"txn-get-001",
				"account-get-001",
				FraudDecision.REVIEW,
				40,
				OffsetDateTime.parse("2026-05-12T11:00:00+02:00"),
				OffsetDateTime.parse("2026-05-12T11:01:00+02:00"),
				List.of(ruleResult("RISKY_MERCHANT_CATEGORY", true, RuleSeverity.REVIEW, 40))
			)
		));

		mockMvc.perform(get("/api/fraud-evaluations/{evaluationId}", evaluationId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.evaluationId", is(evaluationId.toString())))
			.andExpect(jsonPath("$.transactionId", is("txn-get-001")))
			.andExpect(jsonPath("$.decision", is("REVIEW")))
			.andExpect(jsonPath("$.decisionScore", is(40)))
			.andExpect(jsonPath("$.ruleResults", hasSize(1)))
			.andExpect(jsonPath("$.ruleResults[0].ruleCode", is("RISKY_MERCHANT_CATEGORY")));
	}

	@Test
	void shouldReturnNotFoundForUnknownEvaluationId() throws Exception {
		mockMvc.perform(get("/api/fraud-evaluations/{evaluationId}", UUID.randomUUID()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status", is(404)))
			.andExpect(jsonPath("$.message", containsString("Fraud evaluation not found")));
	}

	@Test
	void shouldReturnBadRequestForUnsupportedRequestBodyEnumValue() throws Exception {
		mockMvc.perform(post("/api/fraud-evaluations")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "transactionId": "txn-post-unsupported-001",
					  "accountId": "account-post-unsupported-001",
					  "customerId": "customer-post-unsupported-001",
					  "amount": 500.00,
					  "currency": "ZAR",
					  "merchantId": "merchant-123",
					  "merchantCategory": "NOT_A_REAL_CATEGORY",
					  "transactionType": "PURCHASE",
					  "channel": "ONLINE",
					  "eventTimestamp": "2026-05-12T10:00:00+02:00",
					  "location": {
					    "countryCode": "ZA",
					    "city": "Cape Town"
					  },
					  "reference": "post-integration-test-invalid-enum"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status", is(400)))
			.andExpect(jsonPath("$.message", is("Request payload contains an unsupported value.")))
			.andExpect(jsonPath("$.details[0]", containsString("merchantCategory")));
	}

	@Test
	void shouldReturnFilteredEvaluationSummaries() throws Exception {
		fraudEvaluationJpaRepository.save(fraudEvaluationPersistenceMapper.toEntity(
			fraudEvaluation(
				UUID.randomUUID(),
				"txn-list-001",
				"account-list-001",
				FraudDecision.REVIEW,
				40,
				OffsetDateTime.parse("2026-05-12T09:55:00+02:00"),
				OffsetDateTime.parse("2026-05-12T10:00:00+02:00"),
				List.of(ruleResult("UNUSUAL_TIME", true, RuleSeverity.REVIEW, 40))
			)
		));
		fraudEvaluationJpaRepository.save(fraudEvaluationPersistenceMapper.toEntity(
			fraudEvaluation(
				UUID.randomUUID(),
				"txn-list-002",
				"account-list-001",
				FraudDecision.BLOCK,
				100,
				OffsetDateTime.parse("2026-05-12T10:55:00+02:00"),
				OffsetDateTime.parse("2026-05-12T11:00:00+02:00"),
				List.of(ruleResult("HIGH_AMOUNT", true, RuleSeverity.BLOCK, 100))
			)
		));
		fraudEvaluationJpaRepository.save(fraudEvaluationPersistenceMapper.toEntity(
			fraudEvaluation(
				UUID.randomUUID(),
				"txn-list-003",
				"other-account",
				FraudDecision.REVIEW,
				40,
				OffsetDateTime.parse("2026-05-12T09:55:00+02:00"),
				OffsetDateTime.parse("2026-05-12T10:00:00+02:00"),
				List.of(ruleResult("RISKY_MERCHANT_CATEGORY", true, RuleSeverity.REVIEW, 40))
			)
		));

		mockMvc.perform(get("/api/fraud-evaluations")
				.param("decision", "REVIEW")
				.param("accountId", "account-list-001")
				.param("from", "2026-05-12T09:59:00+02:00")
				.param("to", "2026-05-12T10:01:00+02:00"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].transactionId", is("txn-list-001")))
			.andExpect(jsonPath("$[0].accountId", is("account-list-001")))
			.andExpect(jsonPath("$[0].decision", is("REVIEW")));
	}

	@Test
	void shouldExposeOpenApiDocument() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.info.title", is("Fraud Rule Engine API")))
			.andExpect(jsonPath("$.paths['/api/fraud-evaluations']").exists())
			.andExpect(jsonPath("$.paths['/api/fraud-evaluations'].post.summary", is("Evaluate a transaction for fraud")))
			.andExpect(jsonPath("$.paths['/api/fraud-evaluations/{evaluationId}'].get.summary", is("Retrieve a fraud evaluation by id")));
	}

	@Test
	void shouldExposeSwaggerUiEntryPoint() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string("Location", containsString("/swagger-ui/index.html")));
	}

	private void assertThatStoredEvaluationCountIs(int expectedCount) {
		org.assertj.core.api.Assertions.assertThat(fraudEvaluationJpaRepository.count()).isEqualTo(expectedCount);
	}

	private FraudEvaluation fraudEvaluation(
		UUID evaluationId,
		String transactionId,
		String accountId,
		FraudDecision decision,
		int decisionScore,
		OffsetDateTime eventTimestamp,
		OffsetDateTime evaluatedAt,
		List<RuleEvaluationResult> ruleResults
	) {
		TransactionEvent transactionEvent = new TransactionEvent(
			transactionId,
			accountId,
			"customer-" + accountId,
			new BigDecimal("1500.00"),
			"ZAR",
			"merchant-123",
			MerchantCategory.RETAIL,
			TransactionType.PURCHASE,
			TransactionChannel.ONLINE,
			eventTimestamp,
			new TransactionLocation("ZA", "Cape Town"),
			"api-integration-test"
		);

		return new FraudEvaluation(
			evaluationId,
			transactionEvent,
			decision,
			decisionScore,
			evaluatedAt,
			"API integration test trace",
			ruleResults
		);
	}

	private RuleEvaluationResult ruleResult(String ruleCode, boolean triggered, RuleSeverity severity, int scoreContribution) {
		return new RuleEvaluationResult(
			ruleCode,
			ruleCode + " Rule",
			triggered,
			severity,
			scoreContribution,
			"Reason for " + ruleCode
		);
	}
}
