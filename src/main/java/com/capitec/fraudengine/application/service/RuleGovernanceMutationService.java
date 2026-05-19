package com.capitec.fraudengine.application.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capitec.fraudengine.api.dto.RuleGovernanceMetadataResponseDto;
import com.capitec.fraudengine.api.error.RuleGovernanceMetadataNotFoundException;
import com.capitec.fraudengine.api.error.RuleGovernanceRuleCodeNotFoundException;
import com.capitec.fraudengine.common.error.InvalidRuleGovernanceStateException;
import com.capitec.fraudengine.domain.model.RuleGovernanceMetadata;
import com.capitec.fraudengine.domain.model.RuleIdentity;
import com.capitec.fraudengine.domain.model.RuleLifecycleState;
import com.capitec.fraudengine.domain.model.enums.RuleExecutionSource;
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
	private final RuleGovernanceConfigurationReadModelService ruleGovernanceConfigurationReadModelService;
	private final MeterRegistry meterRegistry;

	public RuleGovernanceMutationService(
		RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository,
		RuleGovernanceMetadataPersistenceMapper ruleGovernanceMetadataPersistenceMapper,
		RuleGovernancePolicy ruleGovernancePolicy,
		RuleGovernanceConfigurationReadModelService ruleGovernanceConfigurationReadModelService,
		MeterRegistry meterRegistry
	) {
		this.ruleGovernanceMetadataJpaRepository = ruleGovernanceMetadataJpaRepository;
		this.ruleGovernanceMetadataPersistenceMapper = ruleGovernanceMetadataPersistenceMapper;
		this.ruleGovernancePolicy = ruleGovernancePolicy;
		this.ruleGovernanceConfigurationReadModelService = ruleGovernanceConfigurationReadModelService;
		this.meterRegistry = meterRegistry;
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
		recordMutationMetric("transition_state", "success");

		return new RuleGovernanceMetadataResponseDto(
			updatedMetadata.identity().ruleCode(),
			updatedMetadata.identity().version(),
			updatedMetadata.ruleName(),
			updatedMetadata.lifecycleState().lifecycleStatus(),
			updatedMetadata.lifecycleState().activationState(),
			updatedMetadata.executionSource(),
			ruleGovernanceConfigurationReadModelService.describe(updatedMetadata.identity().ruleCode())
		);
	}

	/**
	 * Registers a new governed metadata version for an existing rule code.
	 *
	 * @param ruleCode stable machine-readable rule code
	 * @param version new semantic rule version
	 * @param lifecycleState target lifecycle + activation state for the new version
	 * @return created metadata projection
	 */
	@Transactional
	public RuleGovernanceMetadataResponseDto registerVersion(
		String ruleCode,
		String version,
		RuleLifecycleState lifecycleState
	) {
		boolean hasExistingRuleCode = ruleGovernanceMetadataJpaRepository.existsByRuleCode(ruleCode);
		if (!hasExistingRuleCode) {
			throw new RuleGovernanceRuleCodeNotFoundException(ruleCode);
		}

		boolean versionAlreadyExists = ruleGovernanceMetadataJpaRepository.findByRuleCodeAndRuleVersion(ruleCode, version).isPresent();
		if (versionAlreadyExists) {
			throw new InvalidRuleGovernanceStateException(
				"Rule governance version '" + version + "' already exists for ruleCode '" + ruleCode + "'."
			);
		}

		RuleGovernanceMetadataEntity latestEntity = ruleGovernanceMetadataJpaRepository
			.findFirstByRuleCodeOrderByUpdatedAtDesc(ruleCode)
			.orElseThrow(() -> new RuleGovernanceRuleCodeNotFoundException(ruleCode));

		RuleGovernanceMetadata latestMetadata = ruleGovernanceMetadataPersistenceMapper.toDomain(latestEntity);
		RuleGovernanceMetadata newVersionMetadata = new RuleGovernanceMetadata(
			new RuleIdentity(ruleCode, version),
			latestMetadata.ruleName(),
			lifecycleState,
			RuleExecutionSource.CODE_DEFINED
		);

		ruleGovernancePolicy.validateState(newVersionMetadata);
		ruleGovernancePolicy.validateExecutionBoundary(newVersionMetadata);

		RuleGovernanceMetadataEntity savedEntity = ruleGovernanceMetadataJpaRepository.save(
			ruleGovernanceMetadataPersistenceMapper.toEntity(newVersionMetadata)
		);
		RuleGovernanceMetadata savedMetadata = ruleGovernanceMetadataPersistenceMapper.toDomain(savedEntity);
		recordMutationMetric("register_version", "success");

		return new RuleGovernanceMetadataResponseDto(
			savedMetadata.identity().ruleCode(),
			savedMetadata.identity().version(),
			savedMetadata.ruleName(),
			savedMetadata.lifecycleState().lifecycleStatus(),
			savedMetadata.lifecycleState().activationState(),
			savedMetadata.executionSource(),
			ruleGovernanceConfigurationReadModelService.describe(savedMetadata.identity().ruleCode())
		);
	}

	private void recordMutationMetric(String operation, String outcome) {
		meterRegistry.counter(
			"fraud.governance.mutation.total",
			"operation",
			operation,
			"outcome",
			outcome
		).increment();
	}
}
