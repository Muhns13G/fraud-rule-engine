package com.oitws.fraudengine.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.oitws.fraudengine.domain.model.enums.MerchantCategory;
import com.oitws.fraudengine.domain.model.enums.TransactionChannel;
import com.oitws.fraudengine.domain.model.enums.TransactionType;

/**
 * Core domain input for evaluating a single transaction for fraud risk.
 *
 * @param transactionId source transaction identifier
 * @param accountId account associated with the transaction
 * @param customerId customer associated with the account
 * @param amount transaction amount
 * @param currency ISO-style uppercase currency code
 * @param merchantId merchant identifier from the source system
 * @param merchantCategory normalized merchant category
 * @param transactionType normalized transaction type
 * @param channel normalized transaction channel
 * @param eventTimestamp point in time when the transaction occurred
 * @param location optional location details
 * @param reference optional caller reference
 */
public record TransactionEvent(
	String transactionId,
	String accountId,
	String customerId,
	BigDecimal amount,
	String currency,
	String merchantId,
	MerchantCategory merchantCategory,
	TransactionType transactionType,
	TransactionChannel channel,
	OffsetDateTime eventTimestamp,
	TransactionLocation location,
	String reference
) {
}
