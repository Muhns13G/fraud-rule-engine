package com.capitec.fraudengine.api.error;

import java.time.OffsetDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Consistent API error payload returned by controller exception handling.
 *
 * @param timestamp point in time when the error was produced
 * @param status HTTP status code
 * @param error HTTP error reason phrase
 * @param message high-level error description
 * @param details field-level or validation details when available
 */
public record ApiErrorResponse(
	@Schema(description = "Point in time when the error was produced.", example = "2026-05-12T10:00:01+02:00")
	OffsetDateTime timestamp,
	@Schema(description = "HTTP status code.", example = "400")
	int status,
	@Schema(description = "HTTP reason phrase.", example = "Bad Request")
	String error,
	@Schema(description = "High-level error description.", example = "Validation failed for the supplied request.")
	String message,
	@Schema(description = "Field-level or request-level validation details.", example = "[\"amount: must be greater than or equal to 0.01\"]")
	List<String> details
) {
}
