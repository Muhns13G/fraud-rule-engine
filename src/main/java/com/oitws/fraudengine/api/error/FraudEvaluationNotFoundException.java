package com.oitws.fraudengine.api.error;

import java.util.UUID;

/**
 * Thrown when a requested fraud evaluation identifier does not exist.
 */
public class FraudEvaluationNotFoundException extends RuntimeException {

	public FraudEvaluationNotFoundException(UUID evaluationId) {
		super("Fraud evaluation not found: " + evaluationId);
	}
}
