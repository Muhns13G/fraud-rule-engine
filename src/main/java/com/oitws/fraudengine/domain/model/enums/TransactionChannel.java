package com.oitws.fraudengine.domain.model.enums;

/**
 * Normalized channel through which a transaction was initiated.
 */
public enum TransactionChannel {
	CARD_PRESENT,
	ONLINE,
	ATM,
	TRANSFER,
	MOBILE_APP,
	OTHER
}
