package com.oitws.fraudengine.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.oitws.fraudengine.TestcontainersConfiguration;
import com.oitws.fraudengine.domain.model.RuleGovernanceMetadata;
import com.oitws.fraudengine.domain.model.RuleIdentity;
import com.oitws.fraudengine.domain.model.RuleLifecycleState;
import com.oitws.fraudengine.domain.model.enums.RuleActivationState;
import com.oitws.fraudengine.domain.model.enums.RuleExecutionSource;
import com.oitws.fraudengine.domain.model.enums.RuleLifecycleStatus;
import com.oitws.fraudengine.infrastructure.persistence.mapper.RuleGovernanceMetadataPersistenceMapper;
import com.oitws.fraudengine.infrastructure.persistence.repository.RuleGovernanceHistoryJpaRepository;
import com.oitws.fraudengine.infrastructure.persistence.repository.RuleGovernanceMetadataJpaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class RuleGovernanceControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository;

	@Autowired
	private RuleGovernanceMetadataPersistenceMapper ruleGovernanceMetadataPersistenceMapper;

	@Autowired
	private RuleGovernanceHistoryJpaRepository ruleGovernanceHistoryJpaRepository;

	@BeforeEach
	void setUp() {
		ruleGovernanceMetadataJpaRepository.deleteAll();
		ruleGovernanceHistoryJpaRepository.deleteAll();
	}

	@Test
	void shouldListOnlyActiveRulesByDefault() throws Exception {
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("ACTIVE_RULE", "1.0.0", "Active Rule", RuleLifecycleStatus.ACTIVE, RuleActivationState.ACTIVE)
			)
		);
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("INACTIVE_RULE", "1.0.0", "Inactive Rule", RuleLifecycleStatus.DRAFT, RuleActivationState.INACTIVE)
			)
		);

		mockMvc.perform(get("/api/admin/rules"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].ruleCode", is("ACTIVE_RULE")))
			.andExpect(jsonPath("$.content[0].activationState", is("ACTIVE")))
			.andExpect(jsonPath("$.page", is(0)))
			.andExpect(jsonPath("$.size", is(20)))
			.andExpect(jsonPath("$.totalElements", is(1)))
			.andExpect(jsonPath("$.totalPages", is(1)));
	}

	@Test
	void shouldListAllRulesWhenActiveOnlyIsFalse() throws Exception {
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("RULE_A", "1.0.0", "Rule A", RuleLifecycleStatus.ACTIVE, RuleActivationState.ACTIVE)
			)
		);
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("RULE_B", "1.0.0", "Rule B", RuleLifecycleStatus.DEPRECATED, RuleActivationState.INACTIVE)
			)
		);

		mockMvc.perform(get("/api/admin/rules")
				.param("activeOnly", "false"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.totalElements", is(2)));
	}

	@Test
	void shouldListGovernedVersionsForOneRuleCodeWithPagination() throws Exception {
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("HIGH_AMOUNT", "1.0.0", "High Amount Rule", RuleLifecycleStatus.ACTIVE, RuleActivationState.ACTIVE)
			)
		);
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("HIGH_AMOUNT", "1.1.0", "High Amount Rule", RuleLifecycleStatus.DEPRECATED, RuleActivationState.INACTIVE)
			)
		);

		mockMvc.perform(get("/api/admin/rules/HIGH_AMOUNT/versions")
				.param("page", "0")
				.param("size", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].ruleCode", is("HIGH_AMOUNT")))
			.andExpect(jsonPath("$.page", is(0)))
			.andExpect(jsonPath("$.size", is(1)))
			.andExpect(jsonPath("$.totalElements", is(2)))
			.andExpect(jsonPath("$.totalPages", is(2)));
	}

	@Test
	void shouldRetrieveRuleByCodeAndVersion() throws Exception {
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("HIGH_AMOUNT", "1.0.0", "High Amount Rule", RuleLifecycleStatus.ACTIVE, RuleActivationState.ACTIVE)
			)
		);

		mockMvc.perform(get("/api/admin/rules/HIGH_AMOUNT/versions/1.0.0"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.ruleCode", is("HIGH_AMOUNT")))
			.andExpect(jsonPath("$.version", is("1.0.0")))
			.andExpect(jsonPath("$.ruleName", is("High Amount Rule")))
			.andExpect(jsonPath("$.lifecycleStatus", is("ACTIVE")))
			.andExpect(jsonPath("$.activationState", is("ACTIVE")))
			.andExpect(jsonPath("$.executionSource", is("CODE_DEFINED")))
			.andExpect(jsonPath("$.activeConfiguration", is("reviewThreshold=10000.00, blockThreshold=25000.00")));
	}

	@Test
	void shouldReturnNotFoundWhenRuleMetadataDoesNotExist() throws Exception {
		mockMvc.perform(get("/api/admin/rules/DOES_NOT_EXIST/versions/9.9.9"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status", is(404)))
			.andExpect(jsonPath("$.message", is("Rule governance metadata not found for ruleCode 'DOES_NOT_EXIST' and version '9.9.9'.")));
	}

	@Test
	void shouldTransitionRuleLifecycleStateWhenTransitionIsValid() throws Exception {
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("HIGH_AMOUNT", "1.0.0", "High Amount Rule", RuleLifecycleStatus.ACTIVE, RuleActivationState.ACTIVE)
			)
		);

		mockMvc.perform(
			patch("/api/admin/rules/HIGH_AMOUNT/versions/1.0.0/state")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "lifecycleStatus": "DEPRECATED",
					  "activationState": "INACTIVE"
					}
					""")
		)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.ruleCode", is("HIGH_AMOUNT")))
			.andExpect(jsonPath("$.version", is("1.0.0")))
			.andExpect(jsonPath("$.lifecycleStatus", is("DEPRECATED")))
			.andExpect(jsonPath("$.activationState", is("INACTIVE")))
			.andExpect(jsonPath("$.executionSource", is("CODE_DEFINED")));

		var history = ruleGovernanceHistoryJpaRepository
			.findByRuleCodeAndRuleVersionOrderByCreatedAtAscGovernanceHistoryIdAsc("HIGH_AMOUNT", "1.0.0");
		org.assertj.core.api.Assertions.assertThat(history).hasSize(1);
		org.assertj.core.api.Assertions.assertThat(history.getFirst().getActionType()).isEqualTo("STATE_TRANSITION");
		org.assertj.core.api.Assertions.assertThat(history.getFirst().getActor()).isNotBlank();
		org.assertj.core.api.Assertions.assertThat(history.getFirst().getRequestId()).isNotBlank();
		org.assertj.core.api.Assertions.assertThat(history.getFirst().getFromLifecycleStatus().name()).isEqualTo("ACTIVE");
		org.assertj.core.api.Assertions.assertThat(history.getFirst().getToLifecycleStatus().name()).isEqualTo("DEPRECATED");
	}

	@Test
	void shouldRejectRuleLifecycleStateTransitionWhenPolicyIsViolated() throws Exception {
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("HIGH_AMOUNT", "1.0.0", "High Amount Rule", RuleLifecycleStatus.ACTIVE, RuleActivationState.ACTIVE)
			)
		);

		mockMvc.perform(
			patch("/api/admin/rules/HIGH_AMOUNT/versions/1.0.0/state")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "lifecycleStatus": "DRAFT",
					  "activationState": "INACTIVE"
					}
					""")
		)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status", is(400)))
			.andExpect(jsonPath("$.message", is("Rule governance state transition is invalid.")))
			.andExpect(jsonPath("$.details[0]", is("Lifecycle transition from ACTIVE to DRAFT is not permitted.")));
	}

	@Test
	void shouldReturnNotFoundWhenTransitionTargetDoesNotExist() throws Exception {
		mockMvc.perform(
			patch("/api/admin/rules/DOES_NOT_EXIST/versions/1.0.0/state")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "lifecycleStatus": "DEPRECATED",
					  "activationState": "INACTIVE"
					}
					""")
		)
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status", is(404)))
			.andExpect(jsonPath("$.message", is("Rule governance metadata not found for ruleCode 'DOES_NOT_EXIST' and version '1.0.0'.")));
	}

	@Test
	void shouldRegisterNewRuleVersionForExistingRuleCode() throws Exception {
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("HIGH_AMOUNT", "1.0.0", "High Amount Rule", RuleLifecycleStatus.ACTIVE, RuleActivationState.ACTIVE)
			)
		);

		mockMvc.perform(
			post("/api/admin/rules/HIGH_AMOUNT/versions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "version": "1.1.0",
					  "lifecycleStatus": "DEPRECATED",
					  "activationState": "INACTIVE"
					}
					""")
		)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.ruleCode", is("HIGH_AMOUNT")))
			.andExpect(jsonPath("$.version", is("1.1.0")))
			.andExpect(jsonPath("$.ruleName", is("High Amount Rule")))
			.andExpect(jsonPath("$.lifecycleStatus", is("DEPRECATED")))
			.andExpect(jsonPath("$.activationState", is("INACTIVE")))
			.andExpect(jsonPath("$.executionSource", is("CODE_DEFINED")));

		var history = ruleGovernanceHistoryJpaRepository
			.findByRuleCodeAndRuleVersionOrderByCreatedAtAscGovernanceHistoryIdAsc("HIGH_AMOUNT", "1.1.0");
		org.assertj.core.api.Assertions.assertThat(history).hasSize(1);
		org.assertj.core.api.Assertions.assertThat(history.getFirst().getActionType()).isEqualTo("VERSION_REGISTERED");
		org.assertj.core.api.Assertions.assertThat(history.getFirst().getFromLifecycleStatus()).isNull();
		org.assertj.core.api.Assertions.assertThat(history.getFirst().getToLifecycleStatus().name()).isEqualTo("DEPRECATED");
	}

	@Test
	void shouldRejectDuplicateRuleVersionRegistration() throws Exception {
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("HIGH_AMOUNT", "1.0.0", "High Amount Rule", RuleLifecycleStatus.ACTIVE, RuleActivationState.ACTIVE)
			)
		);

		mockMvc.perform(
			post("/api/admin/rules/HIGH_AMOUNT/versions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "version": "1.0.0",
					  "lifecycleStatus": "DEPRECATED",
					  "activationState": "INACTIVE"
					}
					""")
		)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status", is(400)))
			.andExpect(jsonPath("$.message", is("Rule governance state transition is invalid.")))
			.andExpect(jsonPath("$.details[0]", is("Rule governance version '1.0.0' already exists for ruleCode 'HIGH_AMOUNT'.")));
	}

	@Test
	void shouldReturnNotFoundWhenRegisteringVersionForUnknownRuleCode() throws Exception {
		mockMvc.perform(
			post("/api/admin/rules/UNKNOWN_RULE/versions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "version": "1.0.0",
					  "lifecycleStatus": "ACTIVE",
					  "activationState": "ACTIVE"
					}
					""")
		)
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status", is(404)))
			.andExpect(jsonPath("$.message", is("Rule governance metadata not found for ruleCode 'UNKNOWN_RULE'.")));
	}

	@Test
	void shouldReflectGovernanceMutationsInSubsequentRetrievals() throws Exception {
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("HIGH_AMOUNT", "1.0.0", "High Amount Rule", RuleLifecycleStatus.ACTIVE, RuleActivationState.ACTIVE)
			)
		);

		mockMvc.perform(
			patch("/api/admin/rules/HIGH_AMOUNT/versions/1.0.0/state")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "lifecycleStatus": "DEPRECATED",
					  "activationState": "INACTIVE"
					}
					""")
		)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lifecycleStatus", is("DEPRECATED")))
			.andExpect(jsonPath("$.activationState", is("INACTIVE")));

		mockMvc.perform(
			post("/api/admin/rules/HIGH_AMOUNT/versions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "version": "1.1.0",
					  "lifecycleStatus": "ACTIVE",
					  "activationState": "ACTIVE"
					}
					""")
		)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.version", is("1.1.0")));

		mockMvc.perform(get("/api/admin/rules/HIGH_AMOUNT/versions/1.0.0"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lifecycleStatus", is("DEPRECATED")))
			.andExpect(jsonPath("$.activationState", is("INACTIVE")));

		mockMvc.perform(get("/api/admin/rules/HIGH_AMOUNT/versions/1.1.0"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lifecycleStatus", is("ACTIVE")))
			.andExpect(jsonPath("$.activationState", is("ACTIVE")));

		mockMvc.perform(get("/api/admin/rules").param("activeOnly", "false"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(2)));
	}

	@Test
	void shouldApplyPromoteWorkflowActionWhenTransitionIsValid() throws Exception {
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("WORKFLOW_RULE", "1.0.0", "Workflow Rule", RuleLifecycleStatus.DRAFT, RuleActivationState.INACTIVE)
			)
		);

		mockMvc.perform(
			post("/api/admin/rules/WORKFLOW_RULE/versions/1.0.0/actions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "action": "PROMOTE"
					}
					""")
		)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.ruleCode", is("WORKFLOW_RULE")))
			.andExpect(jsonPath("$.version", is("1.0.0")))
			.andExpect(jsonPath("$.lifecycleStatus", is("ACTIVE")))
			.andExpect(jsonPath("$.activationState", is("ACTIVE")));

		var history = ruleGovernanceHistoryJpaRepository
			.findByRuleCodeAndRuleVersionOrderByCreatedAtAscGovernanceHistoryIdAsc("WORKFLOW_RULE", "1.0.0");
		org.assertj.core.api.Assertions.assertThat(history).hasSize(1);
		org.assertj.core.api.Assertions.assertThat(history.getFirst().getActionType()).isEqualTo("WORKFLOW_PROMOTE");
		org.assertj.core.api.Assertions.assertThat(history.getFirst().getFromLifecycleStatus().name()).isEqualTo("DRAFT");
		org.assertj.core.api.Assertions.assertThat(history.getFirst().getToLifecycleStatus().name()).isEqualTo("ACTIVE");
	}

	@Test
	void shouldReturnPagedLifecycleHistoryForGovernedRuleIdentity() throws Exception {
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("HISTORY_RULE", "1.0.0", "History Rule", RuleLifecycleStatus.DRAFT, RuleActivationState.INACTIVE)
			)
		);

		mockMvc.perform(
			post("/api/admin/rules/HISTORY_RULE/versions/1.0.0/actions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "action": "PROMOTE"
					}
					""")
		)
			.andExpect(status().isOk());

		mockMvc.perform(get("/api/admin/rules/HISTORY_RULE/versions/1.0.0/history")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].ruleCode", is("HISTORY_RULE")))
			.andExpect(jsonPath("$.content[0].version", is("1.0.0")))
			.andExpect(jsonPath("$.content[0].actionType", is("WORKFLOW_PROMOTE")))
			.andExpect(jsonPath("$.content[0].actor").isNotEmpty())
			.andExpect(jsonPath("$.content[0].requestId").isNotEmpty())
			.andExpect(jsonPath("$.content[0].fromLifecycleStatus", is("DRAFT")))
			.andExpect(jsonPath("$.content[0].toLifecycleStatus", is("ACTIVE")))
			.andExpect(jsonPath("$.page", is(0)))
			.andExpect(jsonPath("$.size", is(10)))
			.andExpect(jsonPath("$.totalElements", is(1)))
			.andExpect(jsonPath("$.totalPages", is(1)));
	}

	@Test
	void shouldRejectWorkflowActionWhenTransitionIsInvalid() throws Exception {
		ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(
				ruleMetadata("WORKFLOW_RULE", "1.0.0", "Workflow Rule", RuleLifecycleStatus.RETIRED, RuleActivationState.INACTIVE)
			)
		);

		mockMvc.perform(
			post("/api/admin/rules/WORKFLOW_RULE/versions/1.0.0/actions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "action": "REACTIVATE"
					}
					""")
		)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status", is(400)))
			.andExpect(jsonPath("$.message", is("Rule governance state transition is invalid.")))
			.andExpect(jsonPath("$.details[0]", is("Lifecycle transition from RETIRED to ACTIVE is not permitted.")));
	}

	@Test
	void shouldReturnNotFoundWhenWorkflowActionTargetDoesNotExist() throws Exception {
		mockMvc.perform(
			post("/api/admin/rules/DOES_NOT_EXIST/versions/1.0.0/actions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "action": "RETIRE"
					}
					""")
		)
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status", is(404)))
			.andExpect(jsonPath("$.message", is("Rule governance metadata not found for ruleCode 'DOES_NOT_EXIST' and version '1.0.0'.")));
	}

	private RuleGovernanceMetadata ruleMetadata(
		String ruleCode,
		String version,
		String ruleName,
		RuleLifecycleStatus lifecycleStatus,
		RuleActivationState activationState
	) {
		return new RuleGovernanceMetadata(
			new RuleIdentity(ruleCode, version),
			ruleName,
			new RuleLifecycleState(lifecycleStatus, activationState),
			RuleExecutionSource.CODE_DEFINED
		);
	}
}
