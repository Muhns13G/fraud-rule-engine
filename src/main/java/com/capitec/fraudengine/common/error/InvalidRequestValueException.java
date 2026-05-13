package com.capitec.fraudengine.common.error;

/**
 * Raised when a client-supplied request value cannot be mapped into the expected application representation.
 */
public class InvalidRequestValueException extends RuntimeException {

	public InvalidRequestValueException(String message) {
		super(message);
	}
}
