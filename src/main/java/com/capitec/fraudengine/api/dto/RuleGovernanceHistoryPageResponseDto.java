package com.capitec.fraudengine.api.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Paged response for governance lifecycle history trail queries.
 */
public record RuleGovernanceHistoryPageResponseDto(
	@Schema(description = "Governance history items for the current page.")
	List<RuleGovernanceHistoryResponseDto> content,
	@Schema(description = "Zero-based current page index.", example = "0")
	int page,
	@Schema(description = "Requested page size.", example = "20")
	int size,
	@Schema(description = "Total number of matching elements.", example = "42")
	long totalElements,
	@Schema(description = "Total number of pages for the current query.", example = "3")
	int totalPages
) {
}
