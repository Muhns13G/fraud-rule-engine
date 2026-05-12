package com.capitec.fraudengine.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.capitec.fraudengine.api.dto.FraudEvaluationResponseDto;
import com.capitec.fraudengine.api.dto.FraudEvaluationSummaryResponseDto;
import com.capitec.fraudengine.application.mapper.FraudEvaluationApplicationMapper;
import com.capitec.fraudengine.domain.model.FraudEvaluation;

/**
 * Application service that maps domain fraud evaluations into API-facing response shapes.
 */
@Service
public class FraudEvaluationRetrievalService {

	private final FraudEvaluationApplicationMapper fraudEvaluationApplicationMapper;

	public FraudEvaluationRetrievalService(FraudEvaluationApplicationMapper fraudEvaluationApplicationMapper) {
		this.fraudEvaluationApplicationMapper = fraudEvaluationApplicationMapper;
	}

	/**
	 * Maps a domain evaluation aggregate into the detailed response view.
	 *
	 * @param evaluation domain evaluation aggregate
	 * @return detailed response DTO
	 */
	public FraudEvaluationResponseDto toResponse(FraudEvaluation evaluation) {
		return fraudEvaluationApplicationMapper.toResponse(evaluation);
	}

	/**
	 * Maps a list of domain evaluation aggregates into the lightweight list response view.
	 *
	 * @param evaluations domain evaluations to summarize
	 * @return summary response DTOs
	 */
	public List<FraudEvaluationSummaryResponseDto> toSummaryResponses(List<FraudEvaluation> evaluations) {
		return evaluations.stream()
			.map(fraudEvaluationApplicationMapper::toSummaryResponse)
			.toList();
	}
}
