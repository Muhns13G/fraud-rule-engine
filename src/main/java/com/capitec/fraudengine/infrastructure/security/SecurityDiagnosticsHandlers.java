package com.capitec.fraudengine.infrastructure.security;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.capitec.fraudengine.infrastructure.config.RequestCorrelationFilter;

/**
 * Emits structured, non-sensitive diagnostics for security denial outcomes.
 */
@Component
public class SecurityDiagnosticsHandlers {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityDiagnosticsHandlers.class);
	private final MeterRegistry meterRegistry;

	public SecurityDiagnosticsHandlers(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	/**
	 * Builds the authentication entry point for unauthenticated request handling.
	 *
	 * @return authentication entry point that emits structured denial diagnostics
	 */
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return this::handleUnauthorized;
	}

	/**
	 * Builds the access denied handler for authenticated-but-unauthorized requests.
	 *
	 * @return access denied handler that emits structured denial diagnostics
	 */
	public AccessDeniedHandler accessDeniedHandler() {
		return this::handleAccessDenied;
	}

	private void handleUnauthorized(
		HttpServletRequest request,
		HttpServletResponse response,
		org.springframework.security.core.AuthenticationException exception
	) throws IOException {
		String requestId = requestId(request);
		String path = request.getRequestURI();
		String method = request.getMethod();
		String outcome = "unauthorized";

		LOGGER.warn(
			"security_authn_denied requestId={} method={} path={} outcome={} reason={}",
			requestId,
			method,
			path,
			outcome,
			exception.getClass().getSimpleName()
		);

		meterRegistry.counter(
			"fraud.security.authn.denied.total",
			"outcome",
			outcome,
			"method",
			method
		).increment();

		response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
	}

	private void handleAccessDenied(
		HttpServletRequest request,
		HttpServletResponse response,
		AccessDeniedException exception
	) throws IOException {
		String requestId = requestId(request);
		String path = request.getRequestURI();
		String method = request.getMethod();
		Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext()
			.getAuthentication();
		String principalType = authentication == null || authentication.getPrincipal() == null
			? "unknown"
			: authentication.getPrincipal().getClass().getSimpleName();
		String outcome = "access_denied";

		LOGGER.warn(
			"security_authz_denied requestId={} method={} path={} outcome={} principalType={} reason={}",
			requestId,
			method,
			path,
			outcome,
			principalType,
			exception.getClass().getSimpleName()
		);

		meterRegistry.counter(
			"fraud.security.authz.denied.total",
			"outcome",
			outcome,
			"method",
			method
		).increment();

		response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase());
	}

	private static String requestId(HttpServletRequest request) {
		String requestId = request.getHeader(RequestCorrelationFilter.REQUEST_ID_HEADER);
		return requestId == null || requestId.isBlank() ? "missing" : requestId;
	}
}
