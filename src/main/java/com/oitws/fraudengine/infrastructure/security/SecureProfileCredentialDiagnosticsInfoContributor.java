package com.oitws.fraudengine.infrastructure.security;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Adds redacted secure-profile credential diagnostics to actuator info.
 */
@Component
@Profile("secure")
public class SecureProfileCredentialDiagnosticsInfoContributor implements InfoContributor {

	private final SecureProfileSecurityProperties properties;
	private final Environment environment;

	public SecureProfileCredentialDiagnosticsInfoContributor(
		SecureProfileSecurityProperties properties,
		Environment environment
	) {
		this.properties = properties;
		this.environment = environment;
	}

	@Override
	public void contribute(Info.Builder builder) {
		builder.withDetail(
			"secureCredentialDiagnostics",
			SecureProfileCredentialDiagnostics.build(properties, environment)
		);
	}
}
