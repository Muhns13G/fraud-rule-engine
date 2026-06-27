package com.oitws.fraudengine.infrastructure.security;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * External-manager adapter that resolves secure-profile credentials from an environment-backed secret payload.
 *
 * <p>Supported secret reference formats:
 * <ul>
 *   <li>{@code env:SECRET_ENV_VAR}</li>
 *   <li>{@code env://SECRET_ENV_VAR}</li>
 * </ul>
 *
 * <p>Supported payload formats:
 * <ul>
 *   <li>JSON object: {@code {"username":"u","password":"p"}} or {@code {"username":"u","passwordEncoded":"..."}}</li>
 *   <li>Key-value lines: {@code username=u;password=p} (also supports newline-separated entries)</li>
 * </ul>
 */
public class EnvExternalManagerSecretSupplier implements SecureProfileSecretSupplier {

	private static final TypeReference<Map<String, String>> STRING_MAP = new TypeReference<>() { };
	private final Environment environment;
	private final ObjectMapper objectMapper;

	public EnvExternalManagerSecretSupplier(Environment environment, ObjectMapper objectMapper) {
		this.environment = environment;
		this.objectMapper = objectMapper;
	}

	@Override
	public SecureProfileResolvedSecrets resolve(String externalSecretRef) {
		String reference = normalize(externalSecretRef);
		if (reference == null) {
			throw new IllegalStateException(
				"External secret reference is required for env external-manager adapter."
			);
		}

		String secretEnvVar = parseEnvVarName(reference);
		String payload = normalize(environment.getProperty(secretEnvVar));
		if (payload == null) {
			throw new IllegalStateException(
				"External secret payload environment variable '" + secretEnvVar + "' is missing or empty."
			);
		}

		Map<String, String> secrets = parseSecretsPayload(payload, secretEnvVar);
		String username = firstNonBlank(secrets.get("username"), secrets.get("user"));
		String password = firstNonBlank(secrets.get("password"), secrets.get("rawPassword"), secrets.get("raw_password"));
		String passwordEncoded = firstNonBlank(
			secrets.get("passwordEncoded"),
			secrets.get("password-encoded"),
			secrets.get("password_encoded")
		);

		return new SecureProfileResolvedSecrets(username, password, passwordEncoded);
	}

	private static String parseEnvVarName(String reference) {
		String envVar;
		if (reference.startsWith("env://")) {
			envVar = normalize(reference.substring("env://".length()));
		}
		else if (reference.startsWith("env:")) {
			envVar = normalize(reference.substring("env:".length()));
		}
		else {
			throw new IllegalStateException(
				"Unsupported external-secret-ref '" + reference + "'. Expected env:VAR or env://VAR."
			);
		}

		if (envVar == null) {
			throw new IllegalStateException(
				"External secret reference must include an environment variable name."
			);
		}
		return envVar;
	}

	private Map<String, String> parseSecretsPayload(String payload, String secretEnvVar) {
		if (payload.startsWith("{")) {
			try {
				Map<String, String> parsed = objectMapper.readValue(payload, STRING_MAP);
				return parsed != null ? parsed : Map.of();
			}
			catch (Exception ex) {
				throw new IllegalStateException(
					"Failed to parse JSON secret payload from environment variable '" + secretEnvVar + "'.",
					ex
				);
			}
		}
		return parseKeyValuePayload(payload);
	}

	private static Map<String, String> parseKeyValuePayload(String payload) {
		Map<String, String> values = new HashMap<>();
		String normalizedPayload = payload.replace('\n', ';');
		String[] entries = normalizedPayload.split(";");
		for (String entry : entries) {
			String trimmed = normalize(entry);
			if (trimmed == null) {
				continue;
			}
			int separatorIndex = trimmed.indexOf('=');
			if (separatorIndex <= 0 || separatorIndex == trimmed.length() - 1) {
				continue;
			}

			String key = normalize(trimmed.substring(0, separatorIndex));
			String value = normalize(trimmed.substring(separatorIndex + 1));
			if (key != null && value != null) {
				values.put(key, value);
			}
		}
		return values;
	}

	private static String firstNonBlank(String... values) {
		for (String value : values) {
			String normalized = normalize(value);
			if (normalized != null) {
				return normalized;
			}
		}
		return null;
	}

	private static String normalize(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		return value.trim();
	}
}
