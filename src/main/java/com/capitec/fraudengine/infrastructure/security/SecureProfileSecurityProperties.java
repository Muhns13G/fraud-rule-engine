package com.capitec.fraudengine.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized credentials for the secure runtime profile.
 */
@ConfigurationProperties(prefix = "app.security.secure-profile")
public class SecureProfileSecurityProperties {

	public enum IdentityProvider {
		IN_MEMORY,
		JDBC
	}

	public enum SecretSource {
		ENV,
		PRE_ENCODED,
		EXTERNAL_MANAGER
	}

	public enum RotationPhase {
		PREPARE,
		OVERLAP,
		CUTOVER,
		RETIRE
	}

	private String username;
	private String password;
	private String passwordEncoded;
	private String role;
	private String opsReaderRole;
	private String adminRole;
	private String platformAdminRole;
	private IdentityProvider identityProvider = IdentityProvider.IN_MEMORY;
	private SecretSource secretSource = SecretSource.ENV;
	private String externalSecretRef;
	private boolean rotationEnabled;
	private RotationPhase rotationPhase;
	private String rotationUsername;
	private String rotationPassword;
	private String rotationPasswordEncoded;
	private String usersByUsernameQuery;
	private String authoritiesByUsernameQuery;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordEncoded() {
		return passwordEncoded;
	}

	public void setPasswordEncoded(String passwordEncoded) {
		this.passwordEncoded = passwordEncoded;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getAdminRole() {
		return adminRole;
	}

	public void setAdminRole(String adminRole) {
		this.adminRole = adminRole;
	}

	public String getOpsReaderRole() {
		return opsReaderRole;
	}

	public void setOpsReaderRole(String opsReaderRole) {
		this.opsReaderRole = opsReaderRole;
	}

	public String getPlatformAdminRole() {
		return platformAdminRole;
	}

	public void setPlatformAdminRole(String platformAdminRole) {
		this.platformAdminRole = platformAdminRole;
	}

	public IdentityProvider getIdentityProvider() {
		return identityProvider;
	}

	public void setIdentityProvider(IdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
	}

	public SecretSource getSecretSource() {
		return secretSource;
	}

	public void setSecretSource(SecretSource secretSource) {
		this.secretSource = secretSource;
	}

	public String getExternalSecretRef() {
		return externalSecretRef;
	}

	public void setExternalSecretRef(String externalSecretRef) {
		this.externalSecretRef = externalSecretRef;
	}

	public boolean isRotationEnabled() {
		return rotationEnabled;
	}

	public void setRotationEnabled(boolean rotationEnabled) {
		this.rotationEnabled = rotationEnabled;
	}

	public RotationPhase getRotationPhase() {
		return rotationPhase;
	}

	public void setRotationPhase(RotationPhase rotationPhase) {
		this.rotationPhase = rotationPhase;
	}

	public String getRotationUsername() {
		return rotationUsername;
	}

	public void setRotationUsername(String rotationUsername) {
		this.rotationUsername = rotationUsername;
	}

	public String getRotationPassword() {
		return rotationPassword;
	}

	public void setRotationPassword(String rotationPassword) {
		this.rotationPassword = rotationPassword;
	}

	public String getRotationPasswordEncoded() {
		return rotationPasswordEncoded;
	}

	public void setRotationPasswordEncoded(String rotationPasswordEncoded) {
		this.rotationPasswordEncoded = rotationPasswordEncoded;
	}

	public String getUsersByUsernameQuery() {
		return usersByUsernameQuery;
	}

	public void setUsersByUsernameQuery(String usersByUsernameQuery) {
		this.usersByUsernameQuery = usersByUsernameQuery;
	}

	public String getAuthoritiesByUsernameQuery() {
		return authoritiesByUsernameQuery;
	}

	public void setAuthoritiesByUsernameQuery(String authoritiesByUsernameQuery) {
		this.authoritiesByUsernameQuery = authoritiesByUsernameQuery;
	}
}
