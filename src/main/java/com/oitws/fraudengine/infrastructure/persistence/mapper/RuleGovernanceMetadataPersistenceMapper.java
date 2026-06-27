package com.oitws.fraudengine.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.oitws.fraudengine.domain.model.RuleGovernanceMetadata;
import com.oitws.fraudengine.domain.model.RuleIdentity;
import com.oitws.fraudengine.domain.model.RuleLifecycleState;
import com.oitws.fraudengine.infrastructure.persistence.entity.RuleGovernanceMetadataEntity;

/**
 * Maps rule governance metadata between domain and persistence shapes.
 */
@Component
public class RuleGovernanceMetadataPersistenceMapper {

	/**
	 * Converts a domain rule governance view into a persistence entity.
	 *
	 * @param source source domain model
	 * @return persistence entity
	 */
	public RuleGovernanceMetadataEntity toEntity(RuleGovernanceMetadata source) {
		RuleGovernanceMetadataEntity entity = new RuleGovernanceMetadataEntity();
		entity.setRuleCode(source.identity().ruleCode());
		entity.setRuleVersion(source.identity().version());
		entity.setRuleName(source.ruleName());
		entity.setLifecycleStatus(source.lifecycleState().lifecycleStatus());
		entity.setActivationState(source.lifecycleState().activationState());
		entity.setExecutionSource(source.executionSource());
		return entity;
	}

	/**
	 * Converts a persistence entity into the domain rule governance model.
	 *
	 * @param source source persistence entity
	 * @return domain model
	 */
	public RuleGovernanceMetadata toDomain(RuleGovernanceMetadataEntity source) {
		return new RuleGovernanceMetadata(
			new RuleIdentity(source.getRuleCode(), source.getRuleVersion()),
			source.getRuleName(),
			new RuleLifecycleState(source.getLifecycleStatus(), source.getActivationState()),
			source.getExecutionSource()
		);
	}

	/**
	 * Applies domain values to an existing entity.
	 *
	 * @param source source domain model
	 * @param target mutable persistence entity
	 */
	public void updateEntity(RuleGovernanceMetadata source, RuleGovernanceMetadataEntity target) {
		target.setRuleName(source.ruleName());
		target.setLifecycleStatus(source.lifecycleState().lifecycleStatus());
		target.setActivationState(source.lifecycleState().activationState());
		target.setExecutionSource(source.executionSource());
	}
}
