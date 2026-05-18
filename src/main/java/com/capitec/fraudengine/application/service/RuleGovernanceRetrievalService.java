package com.capitec.fraudengine.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capitec.fraudengine.api.dto.RuleGovernanceMetadataResponseDto;
import com.capitec.fraudengine.domain.model.RuleGovernanceMetadata;
import com.capitec.fraudengine.domain.model.enums.RuleActivationState;
import com.capitec.fraudengine.infrastructure.persistence.mapper.RuleGovernanceMetadataPersistenceMapper;
import com.capitec.fraudengine.infrastructure.persistence.repository.RuleGovernanceMetadataJpaRepository;

/**
 * Read-only service for governance metadata visibility over code-defined rules.
 */
@Service
public class RuleGovernanceRetrievalService {

	private final RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository;
	private final RuleGovernanceMetadataPersistenceMapper ruleGovernanceMetadataPersistenceMapper;
	private final RuleGovernanceConfigurationReadModelService ruleGovernanceConfigurationReadModelService;

	public RuleGovernanceRetrievalService(
		RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository,
		RuleGovernanceMetadataPersistenceMapper ruleGovernanceMetadataPersistenceMapper,
		RuleGovernanceConfigurationReadModelService ruleGovernanceConfigurationReadModelService
	) {
		this.ruleGovernanceMetadataJpaRepository = ruleGovernanceMetadataJpaRepository;
		this.ruleGovernanceMetadataPersistenceMapper = ruleGovernanceMetadataPersistenceMapper;
		this.ruleGovernanceConfigurationReadModelService = ruleGovernanceConfigurationReadModelService;
	}

	/**
	 * Lists governance metadata entries. Defaults to active-only view for admin visibility.
	 *
	 * @param activeOnly whether to include only activation-state ACTIVE entries
	 * @return metadata response projection
	 */
	@Transactional(readOnly = true)
	public List<RuleGovernanceMetadataResponseDto> findRules(boolean activeOnly) {
		List<RuleGovernanceMetadata> metadata = activeOnly
			? ruleGovernanceMetadataJpaRepository.findByActivationStateOrderByRuleCodeAscRuleVersionAsc(RuleActivationState.ACTIVE)
				.stream()
				.map(ruleGovernanceMetadataPersistenceMapper::toDomain)
				.toList()
			: ruleGovernanceMetadataJpaRepository.findAllByOrderByRuleCodeAscRuleVersionAsc()
				.stream()
				.map(ruleGovernanceMetadataPersistenceMapper::toDomain)
				.toList();

		return metadata.stream().map(this::toResponse).toList();
	}

	/**
	 * Finds one governance metadata entry by rule code and version.
	 *
	 * @param ruleCode stable machine-readable rule code
	 * @param version semantic rule version
	 * @return optional metadata response projection
	 */
	@Transactional(readOnly = true)
	public Optional<RuleGovernanceMetadataResponseDto> findRule(String ruleCode, String version) {
		return ruleGovernanceMetadataJpaRepository.findByRuleCodeAndRuleVersion(ruleCode, version)
			.map(ruleGovernanceMetadataPersistenceMapper::toDomain)
			.map(this::toResponse);
	}

	private RuleGovernanceMetadataResponseDto toResponse(RuleGovernanceMetadata metadata) {
		return new RuleGovernanceMetadataResponseDto(
			metadata.identity().ruleCode(),
			metadata.identity().version(),
			metadata.ruleName(),
			metadata.lifecycleState().lifecycleStatus(),
			metadata.lifecycleState().activationState(),
			metadata.executionSource(),
			ruleGovernanceConfigurationReadModelService.describe(metadata.identity().ruleCode())
		);
	}
}
