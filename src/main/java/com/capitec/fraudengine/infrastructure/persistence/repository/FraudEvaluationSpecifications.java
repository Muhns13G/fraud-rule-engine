package com.capitec.fraudengine.infrastructure.persistence.repository;

import java.time.OffsetDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.capitec.fraudengine.domain.model.enums.FraudDecision;
import com.capitec.fraudengine.domain.model.enums.MerchantCategory;
import com.capitec.fraudengine.domain.model.enums.TransactionChannel;
import com.capitec.fraudengine.infrastructure.persistence.entity.FraudEvaluationEntity;

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

		return specification;
	}
}
