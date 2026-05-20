package com.capitec.fraudengine.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capitec.fraudengine.infrastructure.persistence.entity.RuleGovernanceHistoryEntity;

/**
 * Spring Data repository for persisted governance mutation history events.
 */
public interface RuleGovernanceHistoryJpaRepository extends JpaRepository<RuleGovernanceHistoryEntity, Long> {

	/**
	 * Lists governance history entries for a rule identity in timeline order.
	 *
	 * @param ruleCode stable machine-readable rule code
	 * @param ruleVersion semantic rule version
	 * @return history events ordered by creation timestamp then identity
	 */
	List<RuleGovernanceHistoryEntity> findByRuleCodeAndRuleVersionOrderByCreatedAtAscGovernanceHistoryIdAsc(
		String ruleCode,
		String ruleVersion
	);
}
