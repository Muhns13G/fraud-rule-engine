package com.capitec.fraudengine.application.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.capitec.fraudengine.infrastructure.config.FraudRuleProperties;

/**
 * Exposes active configured rule thresholds/windows in a governance-friendly read shape.
 */
@Service
public class RuleGovernanceConfigurationReadModelService {

	private final FraudRuleProperties fraudRuleProperties;

	public RuleGovernanceConfigurationReadModelService(FraudRuleProperties fraudRuleProperties) {
		this.fraudRuleProperties = fraudRuleProperties;
	}

	public String describe(String ruleCode) {
		return switch (ruleCode) {
			case "HIGH_AMOUNT" -> "reviewThreshold=" + fraudRuleProperties.getHighAmount().getReviewThreshold().toPlainString()
				+ ", blockThreshold=" + fraudRuleProperties.getHighAmount().getBlockThreshold().toPlainString();
			case "VELOCITY" -> "thresholdCount=" + fraudRuleProperties.getVelocity().getThresholdCount()
				+ ", windowMinutes=" + fraudRuleProperties.getVelocity().getWindowMinutes();
			case "RISKY_MERCHANT_CATEGORY" -> "flaggedCategories=" + fraudRuleProperties.getRiskyMerchantCategory()
				.getFlaggedCategories()
				.stream()
				.map(Enum::name)
				.collect(Collectors.joining("|"));
			case "UNUSUAL_TIME" -> "start=" + fraudRuleProperties.getUnusualTime().getStart()
				+ ", end=" + fraudRuleProperties.getUnusualTime().getEnd();
			case "LOCATION_ANOMALY" -> "scoreContribution=" + fraudRuleProperties.getLocationAnomaly().getScoreContribution()
				+ ", compareCityWhenCountryMatches=" + fraudRuleProperties.getLocationAnomaly().isCompareCityWhenCountryMatches();
			default -> "No active configuration mapping available for this rule code.";
		};
	}
}
