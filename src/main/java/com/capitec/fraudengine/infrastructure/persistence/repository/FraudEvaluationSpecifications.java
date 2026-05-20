package com.capitec.fraudengine.infrastructure.persistence.repository;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import com.capitec.fraudengine.application.service.FraudEvaluationRuleHitMatchMode;
import com.capitec.fraudengine.domain.model.enums.FraudDecision;
import com.capitec.fraudengine.domain.model.enums.MerchantCategory;
import com.capitec.fraudengine.domain.model.enums.TransactionChannel;
import com.capitec.fraudengine.infrastructure.persistence.entity.FraudEvaluationEntity;
import com.capitec.fraudengine.infrastructure.persistence.entity.FraudRuleResultEntity;

/**
 * Specification helpers for fraud-evaluation retrieval filters.
 */
public final class FraudEvaluationSpecifications {

	private FraudEvaluationSpecifications() {
	}

	public static Specification<FraudEvaluationEntity> withReviewFilters(
		FraudDecision decision,
		String accountId,
		String customerId,
		String transactionId,
		MerchantCategory merchantCategory,
		TransactionChannel channel,
		List<String> normalizedRuleHits,
		FraudEvaluationRuleHitMatchMode ruleHitMatch,
		OffsetDateTime from,
		OffsetDateTime to
	) {
		Specification<FraudEvaluationEntity> specification = Specification.unrestricted();

		if (decision != null) {
			specification = specification.and((root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.get("decision"), decision)
			);
		}

		if (accountId != null) {
			specification = specification.and((root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.get("accountId"), accountId)
			);
		}

		if (customerId != null) {
			specification = specification.and((root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.get("customerId"), customerId)
			);
		}

		if (transactionId != null) {
			specification = specification.and((root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.get("transactionId"), transactionId)
			);
		}

		if (merchantCategory != null) {
			specification = specification.and((root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.get("merchantCategory"), merchantCategory)
			);
		}

		if (channel != null) {
			specification = specification.and((root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.get("channel"), channel)
			);
		}

		if (from != null && to != null) {
			specification = specification.and((root, query, criteriaBuilder) ->
				criteriaBuilder.between(root.get("evaluatedAt"), from, to)
			);
		}
		else if (from != null) {
			specification = specification.and((root, query, criteriaBuilder) ->
				criteriaBuilder.greaterThanOrEqualTo(root.get("evaluatedAt"), from)
			);
		}
		else if (to != null) {
			specification = specification.and((root, query, criteriaBuilder) ->
				criteriaBuilder.lessThanOrEqualTo(root.get("evaluatedAt"), to)
			);
		}

		if (!normalizedRuleHits.isEmpty()) {
			specification = specification.and(ruleHitMatch == FraudEvaluationRuleHitMatchMode.ALL
				? withAllTriggeredRules(normalizedRuleHits)
				: withAnyTriggeredRule(normalizedRuleHits));
		}

		return specification;
	}

	private static Specification<FraudEvaluationEntity> withAnyTriggeredRule(List<String> normalizedRuleHits) {
		return (root, query, criteriaBuilder) -> {
			Subquery<Long> ruleHitSubquery = query.subquery(Long.class);
			var ruleResultRoot = ruleHitSubquery.from(FraudRuleResultEntity.class);
			ruleHitSubquery.select(criteriaBuilder.literal(1L))
				.where(
					criteriaBuilder.equal(ruleResultRoot.get("fraudEvaluation"), root),
					criteriaBuilder.isTrue(ruleResultRoot.get("triggered")),
					ruleResultRoot.get("ruleCode").in(normalizedRuleHits)
				);
			return criteriaBuilder.exists(ruleHitSubquery);
		};
	}

	private static Specification<FraudEvaluationEntity> withAllTriggeredRules(List<String> normalizedRuleHits) {
		Specification<FraudEvaluationEntity> specification = Specification.unrestricted();

		for (String requestedRuleCode : normalizedRuleHits) {
			specification = specification.and((root, query, criteriaBuilder) -> {
				Subquery<Long> allRulesSubquery = query.subquery(Long.class);
				var ruleResultRoot = allRulesSubquery.from(FraudRuleResultEntity.class);
				allRulesSubquery.select(criteriaBuilder.literal(1L))
					.where(
						criteriaBuilder.equal(ruleResultRoot.get("fraudEvaluation"), root),
						criteriaBuilder.isTrue(ruleResultRoot.get("triggered")),
						criteriaBuilder.equal(ruleResultRoot.get("ruleCode"), requestedRuleCode)
					);
				return criteriaBuilder.exists(allRulesSubquery);
			});
		}

		return specification;
	}
}
