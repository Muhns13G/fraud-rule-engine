package com.capitec.fraudengine.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.capitec.fraudengine.TestcontainersConfiguration;
import com.capitec.fraudengine.domain.model.enums.RuleActivationState;
import com.capitec.fraudengine.domain.model.enums.RuleExecutionSource;
import com.capitec.fraudengine.domain.model.enums.RuleLifecycleStatus;
import com.capitec.fraudengine.domain.rule.FraudRule;
import com.capitec.fraudengine.infrastructure.persistence.repository.RuleGovernanceMetadataJpaRepository;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class RuleGovernanceMetadataBootstrapServiceIntegrationTest {

	@Autowired
	private List<FraudRule> fraudRules;

	@Autowired
	private RuleGovernanceMetadataBootstrapService bootstrapService;

	@Autowired
	private RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository;

	@BeforeEach
	void setUp() {
		ruleGovernanceMetadataJpaRepository.deleteAll();
	}

	@Test
	void shouldPersistMetadataForAllCodeDefinedRules() {
		bootstrapService.ensureMetadataForCodeDefinedRules();

		assertEquals(fraudRules.size(), ruleGovernanceMetadataJpaRepository.count());
		ruleGovernanceMetadataJpaRepository.findAll().forEach(entity -> {
			assertEquals("1.0.0", entity.getRuleVersion());
			assertEquals(RuleLifecycleStatus.ACTIVE, entity.getLifecycleStatus());
			assertEquals(RuleActivationState.ACTIVE, entity.getActivationState());
			assertEquals(RuleExecutionSource.CODE_DEFINED, entity.getExecutionSource());
			assertTrue(
				fraudRules.stream().anyMatch(rule -> rule.ruleCode().equals(entity.getRuleCode())),
				"Persisted rule code must map to a configured code-defined rule."
			);
		});
	}
}
