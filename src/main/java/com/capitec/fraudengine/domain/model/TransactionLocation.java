package com.capitec.fraudengine.domain.model;

public record TransactionLocation(
	String countryCode,
	String city
) {
}
