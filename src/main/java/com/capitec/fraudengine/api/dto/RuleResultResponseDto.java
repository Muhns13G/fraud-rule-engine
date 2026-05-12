package com.capitec.fraudengine.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response view of one fraud rule evaluation result.
 *
 * @param ruleCode stable machine-readable rule identifier
 * @param ruleName human-readable rule name
 * @param triggered whether the rule was triggered
 * @param severity severity assigned by the rule
 * @param scoreContribution score contributed by the rule
 * @param reason explanation for the rule outcome
 */
public record RuleResultResponseDto(
	@Schema(description = "Stable machine-readable rule identifier.", example = "HIGH_AMOUNT")
	String ruleCode,
	@Schema(description = "Human-readable rule name.", example = "High Amount Rule")
	String ruleName,
	@Schema(description = "Whether the rule was triggered.", example = "true")
	boolean triggered,
	@Schema(description = "Severity assigned by the rule.", example = "BLOCK")
	String severity,
	@Schema(description = "Score contributed by the rule.", example = "100")
	int scoreContribution,
	@Schema(description = "Explanation for the rule outcome.", example = "Transaction amount exceeds the block threshold of 25000.00 ZAR.")
	String reason
) {
}
