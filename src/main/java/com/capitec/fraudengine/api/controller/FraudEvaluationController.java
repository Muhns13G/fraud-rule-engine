package com.capitec.fraudengine.api.controller;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capitec.fraudengine.api.dto.FraudEvaluationRequestDto;
import com.capitec.fraudengine.api.dto.FraudEvaluationResponseDto;
import com.capitec.fraudengine.api.dto.FraudEvaluationSummaryResponseDto;
import com.capitec.fraudengine.api.error.FraudEvaluationNotFoundException;
import com.capitec.fraudengine.application.mapper.FraudEvaluationApplicationMapper;
import com.capitec.fraudengine.application.service.FraudEvaluationRetrievalService;
import com.capitec.fraudengine.application.service.FraudEvaluationService;
import com.capitec.fraudengine.domain.model.FraudEvaluation;
import com.capitec.fraudengine.domain.model.enums.FraudDecision;

import jakarta.validation.Valid;

/**
 * REST controller for evaluating transactions and retrieving stored fraud evaluations.
 */
@RestController
@Validated
@RequestMapping("/api/fraud-evaluations")
public class FraudEvaluationController {

	private final FraudEvaluationService fraudEvaluationService;
	private final FraudEvaluationRetrievalService fraudEvaluationRetrievalService;
	private final FraudEvaluationApplicationMapper fraudEvaluationApplicationMapper;

	public FraudEvaluationController(
		FraudEvaluationService fraudEvaluationService,
		FraudEvaluationRetrievalService fraudEvaluationRetrievalService,
		FraudEvaluationApplicationMapper fraudEvaluationApplicationMapper
	) {
		this.fraudEvaluationService = fraudEvaluationService;
		this.fraudEvaluationRetrievalService = fraudEvaluationRetrievalService;
		this.fraudEvaluationApplicationMapper = fraudEvaluationApplicationMapper;
	}

	/**
	 * Evaluates one transaction and persists the resulting fraud evaluation.
	 *
	 * @param request fraud evaluation request payload
	 * @return created evaluation response
	 */
	@PostMapping
	public ResponseEntity<FraudEvaluationResponseDto> evaluate(@Valid @RequestBody FraudEvaluationRequestDto request) {
		FraudEvaluation evaluation = fraudEvaluationService.evaluate(request);
		FraudEvaluationResponseDto response = fraudEvaluationApplicationMapper.toResponse(evaluation);

		return ResponseEntity.created(URI.create("/api/fraud-evaluations/" + response.evaluationId()))
			.body(response);
	}

	/**
	 * Retrieves one persisted fraud evaluation by identifier.
	 *
	 * @param evaluationId persisted evaluation identifier
	 * @return detailed evaluation response
	 */
	@GetMapping("/{evaluationId}")
	public FraudEvaluationResponseDto findById(@PathVariable UUID evaluationId) {
		return fraudEvaluationRetrievalService.findById(evaluationId)
			.orElseThrow(() -> new FraudEvaluationNotFoundException(evaluationId));
	}

	/**
	 * Retrieves persisted fraud evaluations using the Phase 1 filter set.
	 *
	 * @param decision optional decision filter
	 * @param accountId optional account filter
	 * @param from optional time-range start
	 * @param to optional time-range end
	 * @return matching summary responses
	 */
	@GetMapping
	public List<FraudEvaluationSummaryResponseDto> findSummaries(
		@RequestParam(required = false) FraudDecision decision,
		@RequestParam(required = false) String accountId,
		@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime from,
		@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime to
	) {
		return fraudEvaluationRetrievalService.findSummaries(decision, accountId, from, to);
	}
}
