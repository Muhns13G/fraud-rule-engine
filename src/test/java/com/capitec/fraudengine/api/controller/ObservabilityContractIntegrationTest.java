package com.capitec.fraudengine.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import com.capitec.fraudengine.domain.model.RuleGovernanceMetadata;
import com.capitec.fraudengine.domain.model.RuleIdentity;
import com.capitec.fraudengine.domain.model.RuleLifecycleState;
import com.capitec.fraudengine.domain.model.enums.RuleActivationState;
import com.capitec.fraudengine.domain.model.enums.RuleExecutionSource;
import com.capitec.fraudengine.domain.model.enums.RuleLifecycleStatus;
import com.capitec.fraudengine.infrastructure.config.RequestCorrelationFilter;
import com.capitec.fraudengine.infrastructure.persistence.mapper.RuleGovernanceMetadataPersistenceMapper;
import com.capitec.fraudengine.infrastructure.persistence.repository.RuleGovernanceMetadataJpaRepository;

import io.micrometer.core.instrument.MeterRegistry;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ObservabilityContractIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MeterRegistry meterRegistry;

	@Autowired
	private RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository;

	@Autowired
	private RuleGovernanceMetadataPersistenceMapper ruleGovernanceMetadataPersistenceMapper;

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
		String requestId = UUID.randomUUID().toString().toUpperCase();

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
			.andExpect(header().string(RequestCorrelationFilter.REQUEST_ID_HEADER, requestId.toLowerCase()));
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

	@Test
	void shouldGenerateNewRequestIdWhenIncomingHeaderIsOverlyLong() throws Exception {
		String overlyLongHeader = "12345678-1234-1234-8234-123456789012-extra-padding-invalid";

		MvcResult result = mockMvc.perform(post("/api/fraud-evaluations")
				.header(RequestCorrelationFilter.REQUEST_ID_HEADER, overlyLongHeader)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "transactionId": "txn-observability-004",
					  "accountId": "account-observability-004",
					  "customerId": "customer-observability-004",
					  "amount": 1600.00,
					  "currency": "ZAR",
					  "merchantId": "merchant-observability-004",
					  "merchantCategory": "RETAIL",
					  "transactionType": "PURCHASE",
					  "channel": "ONLINE",
					  "eventTimestamp": "2026-05-12T18:15:00+02:00",
					  "location": {
					    "countryCode": "ZA",
					    "city": "Cape Town"
					  },
					  "reference": "observability-correlation-overlong"
					}
					"""))
			.andExpect(status().isCreated())
			.andReturn();

		String responseRequestId = result.getResponse().getHeader(RequestCorrelationFilter.REQUEST_ID_HEADER);
		assertThat(responseRequestId).isNotBlank();
		assertThat(responseRequestId).isNotEqualTo(overlyLongHeader);
		assertThat(UUID.fromString(responseRequestId)).isNotNull();
	}

	@Test
	void shouldPropagateRequestIdOnGovernanceErrorPath() throws Exception {
		String requestId = UUID.randomUUID().toString();

		mockMvc.perform(patch("/api/admin/rules/DOES_NOT_EXIST/versions/1.0.0/state")
				.header(RequestCorrelationFilter.REQUEST_ID_HEADER, requestId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "lifecycleStatus": "ACTIVE",
					  "activationState": "ACTIVE"
					}
					"""))
			.andExpect(status().isNotFound())
			.andExpect(header().string(RequestCorrelationFilter.REQUEST_ID_HEADER, requestId));
	}

	@Test
	void shouldEmitRetrievalAndGovernanceMetrics() throws Exception {
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				new RuleGovernanceMetadata(
					new RuleIdentity("OBSERVABILITY_RULE", "1.0.0"),
					"Observability Rule",
					new RuleLifecycleState(RuleLifecycleStatus.ACTIVE, RuleActivationState.ACTIVE),
					RuleExecutionSource.CODE_DEFINED
				)
			)
		);

		double fraudSummaryBefore = counterValue(
			"fraud.retrieval.request.total",
			"resource",
			"fraud_evaluation",
			"operation",
			"find_summaries",
			"outcome",
			"success"
		);
		double governanceListBefore = counterValue(
			"fraud.retrieval.request.total",
			"resource",
			"rule_governance",
			"operation",
			"find_rules",
			"outcome",
			"success"
		);
		double governanceMutationBefore = counterValue(
			"fraud.governance.mutation.total",
			"operation",
			"register_version",
			"outcome",
			"success"
		);
		double lifecycleTransitionBefore = counterValue(
			"fraud.governance.lifecycle.transition.total",
			"ruleCode",
			"OBSERVABILITY_RULE",
			"fromLifecycle",
			"ACTIVE",
			"toLifecycle",
			"DEPRECATED",
			"fromActivation",
			"ACTIVE",
			"toActivation",
			"INACTIVE"
		);
		double versionRegistrationBefore = counterValue(
			"fraud.governance.version.registration.total",
			"ruleCode",
			"OBSERVABILITY_RULE",
			"lifecycleStatus",
			"DEPRECATED",
			"activationState",
			"INACTIVE"
		);

		mockMvc.perform(get("/api/fraud-evaluations"))
			.andExpect(status().isOk());

		mockMvc.perform(get("/api/admin/rules")
				.param("activeOnly", "false"))
			.andExpect(status().isOk());

		mockMvc.perform(post("/api/admin/rules/OBSERVABILITY_RULE/versions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "version": "1.1.0",
					  "lifecycleStatus": "DEPRECATED",
					  "activationState": "INACTIVE"
					}
					"""))
			.andExpect(status().isOk());

		mockMvc.perform(patch("/api/admin/rules/OBSERVABILITY_RULE/versions/1.0.0/state")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "lifecycleStatus": "DEPRECATED",
					  "activationState": "INACTIVE"
					}
					"""))
			.andExpect(status().isOk());

		assertThat(counterValue(
			"fraud.retrieval.request.total",
			"resource",
			"fraud_evaluation",
			"operation",
			"find_summaries",
			"outcome",
			"success"
		)).isEqualTo(fraudSummaryBefore + 1);
		assertThat(counterValue(
			"fraud.retrieval.request.total",
			"resource",
			"rule_governance",
			"operation",
			"find_rules",
			"outcome",
			"success"
		)).isEqualTo(governanceListBefore + 1);
		assertThat(counterValue(
			"fraud.governance.mutation.total",
			"operation",
			"register_version",
			"outcome",
			"success"
		)).isEqualTo(governanceMutationBefore + 1);
		assertThat(counterValue(
			"fraud.governance.lifecycle.transition.total",
			"ruleCode",
			"OBSERVABILITY_RULE",
			"fromLifecycle",
			"ACTIVE",
			"toLifecycle",
			"DEPRECATED",
			"fromActivation",
			"ACTIVE",
			"toActivation",
			"INACTIVE"
		)).isEqualTo(lifecycleTransitionBefore + 1);
		assertThat(counterValue(
			"fraud.governance.version.registration.total",
			"ruleCode",
			"OBSERVABILITY_RULE",
			"lifecycleStatus",
			"DEPRECATED",
			"activationState",
			"INACTIVE"
		)).isEqualTo(versionRegistrationBefore + 1);
	}

	@Test
	void shouldEmitErrorMetricForNotFoundPath() throws Exception {
		double notFoundBefore = counterValue(
			"fraud.api.error.total",
			"status",
			"404",
			"exception",
			"RuleGovernanceMetadataNotFoundException"
		);

		mockMvc.perform(get("/api/admin/rules/DOES_NOT_EXIST/versions/9.9.9"))
			.andExpect(status().isNotFound());

		assertThat(counterValue(
			"fraud.api.error.total",
			"status",
			"404",
			"exception",
			"RuleGovernanceMetadataNotFoundException"
		)).isEqualTo(notFoundBefore + 1);
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
