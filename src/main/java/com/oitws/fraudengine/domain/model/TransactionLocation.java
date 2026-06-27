package com.oitws.fraudengine.domain.model;

/**
 * Domain representation of transaction location details.
 *
 * @param countryCode ISO-style country code when available
 * @param city human-readable city name when available
 */
public record TransactionLocation(
	String countryCode,
	String city
) {
}
