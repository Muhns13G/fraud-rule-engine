package com.oitws.fraudengine.api.error;

import java.time.OffsetDateTime;
import java.util.List;

import io.micrometer.core.instrument.MeterRegistry;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.oitws.fraudengine.common.error.InvalidRuleGovernanceStateException;
import com.oitws.fraudengine.common.error.InvalidRequestValueException;

/**
 * Centralized exception handling for API validation and request failures.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	private final MeterRegistry meterRegistry;

	public GlobalExceptionHandler(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

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
		recordErrorMetric(HttpStatus.BAD_REQUEST, exception);

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
		recordErrorMetric(HttpStatus.BAD_REQUEST, exception);
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
		recordErrorMetric(HttpStatus.BAD_REQUEST, exception);
		return buildResponse(HttpStatus.BAD_REQUEST, "Request payload contains an unsupported value.", List.of(exception.getMessage()));
	}

	/**
	 * Handles request-body parse failures (for example malformed JSON or datetime format mismatches).
	 *
	 * @param exception request-body parse exception
	 * @return structured bad-request response
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
		if (isEventTimestampParseError(exception)) {
			String detail = "eventTimestamp must be an ISO-8601 datetime with timezone offset (for example 2026-05-12T10:00:00+02:00 or 2026-05-12T08:00:00Z).";
			LOGGER.warn("request_payload_parse_failed field=eventTimestamp reason={}", exception.getMostSpecificCause().getMessage());
			recordErrorMetric(HttpStatus.BAD_REQUEST, exception);
			return buildResponse(HttpStatus.BAD_REQUEST, "Request payload could not be parsed.", List.of(detail));
		}

		LOGGER.warn("request_payload_parse_failed reason={}", exception.getMostSpecificCause().getMessage());
		recordErrorMetric(HttpStatus.BAD_REQUEST, exception);
		return buildResponse(
			HttpStatus.BAD_REQUEST,
			"Request payload could not be parsed.",
			List.of(
				"Malformed request payload.",
				"eventTimestamp must be an ISO-8601 datetime with timezone offset (for example 2026-05-12T10:00:00+02:00 or 2026-05-12T08:00:00Z)."
			)
		);
	}

	private boolean isEventTimestampParseError(HttpMessageNotReadableException exception) {
		if (exception.getCause() instanceof InvalidFormatException invalidFormatException
			&& invalidFormatException.getPath() != null
			&& invalidFormatException.getPath().stream().anyMatch(reference -> "eventTimestamp".equals(reference.getFieldName()))) {
			return true;
		}

		String mostSpecificMessage = exception.getMostSpecificCause() != null ? exception.getMostSpecificCause().getMessage() : "";
		String message = exception.getMessage() != null ? exception.getMessage() : "";
		return mostSpecificMessage.contains("eventTimestamp")
			|| message.contains("eventTimestamp");
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
		recordErrorMetric(HttpStatus.NOT_FOUND, exception);
		return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), List.of());
	}

	/**
	 * Handles missing rule governance metadata identities.
	 *
	 * @param exception missing-resource exception
	 * @return structured not-found response
	 */
	@ExceptionHandler(RuleGovernanceMetadataNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleRuleGovernanceMetadataNotFound(
		RuleGovernanceMetadataNotFoundException exception
	) {
		LOGGER.info("rule_governance_metadata_not_found message={}", exception.getMessage());
		recordErrorMetric(HttpStatus.NOT_FOUND, exception);
		return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), List.of());
	}

	/**
	 * Handles missing rule-governance metadata for a supplied rule code.
	 *
	 * @param exception missing-rule-code exception
	 * @return structured not-found response
	 */
	@ExceptionHandler(RuleGovernanceRuleCodeNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleRuleGovernanceRuleCodeNotFound(
		RuleGovernanceRuleCodeNotFoundException exception
	) {
		LOGGER.info("rule_governance_rule_code_not_found message={}", exception.getMessage());
		recordErrorMetric(HttpStatus.NOT_FOUND, exception);
		return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), List.of());
	}

	/**
	 * Handles deterministic rule-governance state validation failures.
	 *
	 * @param exception invalid governance state exception
	 * @return structured bad-request response
	 */
	@ExceptionHandler(InvalidRuleGovernanceStateException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidRuleGovernanceState(
		InvalidRuleGovernanceStateException exception
	) {
		LOGGER.warn("rule_governance_state_rejected reason={}", exception.getMessage());
		recordErrorMetric(HttpStatus.BAD_REQUEST, exception);
		return buildResponse(
			HttpStatus.BAD_REQUEST,
			"Rule governance state transition is invalid.",
			List.of(exception.getMessage())
		);
	}

	/**
	 * Handles requests for unmapped or disabled static/resource endpoints.
	 *
	 * @param exception missing-resource exception
	 * @return structured not-found response
	 */
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNoResourceFound(NoResourceFoundException exception) {
		LOGGER.info("resource_not_found message={}", exception.getMessage());
		recordErrorMetric(HttpStatus.NOT_FOUND, exception);
		return buildResponse(HttpStatus.NOT_FOUND, "Requested resource was not found.", List.of());
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
		recordErrorMetric(HttpStatus.INTERNAL_SERVER_ERROR, exception);
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

	private void recordErrorMetric(HttpStatus status, Exception exception) {
		meterRegistry.counter(
			"fraud.api.error.total",
			"status",
			Integer.toString(status.value()),
			"exception",
			exception.getClass().getSimpleName()
		).increment();
	}
}
