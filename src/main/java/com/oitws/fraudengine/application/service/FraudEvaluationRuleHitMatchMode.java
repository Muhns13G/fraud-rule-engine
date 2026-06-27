package com.oitws.fraudengine.application.service;

/**
 * Contract mode for rule-hit filtering on fraud-evaluation retrieval.
 * <p>
 * ANY means at least one requested rule code must be triggered.
 * ALL means all requested rule codes must be triggered.
 */
public enum FraudEvaluationRuleHitMatchMode {
	ANY,
	ALL
}
