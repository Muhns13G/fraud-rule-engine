package com.capitec.fraudengine.api.error;

import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.capitec.fraudengine.common.error.InvalidRequestValueException;

/**
 * Centralized exception handling for API validation and request failures.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * Handles bean validation failures for request bodies.
	 *
	 * @param exception validation exception
	 * @return structured bad-request response
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
		List<String> details = exception.getBindingResult().getFieldErrors().stream()
			.map(this::formatFieldError)
			.toList();

		LOGGER.warn(
			"request_validation_failed fieldErrorCount={} details={}",
			details.size(),
			details
		);

		return buildResponse(
			HttpStatus.BAD_REQUEST,
			"Validation failed for the supplied request.",
			details
		);
	}

	/**
	 * Handles enum and parameter conversion failures for path or query values.
	 *
	 * @param exception conversion exception
	 * @return structured bad-request response
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
		String detail = "Invalid value '" + exception.getValue() + "' for parameter '" + exception.getName() + "'.";
		LOGGER.warn(
			"request_parameter_parse_failed parameter={} value={} requiredType={}",
			exception.getName(),
			exception.getValue(),
			exception.getRequiredType() != null ? exception.getRequiredType().getSimpleName() : "unknown"
		);
		return buildResponse(HttpStatus.BAD_REQUEST, "Request parameter could not be parsed.", List.of(detail));
	}

	/**
	 * Handles invalid request-body values that fail enum or domain normalization.
	 *
	 * @param exception invalid request value exception
	 * @return structured bad-request response
	 */
	@ExceptionHandler(InvalidRequestValueException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidRequestValue(InvalidRequestValueException exception) {
		LOGGER.warn("request_payload_value_rejected reason={}", exception.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, "Request payload contains an unsupported value.", List.of(exception.getMessage()));
	}

	/**
	 * Handles missing fraud evaluation identifiers.
	 *
	 * @param exception missing-resource exception
	 * @return structured not-found response
	 */
	@ExceptionHandler(FraudEvaluationNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleFraudEvaluationNotFound(FraudEvaluationNotFoundException exception) {
		LOGGER.info("fraud_evaluation_not_found message={}", exception.getMessage());
		return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), List.of());
	}

	/**
	 * Handles unexpected uncaught exceptions at the API boundary.
	 *
	 * @param exception unexpected exception
	 * @return structured internal-server-error response
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGenericException(Exception exception) {
		LOGGER.error("request_processing_failed errorType={} message={}", exception.getClass().getSimpleName(), exception.getMessage(), exception);
		return buildResponse(
			HttpStatus.INTERNAL_SERVER_ERROR,
			"An unexpected error occurred while processing the request.",
			List.of()
		);
	}

	private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, List<String> details) {
		return ResponseEntity.status(status).body(
			new ApiErrorResponse(
				OffsetDateTime.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				details
			)
		);
	}

	private String formatFieldError(FieldError fieldError) {
		return fieldError.getField() + ": " + fieldError.getDefaultMessage();
	}
}
