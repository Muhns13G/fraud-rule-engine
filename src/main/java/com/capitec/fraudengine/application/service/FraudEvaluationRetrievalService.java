package com.capitec.fraudengine.application.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.capitec.fraudengine.api.dto.FraudEvaluationResponseDto;
import com.capitec.fraudengine.api.dto.FraudEvaluationSummaryResponseDto;
import com.capitec.fraudengine.application.mapper.FraudEvaluationApplicationMapper;
import com.capitec.fraudengine.domain.model.FraudEvaluation;
import com.capitec.fraudengine.domain.model.enums.FraudDecision;
import com.capitec.fraudengine.infrastructure.persistence.mapper.FraudEvaluationPersistenceMapper;
import com.capitec.fraudengine.infrastructure.persistence.repository.FraudEvaluationJpaRepository;

/**
 * Application service that loads persisted fraud evaluations and maps them into API-facing response shapes.
 */
@Service
public class FraudEvaluationRetrievalService {

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
	public Optional<FraudEvaluationResponseDto> findById(UUID evaluationId) {
		return fraudEvaluationJpaRepository.findById(evaluationId)
			.map(fraudEvaluationPersistenceMapper::toDomain)
			.map(fraudEvaluationApplicationMapper::toResponse);
	}

	/**
	 * Loads persisted evaluations using the Phase 1 filter set and maps them into summary response DTOs.
	 *
	 * @param decision optional decision filter
	 * @param accountId optional account filter
	 * @param from optional time-range start
	 * @param to optional time-range end
	 * @return summary response DTOs
	 */
	public List<FraudEvaluationSummaryResponseDto> findSummaries(
		FraudDecision decision,
		String accountId,
		OffsetDateTime from,
		OffsetDateTime to
	) {
		return findDomainEvaluations(decision, accountId, from, to).stream()
			.map(fraudEvaluationApplicationMapper::toSummaryResponse)
			.toList();
	}

	private List<FraudEvaluation> findDomainEvaluations(
		FraudDecision decision,
		String accountId,
		OffsetDateTime from,
		OffsetDateTime to
	) {
		if (decision != null && accountId != null && from != null && to != null) {
			return fraudEvaluationJpaRepository.findByDecisionAndAccountIdAndEvaluatedAtBetween(decision, accountId, from, to)
				.stream()
				.map(fraudEvaluationPersistenceMapper::toDomain)
				.toList();
		}

		if (decision != null && accountId != null) {
			return fraudEvaluationJpaRepository.findByDecisionAndAccountId(decision, accountId)
				.stream()
				.map(fraudEvaluationPersistenceMapper::toDomain)
				.toList();
		}

		if (decision != null && from != null && to != null) {
			return fraudEvaluationJpaRepository.findByDecisionAndEvaluatedAtBetween(decision, from, to)
				.stream()
				.map(fraudEvaluationPersistenceMapper::toDomain)
				.toList();
		}

		if (accountId != null && from != null && to != null) {
			return fraudEvaluationJpaRepository.findByAccountIdAndEvaluatedAtBetween(accountId, from, to)
				.stream()
				.map(fraudEvaluationPersistenceMapper::toDomain)
				.toList();
		}

		if (decision != null) {
			return fraudEvaluationJpaRepository.findByDecision(decision)
				.stream()
				.map(fraudEvaluationPersistenceMapper::toDomain)
				.toList();
		}

		if (accountId != null) {
			return fraudEvaluationJpaRepository.findByAccountId(accountId)
				.stream()
				.map(fraudEvaluationPersistenceMapper::toDomain)
				.toList();
		}

		if (from != null && to != null) {
			return fraudEvaluationJpaRepository.findByEvaluatedAtBetween(from, to)
				.stream()
				.map(fraudEvaluationPersistenceMapper::toDomain)
				.toList();
		}

		return fraudEvaluationJpaRepository.findAll().stream()
			.map(fraudEvaluationPersistenceMapper::toDomain)
			.toList();
	}
}
