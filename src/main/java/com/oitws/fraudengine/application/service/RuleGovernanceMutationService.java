package com.oitws.fraudengine.application.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.oitws.fraudengine.api.dto.RuleGovernanceMetadataResponseDto;
import com.oitws.fraudengine.api.error.RuleGovernanceMetadataNotFoundException;
import com.oitws.fraudengine.api.error.RuleGovernanceRuleCodeNotFoundException;
import com.oitws.fraudengine.common.error.InvalidRuleGovernanceStateException;
import com.oitws.fraudengine.domain.model.RuleGovernanceMetadata;
import com.oitws.fraudengine.domain.model.RuleIdentity;
import com.oitws.fraudengine.domain.model.RuleLifecycleState;
import com.oitws.fraudengine.domain.model.enums.RuleActivationState;
import com.oitws.fraudengine.domain.model.enums.RuleGovernanceWorkflowAction;
import com.oitws.fraudengine.infrastructure.config.RequestCorrelationFilter;
import com.oitws.fraudengine.domain.model.enums.RuleExecutionSource;
import com.oitws.fraudengine.domain.model.enums.RuleLifecycleStatus;
import com.oitws.fraudengine.domain.policy.RuleGovernancePolicy;
import com.oitws.fraudengine.infrastructure.persistence.entity.RuleGovernanceMetadataEntity;
import com.oitws.fraudengine.infrastructure.persistence.entity.RuleGovernanceHistoryEntity;
import com.oitws.fraudengine.infrastructure.persistence.mapper.RuleGovernanceMetadataPersistenceMapper;
import com.oitws.fraudengine.infrastructure.persistence.repository.RuleGovernanceHistoryJpaRepository;
import com.oitws.fraudengine.infrastructure.persistence.repository.RuleGovernanceMetadataJpaRepository;

/**
 * Mutation use cases for governed rule lifecycle metadata.
 */
@Service
public class RuleGovernanceMutationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RuleGovernanceMutationService.class);

	private final RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository;
	private final RuleGovernanceHistoryJpaRepository ruleGovernanceHistoryJpaRepository;
	private final RuleGovernanceMetadataPersistenceMapper ruleGovernanceMetadataPersistenceMapper;
	private final RuleGovernancePolicy ruleGovernancePolicy;
	private final RuleGovernanceConfigurationReadModelService ruleGovernanceConfigurationReadModelService;
	private final MeterRegistry meterRegistry;

	public RuleGovernanceMutationService(
		RuleGovernanceMetadataJpaRepository ruleGovernanceMetadataJpaRepository,
		RuleGovernanceHistoryJpaRepository ruleGovernanceHistoryJpaRepository,
		RuleGovernanceMetadataPersistenceMapper ruleGovernanceMetadataPersistenceMapper,
		RuleGovernancePolicy ruleGovernancePolicy,
		RuleGovernanceConfigurationReadModelService ruleGovernanceConfigurationReadModelService,
		MeterRegistry meterRegistry
	) {
		this.ruleGovernanceMetadataJpaRepository = ruleGovernanceMetadataJpaRepository;
		this.ruleGovernanceHistoryJpaRepository = ruleGovernanceHistoryJpaRepository;
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
		return transitionStateInternal(ruleCode, version, lifecycleState, "STATE_TRANSITION");
	}

	private RuleGovernanceMetadataResponseDto transitionStateInternal(
		String ruleCode,
		String version,
		RuleLifecycleState lifecycleState,
		String actionType
	) {
		String actor = resolveActorIdentity();
		String requestId = resolveRequestId();

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
		persistGovernanceHistory(
			updatedMetadata.identity().ruleCode(),
			updatedMetadata.identity().version(),
			actionType,
			actor,
			requestId,
			existingMetadata.lifecycleState().lifecycleStatus(),
			existingMetadata.lifecycleState().activationState(),
			updatedMetadata.lifecycleState().lifecycleStatus(),
			updatedMetadata.lifecycleState().activationState()
		);
		recordMutationMetric("transition_state", "success");
		recordLifecycleTransitionMetric(existingMetadata, updatedMetadata);
		LOGGER.info(
			"rule_governance_state_transition_audit requestId={} actor={} ruleCode={} version={} fromLifecycle={} fromActivation={} toLifecycle={} toActivation={}",
			requestId,
			actor,
			updatedMetadata.identity().ruleCode(),
			updatedMetadata.identity().version(),
			existingMetadata.lifecycleState().lifecycleStatus(),
			existingMetadata.lifecycleState().activationState(),
			updatedMetadata.lifecycleState().lifecycleStatus(),
			updatedMetadata.lifecycleState().activationState()
		);

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
	 * Applies a semantic governance workflow action to one governed rule identity.
	 *
	 * @param ruleCode stable machine-readable rule code
	 * @param version semantic rule version
	 * @param action semantic workflow action
	 * @return updated metadata projection
	 */
	@Transactional
	public RuleGovernanceMetadataResponseDto applyWorkflowAction(
		String ruleCode,
		String version,
		RuleGovernanceWorkflowAction action
	) {
		if (action == null) {
			throw new InvalidRuleGovernanceStateException("Governance workflow action must be provided.");
		}

		RuleLifecycleState targetLifecycleState = switch (action) {
			case PROMOTE, REACTIVATE -> new RuleLifecycleState(
				RuleLifecycleStatus.ACTIVE,
				RuleActivationState.ACTIVE
			);
			case DEPRECATE -> new RuleLifecycleState(
				RuleLifecycleStatus.DEPRECATED,
				RuleActivationState.INACTIVE
			);
			case RETIRE -> new RuleLifecycleState(
				RuleLifecycleStatus.RETIRED,
				RuleActivationState.INACTIVE
			);
		};

		RuleGovernanceMetadataResponseDto response = transitionStateInternal(
			ruleCode,
			version,
			targetLifecycleState,
			"WORKFLOW_" + action.name()
		);
		recordMutationMetric("workflow_action", "success");
		return response;
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
		String actor = resolveActorIdentity();
		String requestId = resolveRequestId();

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
		persistGovernanceHistory(
			savedMetadata.identity().ruleCode(),
			savedMetadata.identity().version(),
			"VERSION_REGISTERED",
			actor,
			requestId,
			null,
			null,
			savedMetadata.lifecycleState().lifecycleStatus(),
			savedMetadata.lifecycleState().activationState()
		);
		recordMutationMetric("register_version", "success");
		recordVersionRegistrationMetric(savedMetadata);
		LOGGER.info(
			"rule_governance_version_registration_audit requestId={} actor={} ruleCode={} version={} lifecycleStatus={} activationState={} executionSource={}",
			requestId,
			actor,
			savedMetadata.identity().ruleCode(),
			savedMetadata.identity().version(),
			savedMetadata.lifecycleState().lifecycleStatus(),
			savedMetadata.lifecycleState().activationState(),
			savedMetadata.executionSource()
		);

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

	private void recordLifecycleTransitionMetric(
		RuleGovernanceMetadata previousMetadata,
		RuleGovernanceMetadata updatedMetadata
	) {
		meterRegistry.counter(
			"fraud.governance.lifecycle.transition.total",
			"ruleCode",
			updatedMetadata.identity().ruleCode(),
			"fromLifecycle",
			previousMetadata.lifecycleState().lifecycleStatus().name(),
			"toLifecycle",
			updatedMetadata.lifecycleState().lifecycleStatus().name(),
			"fromActivation",
			previousMetadata.lifecycleState().activationState().name(),
			"toActivation",
			updatedMetadata.lifecycleState().activationState().name()
		).increment();
	}

	private void recordVersionRegistrationMetric(RuleGovernanceMetadata savedMetadata) {
		meterRegistry.counter(
			"fraud.governance.version.registration.total",
			"ruleCode",
			savedMetadata.identity().ruleCode(),
			"lifecycleStatus",
			savedMetadata.lifecycleState().lifecycleStatus().name(),
			"activationState",
			savedMetadata.lifecycleState().activationState().name()
		).increment();
	}

	private String resolveActorIdentity() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
			return "anonymous";
		}
		return authentication.getName();
	}

	private String resolveRequestId() {
		String requestId = MDC.get(RequestCorrelationFilter.REQUEST_ID_MDC_KEY);
		if (requestId == null || requestId.isBlank()) {
			return "no-request-id";
		}
		return requestId;
	}

	private void persistGovernanceHistory(
		String ruleCode,
		String ruleVersion,
		String actionType,
		String actor,
		String requestId,
		RuleLifecycleStatus fromLifecycleStatus,
		RuleActivationState fromActivationState,
		RuleLifecycleStatus toLifecycleStatus,
		RuleActivationState toActivationState
	) {
		RuleGovernanceHistoryEntity historyEntity = new RuleGovernanceHistoryEntity();
		historyEntity.setRuleCode(ruleCode);
		historyEntity.setRuleVersion(ruleVersion);
		historyEntity.setActionType(actionType);
		historyEntity.setActor(actor);
		historyEntity.setRequestId(requestId);
		historyEntity.setFromLifecycleStatus(fromLifecycleStatus);
		historyEntity.setFromActivationState(fromActivationState);
		historyEntity.setToLifecycleStatus(toLifecycleStatus);
		historyEntity.setToActivationState(toActivationState);
		ruleGovernanceHistoryJpaRepository.save(historyEntity);
	}
}
