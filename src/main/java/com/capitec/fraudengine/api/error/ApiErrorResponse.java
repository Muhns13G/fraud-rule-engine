package com.capitec.fraudengine.api.error;

import java.time.OffsetDateTime;
import java.util.List;

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
	OffsetDateTime timestamp,
	int status,
	String error,
	String message,
	List<String> details
) {
}
