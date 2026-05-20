package com.capitec.fraudengine.application.service;

import java.util.List;
import java.util.Optional;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capitec.fraudengine.api.dto.RuleGovernanceHistoryPageResponseDto;
import com.capitec.fraudengine.api.dto.RuleGovernanceHistoryResponseDto;
import com.capitec.fraudengine.api.dto.RuleGovernanceMetadataResponseDto;
import com.capitec.fraudengine.api.dto.RuleGovernanceMetadataPageResponseDto;
import com.capitec.fraudengine.api.error.RuleGovernanceMetadataNotFoundException;
import com.capitec.fraudengine.api.error.RuleGovernanceRuleCodeNotFoundException;
import com.capitec.fraudengine.domain.model.RuleGovernanceMetadata;
import com.capitec.fraudengine.domain.model.enums.RuleActivationState;
import com.capitec.fraudengine.infrastructure.persistence.entity.RuleGovernanceHistoryEntity;
import com.capitec.fraudengine.infrastructure.persistence.mapper.RuleGovernanceMetadataPersistenceMapper;
import com.capitec.fraudengine.infrastructure.persistence.repository.RuleGovernanceHistoryJpaRepository;
import com.capitec.fraudengine.infrastructure.persistence.repository.RuleGovernanceMetadataJpaRepository;

/**
 * Read-only service for governance metadata visibility over code-defined rules.
 */
@Service
public class RuleGovernanceRetrievalService {

	private final RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository;
	private final RuleGovernanceHistoryJpaRepository ruleGovernanceHistoryJpaRepository;
	private final RuleGovernanceMetadataPersistenceMapper ruleGovernanceMetadataPersistenceMapper;
	private final RuleGovernanceConfigurationReadModelService ruleGovernanceConfigurationReadModelService;
	private final MeterRegistry meterRegistry;

	public RuleGovernanceRetrievalService(
		RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository,
		RuleGovernanceHistoryJpaRepository ruleGovernanceHistoryJpaRepository,
		RuleGovernanceMetadataPersistenceMapper ruleGovernanceMetadataPersistenceMapper,
		RuleGovernanceConfigurationReadModelService ruleGovernanceConfigurationReadModelService,
		MeterRegistry meterRegistry
	) {
		this.ruleGovernanceMetadataJpaRepository = ruleGovernanceMetadataJpaRepository;
		this.ruleGovernanceHistoryJpaRepository = ruleGovernanceHistoryJpaRepository;
		this.ruleGovernanceMetadataPersistenceMapper = ruleGovernanceMetadataPersistenceMapper;
		this.ruleGovernanceConfigurationReadModelService = ruleGovernanceConfigurationReadModelService;
		this.meterRegistry = meterRegistry;
	}

	/**
	 * Lists governance metadata entries. Defaults to active-only view for admin visibility.
	 *
	 * @param activeOnly whether to include only activation-state ACTIVE entries
	 * @return metadata response projection
	 */
	@Transactional(readOnly = true)
	public RuleGovernanceMetadataPageResponseDto findRules(boolean activeOnly, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("ruleCode").ascending().and(Sort.by("ruleVersion").ascending()));
		var metadataPage = activeOnly
			? ruleGovernanceMetadataJpaRepository.findByActivationState(RuleActivationState.ACTIVE, pageable)
			: ruleGovernanceMetadataJpaRepository.findAllBy(pageable);
		recordRetrievalMetric("find_rules", "success");
		return new RuleGovernanceMetadataPageResponseDto(
			metadataPage.getContent().stream()
				.map(ruleGovernanceMetadataPersistenceMapper::toDomain)
				.map(this::toResponse)
				.toList(),
			metadataPage.getNumber(),
			metadataPage.getSize(),
			metadataPage.getTotalElements(),
			metadataPage.getTotalPages()
		);
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
		Optional<RuleGovernanceMetadataResponseDto> response = ruleGovernanceMetadataJpaRepository.findByRuleCodeAndRuleVersion(ruleCode, version)
			.map(ruleGovernanceMetadataPersistenceMapper::toDomain)
			.map(this::toResponse);
		recordRetrievalMetric("find_rule", response.isPresent() ? "found" : "not_found");
		return response;
	}

	/**
	 * Finds paged governed versions for one rule code.
	 *
	 * @param ruleCode stable machine-readable rule code
	 * @param page zero-based page index
	 * @param size requested page size
	 * @return paged governed versions
	 */
	@Transactional(readOnly = true)
	public RuleGovernanceMetadataPageResponseDto findRuleVersions(String ruleCode, int page, int size) {
		if (!ruleGovernanceMetadataJpaRepository.existsByRuleCode(ruleCode)) {
			throw new RuleGovernanceRuleCodeNotFoundException(ruleCode);
		}

		Pageable pageable = PageRequest.of(page, size);
		var metadataPage = ruleGovernanceMetadataJpaRepository.findByRuleCodeOrderByRuleVersionAsc(ruleCode, pageable);
		recordRetrievalMetric("find_rule_versions", "success");

		return new RuleGovernanceMetadataPageResponseDto(
			metadataPage.getContent().stream()
				.map(ruleGovernanceMetadataPersistenceMapper::toDomain)
				.map(this::toResponse)
				.toList(),
			metadataPage.getNumber(),
			metadataPage.getSize(),
			metadataPage.getTotalElements(),
			metadataPage.getTotalPages()
		);
	}

	/**
	 * Finds paged lifecycle history trail entries for one governed rule identity.
	 *
	 * @param ruleCode stable machine-readable rule code
	 * @param version semantic rule version
	 * @param page zero-based page index
	 * @param size requested page size
	 * @return paged lifecycle history entries
	 */
	@Transactional(readOnly = true)
	public RuleGovernanceHistoryPageResponseDto findRuleHistory(String ruleCode, String version, int page, int size) {
		ruleGovernanceMetadataJpaRepository.findByRuleCodeAndRuleVersion(ruleCode, version)
			.orElseThrow(() -> new RuleGovernanceMetadataNotFoundException(ruleCode, version));

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending().and(Sort.by("governanceHistoryId").ascending()));
		var historyPage = ruleGovernanceHistoryJpaRepository.findByRuleCodeAndRuleVersion(ruleCode, version, pageable);
		recordRetrievalMetric("find_rule_history", "success");

		return new RuleGovernanceHistoryPageResponseDto(
			historyPage.getContent().stream().map(this::toHistoryResponse).toList(),
			historyPage.getNumber(),
			historyPage.getSize(),
			historyPage.getTotalElements(),
			historyPage.getTotalPages()
		);
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

	private RuleGovernanceHistoryResponseDto toHistoryResponse(RuleGovernanceHistoryEntity history) {
		return new RuleGovernanceHistoryResponseDto(
			history.getRuleCode(),
			history.getRuleVersion(),
			history.getActionType(),
			history.getActor(),
			history.getRequestId(),
			history.getFromLifecycleStatus(),
			history.getFromActivationState(),
			history.getToLifecycleStatus(),
			history.getToActivationState(),
			history.getCreatedAt()
		);
	}

	private void recordRetrievalMetric(String operation, String outcome) {
		meterRegistry.counter(
			"fraud.retrieval.request.total",
			"resource",
			"rule_governance",
			"operation",
			operation,
			"outcome",
			outcome
		).increment();
	}
}
