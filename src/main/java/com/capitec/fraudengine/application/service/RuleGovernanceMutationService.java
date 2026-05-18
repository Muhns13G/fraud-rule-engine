package com.capitec.fraudengine.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capitec.fraudengine.api.dto.RuleGovernanceMetadataResponseDto;
import com.capitec.fraudengine.api.error.RuleGovernanceMetadataNotFoundException;
import com.capitec.fraudengine.domain.model.RuleGovernanceMetadata;
import com.capitec.fraudengine.domain.model.RuleLifecycleState;
import com.capitec.fraudengine.domain.policy.RuleGovernancePolicy;
import com.capitec.fraudengine.infrastructure.persistence.entity.RuleGovernanceMetadataEntity;
import com.capitec.fraudengine.infrastructure.persistence.mapper.RuleGovernanceMetadataPersistenceMapper;
import com.capitec.fraudengine.infrastructure.persistence.repository.RuleGovernanceMetadataJpaRepository;

/**
 * Mutation use cases for governed rule lifecycle metadata.
 */
@Service
public class RuleGovernanceMutationService {

	private final RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository;
	private final RuleGovernanceMetadataPersistenceMapper ruleGovernanceMetadataPersistenceMapper;
	private final RuleGovernancePolicy ruleGovernancePolicy;

	public RuleGovernanceMutationService(
		RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository,
		RuleGovernanceMetadataPersistenceMapper ruleGovernanceMetadataPersistenceMapper,
		RuleGovernancePolicy ruleGovernancePolicy
	) {
		this.ruleGovernanceMetadataJpaRepository = ruleGovernanceMetadataJpaRepository;
		this.ruleGovernanceMetadataPersistenceMapper = ruleGovernanceMetadataPersistenceMapper;
		this.ruleGovernancePolicy = ruleGovernancePolicy;
	}

	/**
	 * Updates lifecycle and activation state for one governed rule metadata identity.
	 *
	 * @param ruleCode stable machine-readable rule code
	 * @param version semantic rule version
	 * @param lifecycleState target lifecycle + activation state
	 * @return updated metadata projection
	 */
	@Transactional
	public RuleGovernanceMetadataResponseDto transitionState(
		String ruleCode,
		String version,
		RuleLifecycleState lifecycleState
	) {
		RuleGovernanceMetadataEntity existingEntity = ruleGovernanceMetadataJpaRepository
			.findByRuleCodeAndRuleVersion(ruleCode, version)
			.orElseThrow(() -> new RuleGovernanceMetadataNotFoundException(ruleCode, version));

		RuleGovernanceMetadata existingMetadata = ruleGovernanceMetadataPersistenceMapper.toDomain(existingEntity);
		RuleGovernanceMetadata targetMetadata = new RuleGovernanceMetadata(
			existingMetadata.identity(),
			existingMetadata.ruleName(),
			lifecycleState,
			existingMetadata.executionSource()
		);

		ruleGovernancePolicy.validateTransition(existingMetadata, targetMetadata);
		ruleGovernancePolicy.validateState(targetMetadata);
		ruleGovernancePolicy.validateExecutionBoundary(targetMetadata);

		ruleGovernanceMetadataPersistenceMapper.updateEntity(targetMetadata, existingEntity);
		RuleGovernanceMetadataEntity updatedEntity = ruleGovernanceMetadataJpaRepository.save(existingEntity);
		RuleGovernanceMetadata updatedMetadata = ruleGovernanceMetadataPersistenceMapper.toDomain(updatedEntity);

		return new RuleGovernanceMetadataResponseDto(
			updatedMetadata.identity().ruleCode(),
			updatedMetadata.identity().version(),
			updatedMetadata.ruleName(),
			updatedMetadata.lifecycleState().lifecycleStatus(),
			updatedMetadata.lifecycleState().activationState(),
			updatedMetadata.executionSource()
		);
	}
}
