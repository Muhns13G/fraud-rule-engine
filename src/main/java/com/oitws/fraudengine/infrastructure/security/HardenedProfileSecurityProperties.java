package com.oitws.fraudengine.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration contract for Phase 5 hardened-profile token identity strategy.
 *
 * <p>This is contract-only in Sprint 5.1.1. Runtime enforcement is introduced in Sprint 5.1.2.
 */
@ConfigurationProperties(prefix = "app.security.hardened-profile")
public class HardenedProfileSecurityProperties {

	private String authMechanism;
	private String issuerUri;
	private String jwkSetUri;
	private String audience;
	private String principalClaim;
	private String rolesClaim;
	private int clockSkewSeconds;

	public String getAuthMechanism() {
		return authMechanism;
	}

	public void setAuthMechanism(String authMechanism) {
		this.authMechanism = authMechanism;
	}

	public String getIssuerUri() {
		return issuerUri;
	}

	public void setIssuerUri(String issuerUri) {
		this.issuerUri = issuerUri;
	}

	public String getJwkSetUri() {
		return jwkSetUri;
	}

	public void setJwkSetUri(String jwkSetUri) {
		this.jwkSetUri = jwkSetUri;
	}

	public String getAudience() {
		return audience;
	}

	public void setAudience(String audience) {
		this.audience = audience;
	}

	public String getPrincipalClaim() {
		return principalClaim;
	}

	public void setPrincipalClaim(String principalClaim) {
		this.principalClaim = principalClaim;
	}

	public String getRolesClaim() {
		return rolesClaim;
	}

	public void setRolesClaim(String rolesClaim) {
		this.rolesClaim = rolesClaim;
	}

	public int getClockSkewSeconds() {
		return clockSkewSeconds;
	}

	public void setClockSkewSeconds(int clockSkewSeconds) {
		this.clockSkewSeconds = clockSkewSeconds;
	}
}
