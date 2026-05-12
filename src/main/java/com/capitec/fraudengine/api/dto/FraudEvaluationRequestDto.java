package com.capitec.fraudengine.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request contract for evaluating a single categorized transaction event.
 *
 * @param transactionId caller-supplied transaction identifier
 * @param accountId account associated with the transaction
 * @param customerId customer associated with the account
 * @param amount transaction amount in the supplied currency
 * @param currency ISO-style uppercase currency code
 * @param merchantId merchant identifier from the source system
 * @param merchantCategory merchant category value for rule evaluation
 * @param transactionType transaction type value for rule evaluation
 * @param channel transaction channel value for rule evaluation
 * @param eventTimestamp point in time when the transaction occurred
 * @param location optional location details
 * @param reference optional caller reference for traceability
 */
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
