package com.capitec.fraudengine.application.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.capitec.fraudengine.api.dto.FraudEvaluationRequestDto;
import com.capitec.fraudengine.api.dto.FraudEvaluationResponseDto;
import com.capitec.fraudengine.api.dto.FraudEvaluationSummaryResponseDto;
import com.capitec.fraudengine.api.dto.LocationDto;
import com.capitec.fraudengine.api.dto.RuleResultResponseDto;
import com.capitec.fraudengine.domain.model.FraudEvaluation;
import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.TransactionEvent;
import com.capitec.fraudengine.domain.model.TransactionLocation;
import com.capitec.fraudengine.domain.model.enums.MerchantCategory;
import com.capitec.fraudengine.domain.model.enums.TransactionChannel;
import com.capitec.fraudengine.domain.model.enums.TransactionType;

/**
 * Maps between API-layer DTOs and domain-layer fraud evaluation models.
 */
@Component
public class FraudEvaluationApplicationMapper {

	/**
	 * Maps an evaluation request DTO into the domain transaction event.
	 *
	 * @param request request DTO from the API layer
	 * @return domain transaction event
	 */
	public TransactionEvent toDomain(FraudEvaluationRequestDto request) {
		return new TransactionEvent(
			request.transactionId(),
			request.accountId(),
			request.customerId(),
			request.amount(),
			request.currency(),
			request.merchantId(),
			toMerchantCategory(request.merchantCategory()),
			toTransactionType(request.transactionType()),
			toTransactionChannel(request.channel()),
			request.eventTimestamp(),
			toDomain(request.location()),
			request.reference()
		);
	}

	/**
	 * Maps a full fraud evaluation aggregate into the detailed response DTO.
	 *
	 * @param evaluation domain evaluation aggregate
	 * @return detailed response DTO
	 */
	public FraudEvaluationResponseDto toResponse(FraudEvaluation evaluation) {
		return new FraudEvaluationResponseDto(
			evaluation.evaluationId(),
			evaluation.transactionEvent().transactionId(),
			evaluation.decision().name(),
			evaluation.decisionScore(),
			evaluation.evaluatedAt(),
			evaluation.traceSummary(),
			evaluation.ruleResults().stream().map(this::toRuleResultResponse).toList()
		);
	}

	/**
	 * Maps a fraud evaluation aggregate into the lightweight list view.
	 *
	 * @param evaluation domain evaluation aggregate
	 * @return summary response DTO
	 */
	public FraudEvaluationSummaryResponseDto toSummaryResponse(FraudEvaluation evaluation) {
		return new FraudEvaluationSummaryResponseDto(
			evaluation.evaluationId(),
			evaluation.transactionEvent().transactionId(),
			evaluation.transactionEvent().accountId(),
			evaluation.decision().name(),
			evaluation.decisionScore(),
			evaluation.evaluatedAt()
		);
	}

	private TransactionLocation toDomain(LocationDto location) {
		if (location == null) {
			return null;
		}

		return new TransactionLocation(location.countryCode(), location.city());
	}

	private RuleResultResponseDto toRuleResultResponse(RuleEvaluationResult ruleResult) {
		return new RuleResultResponseDto(
			ruleResult.ruleCode(),
			ruleResult.ruleName(),
			ruleResult.triggered(),
			ruleResult.severity().name(),
			ruleResult.scoreContribution(),
			ruleResult.reason()
		);
	}

	private MerchantCategory toMerchantCategory(String value) {
		return MerchantCategory.valueOf(normalizeEnumValue(value));
	}

	private TransactionType toTransactionType(String value) {
		return TransactionType.valueOf(normalizeEnumValue(value));
	}

	private TransactionChannel toTransactionChannel(String value) {
		return TransactionChannel.valueOf(normalizeEnumValue(value));
	}

	private String normalizeEnumValue(String value) {
		return value.trim().toUpperCase().replace('-', '_').replace(' ', '_');
	}
}
