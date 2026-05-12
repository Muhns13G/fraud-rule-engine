package com.capitec.fraudengine.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.capitec.fraudengine.domain.model.enums.MerchantCategory;
import com.capitec.fraudengine.domain.model.enums.TransactionChannel;
import com.capitec.fraudengine.domain.model.enums.TransactionType;

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
