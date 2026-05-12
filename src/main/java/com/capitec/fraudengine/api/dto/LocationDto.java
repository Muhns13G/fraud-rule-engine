package com.capitec.fraudengine.api.dto;

import jakarta.validation.constraints.Size;

/**
 * Optional location fragment supplied with a transaction event.
 *
 * @param countryCode ISO-style country code when available
 * @param city human-readable city name when available
 */
public record LocationDto(
	@Size(min = 2, max = 3) String countryCode,
	@Size(max = 120) String city
) {
}
