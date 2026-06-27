package com.oitws.fraudengine.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Optional location fragment supplied with a transaction event.
 *
 * @param countryCode ISO-style country code when available
 * @param city human-readable city name when available
 */
public record LocationDto(
	@Schema(description = "ISO-style country code when available.", example = "ZA")
	@Size(min = 2, max = 3) String countryCode,
	@Schema(description = "Human-readable city name when available.", example = "Cape Town")
	@Size(max = 120) String city
) {
}
