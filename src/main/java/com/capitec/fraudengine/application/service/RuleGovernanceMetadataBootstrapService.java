package com.capitec.fraudengine.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capitec.fraudengine.domain.model.RuleGovernanceMetadata;
import com.capitec.fraudengine.domain.model.RuleIdentity;
import com.capitec.fraudengine.domain.model.RuleLifecycleState;
import com.capitec.fraudengine.domain.model.enums.RuleActivationState;
import com.capitec.fraudengine.domain.model.enums.RuleExecutionSource;
import com.capitec.fraudengine.domain.model.enums.RuleLifecycleStatus;
import com.capitec.fraudengine.domain.rule.FraudRule;
import com.capitec.fraudengine.infrastructure.persistence.entity.RuleGovernanceMetadataEntity;
import com.capitec.fraudengine.infrastructure.persistence.mapper.RuleGovernanceMetadataPersistenceMapper;
import com.capitec.fraudengine.infrastructure.persistence.repository.RuleGovernanceMetadataJpaRepository;

/**
 * Ensures code-defined rules have corresponding governance metadata persisted.
 */
@Service
public class RuleGovernanceMetadataBootstrapService {

	private static final String INITIAL_RULE_VERSION = "1.0.0";

	private final List<FraudRule> fraudRules;
	private final RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository;
	private final RuleGovernanceMetadataPersistenceMapper ruleGovernanceMetadataPersistenceMapper;

	public RuleGovernanceMetadataBootstrapService(
		List<FraudRule> fraudRules,
		RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository,
		RuleGovernanceMetadataPersistenceMapper ruleGovernanceMetadataPersistenceMapper
	) {
		this.fraudRules = fraudRules;
		this.ruleGovernanceMetadataJpaRepository = ruleGovernanceMetadataJpaRepository;
		this.ruleGovernanceMetadataPersistenceMapper = ruleGovernanceMetadataPersistenceMapper;
	}

	/**
	 * Upserts a metadata row for each code-defined rule.
	 */
	@Transactional
	public void ensureMetadataForCodeDefinedRules() {
		for (FraudRule fraudRule : fraudRules) {
			RuleGovernanceMetadata metadata = new RuleGovernanceMetadata(
				new RuleIdentity(fraudRule.ruleCode(), INITIAL_RULE_VERSION),
				fraudRule.ruleName(),
				new RuleLifecycleState(RuleLifecycleStatus.ACTIVE, RuleActivationState.ACTIVE),
				RuleExecutionSource.CODE_DEFINED
			);

			RuleGovernanceMetadataEntity entity = ruleGovernanceMetadataJpaRepository
				.findByRuleCodeAndRuleVersion(metadata.identity().ruleCode(), metadata.identity().version())
				.map(existing -> {
					ruleGovernanceMetadataPersistenceMapper.updateEntity(metadata, existing);
					return existing;
				})
				.orElseGet(() -> ruleGovernanceMetadataPersistenceMapper.toEntity(metadata));

			ruleGovernanceMetadataJpaRepository.save(entity);
		}
	}
}
