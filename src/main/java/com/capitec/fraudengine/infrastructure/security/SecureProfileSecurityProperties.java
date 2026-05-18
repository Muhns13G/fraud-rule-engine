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

	private String username;
	private String password;
	private String passwordEncoded;
	private String role;
	private String adminRole;
	private IdentityProvider identityProvider = IdentityProvider.IN_MEMORY;
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

	public IdentityProvider getIdentityProvider() {
		return identityProvider;
	}

	public void setIdentityProvider(IdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
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
