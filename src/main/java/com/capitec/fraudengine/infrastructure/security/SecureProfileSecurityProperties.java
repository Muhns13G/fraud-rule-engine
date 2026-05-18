package com.capitec.fraudengine.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized credentials for the secure runtime profile.
 */
@ConfigurationProperties(prefix = "app.security.secure-profile")
public class SecureProfileSecurityProperties {

	private String username;
	private String password;
	private String role;
	private String adminRole;

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
}
