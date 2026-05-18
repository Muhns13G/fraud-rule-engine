package com.capitec.fraudengine.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capitec.fraudengine.domain.model.enums.RuleActivationState;
import com.capitec.fraudengine.infrastructure.persistence.entity.RuleGovernanceMetadataEntity;

/**
 * Spring Data repository for persisted rule governance metadata.
 */
public interface RuleGovernanceMetadataJpaRepository extends JpaRepository<RuleGovernanceMetadataEntity, Long> {

	/**
	 * Finds rule metadata by rule code and version.
	 *
	 * @param ruleCode stable machine-readable rule code
	 * @param ruleVersion semantic rule version
	 * @return matching rule metadata if present
	 */
	Optional<RuleGovernanceMetadataEntity> findByRuleCodeAndRuleVersion(String ruleCode, String ruleVersion);

	/**
	 * Lists all rule metadata entries sorted for stable admin visibility.
	 *
	 * @return sorted rule metadata entities
	 */
	List<RuleGovernanceMetadataEntity> findAllByOrderByRuleCodeAscRuleVersionAsc();

	/**
	 * Lists rule metadata entries filtered by activation state and sorted for stable admin visibility.
	 *
	 * @param activationState activation state filter
	 * @return sorted matching rule metadata entities
	 */
	List<RuleGovernanceMetadataEntity> findByActivationStateOrderByRuleCodeAscRuleVersionAsc(
		RuleActivationState activationState
	);
}
