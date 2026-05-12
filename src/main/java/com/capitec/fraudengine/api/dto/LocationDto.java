package com.capitec.fraudengine.api.dto;

import jakarta.validation.constraints.Size;

public record LocationDto(
	@Size(min = 2, max = 3) String countryCode,
	@Size(max = 120) String city
) {
}
