package com.capitec.fraudengine.infrastructure.config;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Adds a lightweight request correlation identifier to the logging context and response headers.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestCorrelationFilter extends OncePerRequestFilter {

	public static final String REQUEST_ID_HEADER = "X-Request-Id";
	public static final String REQUEST_ID_MDC_KEY = "requestId";
	private static final int MAX_REQUEST_ID_LENGTH = 64;
	private static final Pattern UUID_REQUEST_ID_PATTERN = Pattern.compile(
		"^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"
	);

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String requestId = resolveRequestId(request);
		MDC.put(REQUEST_ID_MDC_KEY, requestId);
		response.setHeader(REQUEST_ID_HEADER, requestId);

		try {
			filterChain.doFilter(request, response);
		}
		finally {
			MDC.remove(REQUEST_ID_MDC_KEY);
		}
	}

	private String resolveRequestId(HttpServletRequest request) {
		String incomingRequestId = request.getHeader(REQUEST_ID_HEADER);
		if (incomingRequestId != null && !incomingRequestId.isBlank()) {
			String candidateRequestId = incomingRequestId.trim();
			if (candidateRequestId.length() <= MAX_REQUEST_ID_LENGTH
				&& UUID_REQUEST_ID_PATTERN.matcher(candidateRequestId).matches()) {
				return candidateRequestId.toLowerCase(Locale.ROOT);
			}
		}

		return UUID.randomUUID().toString();
	}
}
