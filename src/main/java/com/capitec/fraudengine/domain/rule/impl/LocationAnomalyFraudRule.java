package com.capitec.fraudengine.domain.rule.impl;

import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.capitec.fraudengine.domain.model.RuleEvaluationResult;
import com.capitec.fraudengine.domain.model.TransactionEvent;
import com.capitec.fraudengine.domain.model.TransactionLocation;
import com.capitec.fraudengine.domain.model.enums.RuleSeverity;
import com.capitec.fraudengine.domain.rule.AbstractFraudRule;
import com.capitec.fraudengine.domain.rule.FraudRuleContext;
import com.capitec.fraudengine.infrastructure.config.FraudRuleProperties;

/**
 * Flags transactions when the current location differs materially from the most recent comparable transaction.
 */
@Component
public class LocationAnomalyFraudRule extends AbstractFraudRule {

	private final int scoreContribution;
	private final boolean compareCityWhenCountryMatches;

	public LocationAnomalyFraudRule(FraudRuleProperties fraudRuleProperties) {
		super("LOCATION_ANOMALY", "Location Anomaly Rule");
		this.scoreContribution = fraudRuleProperties.getLocationAnomaly().getScoreContribution();
		this.compareCityWhenCountryMatches = fraudRuleProperties.getLocationAnomaly().isCompareCityWhenCountryMatches();
	}

	@Override
	public RuleEvaluationResult evaluate(FraudRuleContext context) {
		TransactionEvent currentTransaction = context.transactionEvent();
		TransactionLocation currentLocation = currentTransaction.location();
		if (!hasComparableLocation(currentLocation)) {
			return result(false, RuleSeverity.INFO, 0, "Current transaction location is missing; location anomaly was not evaluated.");
		}

		Optional<TransactionEvent> previousComparableTransaction = context.recentTransactions().stream()
			.filter(previous -> !previous.transactionId().equals(currentTransaction.transactionId()))
			.filter(previous -> hasComparableLocation(previous.location()))
			.max(Comparator.comparing(TransactionEvent::eventTimestamp));

		if (previousComparableTransaction.isEmpty()) {
			return result(false, RuleSeverity.INFO, 0, "No prior comparable transaction location was available for anomaly comparison.");
		}

		TransactionEvent previousTransaction = previousComparableTransaction.get();
		TransactionLocation previousLocation = previousTransaction.location();

		String currentCountry = normalize(currentLocation.countryCode());
		String previousCountry = normalize(previousLocation.countryCode());
		if (!currentCountry.equals(previousCountry)) {
			return result(
				true,
				RuleSeverity.REVIEW,
				scoreContribution,
				"Location country changed from " + previousCountry + " to " + currentCountry
					+ " compared with transaction " + previousTransaction.transactionId() + "."
			);
		}

		if (compareCityWhenCountryMatches) {
			String currentCity = normalize(currentLocation.city());
			String previousCity = normalize(previousLocation.city());
			if (!currentCity.equals(previousCity)) {
				return result(
					true,
					RuleSeverity.REVIEW,
					scoreContribution,
					"Location city changed from " + previousCity + " to " + currentCity
						+ " within country " + currentCountry
						+ " compared with transaction " + previousTransaction.transactionId() + "."
				);
			}
		}

		return result(false, RuleSeverity.INFO, 0, "Transaction location matches the most recent comparable transaction.");
	}

	private boolean hasComparableLocation(TransactionLocation location) {
		if (location == null) {
			return false;
		}
		return !normalize(location.countryCode()).isBlank() && !normalize(location.city()).isBlank();
	}

	private String normalize(String value) {
		return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
	}
}
