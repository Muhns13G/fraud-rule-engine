package com.capitec.fraudengine.api.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Paged response for governance metadata read surfaces.
 *
 * @param content metadata items for the current page
 * @param page zero-based current page index
 * @param size requested page size
 * @param totalElements total matching elements across all pages
 * @param totalPages total number of pages for the current query
 */
public record RuleGovernanceMetadataPageResponseDto(
	@Schema(description = "Governance metadata items for the current page.")
	List<RuleGovernanceMetadataResponseDto> content,
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
