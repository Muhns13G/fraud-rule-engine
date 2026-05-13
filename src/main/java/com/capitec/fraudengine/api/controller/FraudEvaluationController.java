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
import com.capitec.fraudengine.application.service.FraudEvaluationSummarySortOrder;
import com.capitec.fraudengine.domain.model.FraudEvaluation;
import com.capitec.fraudengine.domain.model.enums.FraudDecision;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for evaluating transactions and retrieving stored fraud evaluations.
 */
@RestController
@Validated
@RequestMapping("/api/fraud-evaluations")
@Tag(name = "Fraud Evaluations", description = "Endpoints for evaluating transactions and querying persisted fraud decisions.")
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
	@Operation(
		summary = "Evaluate a transaction for fraud",
		description = "Accepts one categorized transaction event, evaluates the active Phase 1 rule set, persists the result, and returns the final fraud decision with the full rule-hit trail."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "Fraud evaluation created successfully.",
			content = @Content(schema = @Schema(implementation = FraudEvaluationResponseDto.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "The request payload failed validation or could not be parsed.",
			content = @Content(schema = @Schema(implementation = com.capitec.fraudengine.api.error.ApiErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "500",
			description = "An unexpected server error occurred.",
			content = @Content(schema = @Schema(implementation = com.capitec.fraudengine.api.error.ApiErrorResponse.class))
		)
	})
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
	@Operation(
		summary = "Retrieve a fraud evaluation by id",
		description = "Returns one persisted fraud evaluation including the final decision, score, trace summary, and full per-rule results."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Fraud evaluation retrieved successfully.",
			content = @Content(schema = @Schema(implementation = FraudEvaluationResponseDto.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "The supplied evaluation identifier could not be parsed.",
			content = @Content(schema = @Schema(implementation = com.capitec.fraudengine.api.error.ApiErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "No fraud evaluation exists for the supplied identifier.",
			content = @Content(schema = @Schema(implementation = com.capitec.fraudengine.api.error.ApiErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "500",
			description = "An unexpected server error occurred.",
			content = @Content(schema = @Schema(implementation = com.capitec.fraudengine.api.error.ApiErrorResponse.class))
		)
	})
	public FraudEvaluationResponseDto findById(@PathVariable UUID evaluationId) {
		return fraudEvaluationRetrievalService.findById(evaluationId)
			.orElseThrow(() -> new FraudEvaluationNotFoundException(evaluationId));
	}

	/**
	 * Retrieves persisted fraud evaluations using the current review filter set.
	 *
	 * @param decision optional decision filter
	 * @param accountId optional account filter
	 * @param customerId optional customer filter
	 * @param transactionId optional transaction filter
	 * @param sort optional summary sort order
	 * @param from optional time-range start
	 * @param to optional time-range end
	 * @return matching summary responses
	 */
	@GetMapping
	@Operation(
		summary = "List fraud evaluations",
		description = "Returns persisted fraud evaluations using the current review filters: decision, accountId, customerId, transactionId, and evaluatedAt time range."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Fraud evaluation summaries retrieved successfully.",
			content = @Content(schema = @Schema(implementation = FraudEvaluationSummaryResponseDto.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "One or more filter parameters could not be parsed.",
			content = @Content(schema = @Schema(implementation = com.capitec.fraudengine.api.error.ApiErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "500",
			description = "An unexpected server error occurred.",
			content = @Content(schema = @Schema(implementation = com.capitec.fraudengine.api.error.ApiErrorResponse.class))
		)
	})
	public List<FraudEvaluationSummaryResponseDto> findSummaries(
		@Parameter(description = "Optional final fraud decision filter.", example = "REVIEW")
		@RequestParam(required = false) FraudDecision decision,
		@Parameter(description = "Optional account identifier filter.", example = "account-123")
		@RequestParam(required = false) String accountId,
		@Parameter(description = "Optional customer identifier filter.", example = "customer-123")
		@RequestParam(required = false) String customerId,
		@Parameter(description = "Optional transaction identifier filter.", example = "txn-123")
		@RequestParam(required = false) String transactionId,
		@Parameter(description = "Optional summary sort order. Defaults to newest evaluations first.", example = "NEWEST_FIRST")
		@RequestParam(defaultValue = "NEWEST_FIRST") FraudEvaluationSummarySortOrder sort,
		@Parameter(description = "Optional inclusive evaluated-at range start in ISO-8601 format.", example = "2026-05-12T09:00:00+02:00")
		@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime from,
		@Parameter(description = "Optional inclusive evaluated-at range end in ISO-8601 format.", example = "2026-05-12T12:00:00+02:00")
		@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime to
	) {
		return fraudEvaluationRetrievalService.findSummaries(
			decision,
			accountId,
			customerId,
			transactionId,
			sort,
			from,
			to
		);
	}
}
