package com.capitec.fraudengine.api.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capitec.fraudengine.api.dto.RuleGovernanceMetadataResponseDto;
import com.capitec.fraudengine.api.dto.RuleGovernanceStateTransitionRequestDto;
import com.capitec.fraudengine.api.error.RuleGovernanceMetadataNotFoundException;
import com.capitec.fraudengine.application.service.RuleGovernanceMutationService;
import com.capitec.fraudengine.application.service.RuleGovernanceRetrievalService;
import com.capitec.fraudengine.domain.model.RuleLifecycleState;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Admin-facing read endpoints for governance metadata over code-defined fraud rules.
 */
@RestController
@RequestMapping("/api/admin/rules")
@Tag(name = "Rule Governance", description = "Admin visibility endpoints for governed fraud rule metadata.")
public class RuleGovernanceController {

	private final RuleGovernanceRetrievalService ruleGovernanceRetrievalService;
	private final RuleGovernanceMutationService ruleGovernanceMutationService;

	public RuleGovernanceController(
		RuleGovernanceRetrievalService ruleGovernanceRetrievalService,
		RuleGovernanceMutationService ruleGovernanceMutationService
	) {
		this.ruleGovernanceRetrievalService = ruleGovernanceRetrievalService;
		this.ruleGovernanceMutationService = ruleGovernanceMutationService;
	}

	/**
	 * Lists rule governance metadata entries.
	 *
	 * @param activeOnly whether to return only active rules
	 * @return sorted rule metadata entries
	 */
	@GetMapping
	@Operation(
		summary = "List governed fraud rules",
		description = "Returns rule metadata visibility for governed code-defined rules. By default only active rules are returned."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Rule metadata list retrieved successfully.",
			content = @Content(schema = @Schema(implementation = RuleGovernanceMetadataResponseDto.class))
		),
		@ApiResponse(
			responseCode = "500",
			description = "An unexpected server error occurred.",
			content = @Content(schema = @Schema(implementation = com.capitec.fraudengine.api.error.ApiErrorResponse.class))
		)
	})
	public List<RuleGovernanceMetadataResponseDto> findRules(
		@RequestParam(defaultValue = "true") boolean activeOnly
	) {
		return ruleGovernanceRetrievalService.findRules(activeOnly);
	}

	/**
	 * Retrieves one governed rule metadata entry by rule identity.
	 *
	 * @param ruleCode stable machine-readable rule code
	 * @param version semantic rule version
	 * @return matching rule metadata
	 */
	@GetMapping("/{ruleCode}/versions/{version}")
	@Operation(
		summary = "Get governed rule metadata by identity",
		description = "Returns one governed rule metadata entry using rule code and version."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Rule metadata retrieved successfully.",
			content = @Content(schema = @Schema(implementation = RuleGovernanceMetadataResponseDto.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "No rule metadata exists for the supplied rule code and version.",
			content = @Content(schema = @Schema(implementation = com.capitec.fraudengine.api.error.ApiErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "500",
			description = "An unexpected server error occurred.",
			content = @Content(schema = @Schema(implementation = com.capitec.fraudengine.api.error.ApiErrorResponse.class))
		)
	})
	public RuleGovernanceMetadataResponseDto findRule(
		@PathVariable String ruleCode,
		@PathVariable String version
	) {
		return ruleGovernanceRetrievalService.findRule(ruleCode, version)
			.orElseThrow(() -> new RuleGovernanceMetadataNotFoundException(ruleCode, version));
	}

	/**
	 * Transitions lifecycle and activation state for one governed rule identity.
	 *
	 * @param ruleCode stable machine-readable rule code
	 * @param version semantic rule version
	 * @param request desired lifecycle and activation state
	 * @return updated rule metadata
	 */
	@PatchMapping("/{ruleCode}/versions/{version}/state")
	@Operation(
		summary = "Transition governed rule lifecycle state",
		description = "Applies constrained lifecycle and activation-state transitions for one governed rule version."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Rule metadata state transitioned successfully.",
			content = @Content(schema = @Schema(implementation = RuleGovernanceMetadataResponseDto.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "Supplied state transition violates governance constraints.",
			content = @Content(schema = @Schema(implementation = com.capitec.fraudengine.api.error.ApiErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "No rule metadata exists for the supplied rule code and version.",
			content = @Content(schema = @Schema(implementation = com.capitec.fraudengine.api.error.ApiErrorResponse.class))
		),
		@ApiResponse(
			responseCode = "500",
			description = "An unexpected server error occurred.",
			content = @Content(schema = @Schema(implementation = com.capitec.fraudengine.api.error.ApiErrorResponse.class))
		)
	})
	public RuleGovernanceMetadataResponseDto transitionRuleState(
		@PathVariable String ruleCode,
		@PathVariable String version,
		@Valid @RequestBody RuleGovernanceStateTransitionRequestDto request
	) {
		return ruleGovernanceMutationService.transitionState(
			ruleCode,
			version,
			new RuleLifecycleState(request.lifecycleStatus(), request.activationState())
		);
	}
}
