package com.capitec.fraudengine.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

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
}
