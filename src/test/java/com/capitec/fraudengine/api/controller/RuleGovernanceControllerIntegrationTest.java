package com.capitec.fraudengine.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.capitec.fraudengine.TestcontainersConfiguration;
import com.capitec.fraudengine.domain.model.RuleGovernanceMetadata;
import com.capitec.fraudengine.domain.model.RuleIdentity;
import com.capitec.fraudengine.domain.model.RuleLifecycleState;
import com.capitec.fraudengine.domain.model.enums.RuleActivationState;
import com.capitec.fraudengine.domain.model.enums.RuleExecutionSource;
import com.capitec.fraudengine.domain.model.enums.RuleLifecycleStatus;
import com.capitec.fraudengine.infrastructure.persistence.mapper.RuleGovernanceMetadataPersistenceMapper;
import com.capitec.fraudengine.infrastructure.persistence.repository.RuleGovernanceMetadataJpaRepository;

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

	@BeforeEach
	void setUp() {
		ruleGovernanceMetadataJpaRepository.deleteAll();
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
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].ruleCode", is("ACTIVE_RULE")))
			.andExpect(jsonPath("$[0].activationState", is("ACTIVE")));
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
			.andExpect(jsonPath("$", hasSize(2)));
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
			.andExpect(jsonPath("$.executionSource", is("CODE_DEFINED")));
	}

	@Test
	void shouldReturnNotFoundWhenRuleMetadataDoesNotExist() throws Exception {
		mockMvc.perform(get("/api/admin/rules/DOES_NOT_EXIST/versions/9.9.9"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status", is(404)))
			.andExpect(jsonPath("$.message", is("Rule governance metadata not found for ruleCode 'DOES_NOT_EXIST' and version '9.9.9'.")));
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
