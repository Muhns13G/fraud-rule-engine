package com.capitec.fraudengine.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
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
	@Schema(description = "Caller-supplied transaction identifier.", example = "txn-20260512-0001")
	@NotBlank
	@Size(max = 100)
	String transactionId,

	@Schema(description = "Account associated with the transaction.", example = "account-123")
	@NotBlank
	@Size(max = 100)
	String accountId,

	@Schema(description = "Customer associated with the account.", example = "customer-456")
	@NotBlank
	@Size(max = 100)
	String customerId,

	@Schema(description = "Transaction amount in the supplied currency.", example = "26000.00")
	@NotNull
	@DecimalMin(value = "0.01")
	@Digits(integer = 15, fraction = 2)
	BigDecimal amount,

	@Schema(description = "ISO-style uppercase currency code.", example = "ZAR")
	@NotBlank
	@Size(min = 3, max = 3)
	String currency,

	@Schema(description = "Merchant identifier from the source system.", example = "merchant-123")
	@NotBlank
	@Size(max = 100)
	String merchantId,

	@Schema(description = "Categorized merchant type used for rule evaluation.", example = "RETAIL")
	@NotBlank
	@Size(max = 100)
	String merchantCategory,

	@Schema(description = "Normalized transaction type used for rule evaluation.", example = "PURCHASE")
	@NotBlank
	@Size(max = 100)
	String transactionType,

	@Schema(description = "Normalized transaction channel used for rule evaluation.", example = "ONLINE")
	@NotBlank
	@Size(max = 100)
	String channel,

	@Schema(description = "Point in time when the transaction occurred.", example = "2026-05-12T10:00:00+02:00")
	@NotNull
	OffsetDateTime eventTimestamp,

	@Schema(description = "Optional location fragment supplied with the transaction.")
	@Valid
	LocationDto location,

	@Schema(description = "Optional caller reference for traceability.", example = "checkout-789")
	@Size(max = 255)
	String reference
) {
}
