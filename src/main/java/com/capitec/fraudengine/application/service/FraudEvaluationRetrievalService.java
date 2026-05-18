package com.capitec.fraudengine.application.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import com.capitec.fraudengine.api.dto.FraudEvaluationResponseDto;
import com.capitec.fraudengine.api.dto.FraudEvaluationSummaryPageResponseDto;
import com.capitec.fraudengine.api.dto.FraudEvaluationSummaryResponseDto;
import com.capitec.fraudengine.application.mapper.FraudEvaluationApplicationMapper;
import com.capitec.fraudengine.domain.model.FraudEvaluation;
import com.capitec.fraudengine.domain.model.enums.FraudDecision;
import com.capitec.fraudengine.infrastructure.persistence.mapper.FraudEvaluationPersistenceMapper;
import com.capitec.fraudengine.infrastructure.persistence.repository.FraudEvaluationJpaRepository;
import com.capitec.fraudengine.infrastructure.persistence.repository.FraudEvaluationSpecifications;

/**
 * Application service that loads persisted fraud evaluations and maps them into API-facing response shapes.
 */
@Service
public class FraudEvaluationRetrievalService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FraudEvaluationRetrievalService.class);

	private final FraudEvaluationApplicationMapper fraudEvaluationApplicationMapper;
	private final FraudEvaluationPersistenceMapper fraudEvaluationPersistenceMapper;
	private final FraudEvaluationJpaRepository fraudEvaluationJpaRepository;

	public FraudEvaluationRetrievalService(
		FraudEvaluationApplicationMapper fraudEvaluationApplicationMapper,
		FraudEvaluationPersistenceMapper fraudEvaluationPersistenceMapper,
		FraudEvaluationJpaRepository fraudEvaluationJpaRepository
	) {
		this.fraudEvaluationApplicationMapper = fraudEvaluationApplicationMapper;
		this.fraudEvaluationPersistenceMapper = fraudEvaluationPersistenceMapper;
		this.fraudEvaluationJpaRepository = fraudEvaluationJpaRepository;
	}

	/**
	 * Loads one persisted evaluation and maps it into the detailed response view.
	 *
	 * @param evaluationId persisted evaluation identifier
	 * @return optional detailed response DTO
	 */
	@Transactional(readOnly = true)
	public Optional<FraudEvaluationResponseDto> findById(UUID evaluationId) {
		Optional<FraudEvaluationResponseDto> response = fraudEvaluationJpaRepository.findById(evaluationId)
			.map(fraudEvaluationPersistenceMapper::toDomain)
			.map(fraudEvaluationApplicationMapper::toResponse);

		LOGGER.info(
			"fraud_evaluation_lookup_by_id evaluationId={} found={}",
			evaluationId,
			response.isPresent()
		);

		return response;
	}

	/**
	 * Loads persisted evaluations using the current review filter set and maps them into summary response DTOs.
	 *
	 * @param decision optional decision filter
	 * @param accountId optional account filter
	 * @param customerId optional customer filter
	 * @param transactionId optional transaction filter
	 * @param from optional time-range start
	 * @param to optional time-range end
	 * @return summary response DTOs
	 */
	@Transactional(readOnly = true)
	public FraudEvaluationSummaryPageResponseDto findSummaries(
		FraudDecision decision,
		String accountId,
		String customerId,
		String transactionId,
		FraudEvaluationSummarySortOrder sortOrder,
		OffsetDateTime from,
		OffsetDateTime to,
		int page,
		int size
	) {
		Page<FraudEvaluationSummaryResponseDto> responses = findDomainEvaluations(
			decision,
			accountId,
			customerId,
			transactionId,
			sortOrder,
			from,
			to,
			page,
			size
		).map(fraudEvaluationApplicationMapper::toSummaryResponse);

		LOGGER.info(
			"fraud_evaluation_summary_query decision={} accountIdPresent={} customerIdPresent={} transactionIdPresent={} fromPresent={} toPresent={} sort={} page={} size={} pageResultCount={} totalElements={} totalPages={}",
			decision,
			accountId != null,
			customerId != null,
			transactionId != null,
			from != null,
			to != null,
			sortOrder,
			page,
			size,
			responses.getNumberOfElements(),
			responses.getTotalElements(),
			responses.getTotalPages()
		);

		return new FraudEvaluationSummaryPageResponseDto(
			responses.getContent(),
			responses.getNumber(),
			responses.getSize(),
			responses.getTotalElements(),
			responses.getTotalPages()
		);
	}

	private Page<FraudEvaluation> findDomainEvaluations(
		FraudDecision decision,
		String accountId,
		String customerId,
		String transactionId,
		FraudEvaluationSummarySortOrder sortOrder,
		OffsetDateTime from,
		OffsetDateTime to,
		int page,
		int size
	) {
		return fraudEvaluationJpaRepository.findAll(
			FraudEvaluationSpecifications.withReviewFilters(decision, accountId, customerId, transactionId, from, to),
			PageRequest.of(page, size, toSort(sortOrder))
		).map(fraudEvaluationPersistenceMapper::toDomain);
	}

	private Sort toSort(FraudEvaluationSummarySortOrder sortOrder) {
		if (sortOrder == FraudEvaluationSummarySortOrder.OLDEST_FIRST) {
			return Sort.by(
				Sort.Order.asc("evaluatedAt"),
				Sort.Order.asc("evaluationId")
			);
		}

		return Sort.by(
			Sort.Order.desc("evaluatedAt"),
			Sort.Order.desc("decisionScore"),
			Sort.Order.desc("evaluationId")
		);
	}
}
