package com.oitws.fraudengine.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.oitws.fraudengine.domain.model.enums.RuleActivationState;
import com.oitws.fraudengine.infrastructure.persistence.entity.RuleGovernanceMetadataEntity;

/**
 * Spring Data repository for persisted rule governance metadata.
 */
public interface RuleGovernanceMetadataJpaRepository extends JpaRepository<RuleGovernanceMetadataEntity, Long> {

	/**
	 * Checks whether at least one governed version exists for a rule code.
	 *
	 * @param ruleCode stable machine-readable rule code
	 * @return true when any governed version exists
	 */
	boolean existsByRuleCode(String ruleCode);

	/**
	 * Finds the most recently updated governed metadata entry for a rule code.
	 *
	 * @param ruleCode stable machine-readable rule code
	 * @return latest updated entry when present
	 */
	Optional<RuleGovernanceMetadataEntity> findFirstByRuleCodeOrderByUpdatedAtDesc(String ruleCode);

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
	 * Lists rule metadata entries with pagination.
	 *
	 * @param pageable requested page and sort
	 * @return paged rule metadata entities
	 */
	Page<RuleGovernanceMetadataEntity> findAllBy(Pageable pageable);

	/**
	 * Lists rule metadata entries filtered by activation state and sorted for stable admin visibility.
	 *
	 * @param activationState activation state filter
	 * @return sorted matching rule metadata entities
	 */
	List<RuleGovernanceMetadataEntity> findByActivationStateOrderByRuleCodeAscRuleVersionAsc(
		RuleActivationState activationState
	);

	/**
	 * Lists filtered rule metadata entries by activation state with pagination.
	 *
	 * @param activationState activation state filter
	 * @param pageable requested page and sort
	 * @return paged matching rule metadata entities
	 */
	Page<RuleGovernanceMetadataEntity> findByActivationState(
		RuleActivationState activationState,
		Pageable pageable
	);

	/**
	 * Lists all governed versions for one rule code with pagination.
	 *
	 * @param ruleCode stable machine-readable rule code
	 * @param pageable requested page
	 * @return paged governed versions for the rule
	 */
	Page<RuleGovernanceMetadataEntity> findByRuleCodeOrderByRuleVersionAsc(String ruleCode, Pageable pageable);
}
