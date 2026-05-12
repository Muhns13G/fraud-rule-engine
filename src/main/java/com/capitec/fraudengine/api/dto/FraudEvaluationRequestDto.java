package com.capitec.fraudengine.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FraudEvaluationRequestDto(
	@NotBlank
	@Size(max = 100)
	String transactionId,

	@NotBlank
	@Size(max = 100)
	String accountId,

	@NotBlank
	@Size(max = 100)
	String customerId,

	@NotNull
	@DecimalMin(value = "0.01")
	@Digits(integer = 15, fraction = 2)
	BigDecimal amount,

	@NotBlank
	@Size(min = 3, max = 3)
	String currency,

	@NotBlank
	@Size(max = 100)
	String merchantId,

	@NotBlank
	@Size(max = 100)
	String merchantCategory,

	@NotBlank
	@Size(max = 100)
	String transactionType,

	@NotBlank
	@Size(max = 100)
	String channel,

	@NotNull
	OffsetDateTime eventTimestamp,

	@Valid
	LocationDto location,

	@Size(max = 255)
	String reference
) {
}
