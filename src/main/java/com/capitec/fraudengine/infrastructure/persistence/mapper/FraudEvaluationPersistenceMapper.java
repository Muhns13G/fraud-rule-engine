package com.capitec.fraudengine.infrastructure.persistence.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.capitec.fraudengine.domain.model.FraudEvaluation;
import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.TransactionEvent;
import com.capitec.fraudengine.domain.model.TransactionLocation;
import com.capitec.fraudengine.infrastructure.persistence.entity.FraudEvaluationEntity;
import com.capitec.fraudengine.infrastructure.persistence.entity.FraudRuleResultEntity;

/**
 * Maps between framework-light fraud evaluation domain records and JPA entities.
 */
@Component
public class FraudEvaluationPersistenceMapper {

	/**
	 * Maps a domain fraud evaluation into its persistence representation.
	 *
	 * @param domain domain aggregate to persist
	 * @return JPA entity graph ready for persistence
	 */
	public FraudEvaluationEntity toEntity(FraudEvaluation domain) {
		FraudEvaluationEntity entity = new FraudEvaluationEntity();
		TransactionEvent transactionEvent = domain.transactionEvent();

		entity.setEvaluationId(domain.evaluationId());
		entity.setTransactionId(transactionEvent.transactionId());
		entity.setAccountId(transactionEvent.accountId());
		entity.setCustomerId(transactionEvent.customerId());
		entity.setAmount(transactionEvent.amount());
		entity.setCurrency(transactionEvent.currency());
		entity.setMerchantId(transactionEvent.merchantId());
		entity.setMerchantCategory(transactionEvent.merchantCategory());
		entity.setTransactionType(transactionEvent.transactionType());
		entity.setChannel(transactionEvent.channel());
		entity.setEventTimestamp(transactionEvent.eventTimestamp());
		entity.setReference(transactionEvent.reference());
		entity.setDecision(domain.decision());
		entity.setDecisionScore(domain.decisionScore());
		entity.setTraceSummary(domain.traceSummary());
		entity.setEvaluatedAt(domain.evaluatedAt());

		if (transactionEvent.location() != null) {
			entity.setLocationCountryCode(transactionEvent.location().countryCode());
			entity.setLocationCity(transactionEvent.location().city());
		}

		entity.setRuleResults(domain.ruleResults().stream().map(this::toRuleResultEntity).toList());
		return entity;
	}

	/**
	 * Maps a persisted evaluation entity graph back into the domain model.
	 *
	 * @param entity persisted evaluation entity
	 * @return domain aggregate
	 */
	public FraudEvaluation toDomain(FraudEvaluationEntity entity) {
		TransactionLocation location = null;
		if (entity.getLocationCountryCode() != null || entity.getLocationCity() != null) {
			location = new TransactionLocation(entity.getLocationCountryCode(), entity.getLocationCity());
		}

		TransactionEvent transactionEvent = new TransactionEvent(
			entity.getTransactionId(),
			entity.getAccountId(),
			entity.getCustomerId(),
			entity.getAmount(),
			entity.getCurrency(),
			entity.getMerchantId(),
			entity.getMerchantCategory(),
			entity.getTransactionType(),
			entity.getChannel(),
			entity.getEventTimestamp(),
			location,
			entity.getReference()
		);

		List<RuleEvaluationResult> ruleResults = entity.getRuleResults().stream()
			.map(this::toRuleEvaluationResult)
			.toList();

		return new FraudEvaluation(
			entity.getEvaluationId(),
			transactionEvent,
			entity.getDecision(),
			entity.getDecisionScore(),
			entity.getEvaluatedAt(),
			entity.getTraceSummary(),
			ruleResults
		);
	}

	private FraudRuleResultEntity toRuleResultEntity(RuleEvaluationResult domain) {
		FraudRuleResultEntity entity = new FraudRuleResultEntity();
		entity.setRuleCode(domain.ruleCode());
		entity.setRuleName(domain.ruleName());
		entity.setTriggered(domain.triggered());
		entity.setSeverity(domain.severity());
		entity.setScoreContribution(domain.scoreContribution());
		entity.setReason(domain.reason());
		return entity;
	}

	private RuleEvaluationResult toRuleEvaluationResult(FraudRuleResultEntity entity) {
		return new RuleEvaluationResult(
			entity.getRuleCode(),
			entity.getRuleName(),
			entity.isTriggered(),
			entity.getSeverity(),
			entity.getScoreContribution(),
			entity.getReason()
		);
	}
}
