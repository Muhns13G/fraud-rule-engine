package com.capitec.fraudengine.api.controller;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

final class SecureProfileTestCredentials {

	static final String USERNAME = "secure-user";
	static final String PASSWORD = "change-me-secure";
	static final String USERNAME_PROPERTY = "app.security.secure-profile.username=" + USERNAME;
	static final String PASSWORD_PROPERTY = "app.security.secure-profile.password=" + PASSWORD;

	static final String PRIMARY_USERNAME = "secure-user-primary";
	static final String PRIMARY_PASSWORD = "change-me-secure-primary";
	static final String PRIMARY_USERNAME_PROPERTY = "app.security.secure-profile.username=" + PRIMARY_USERNAME;
	static final String PRIMARY_PASSWORD_PROPERTY = "app.security.secure-profile.password=" + PRIMARY_PASSWORD;

	static final String ROTATION_USERNAME = "secure-user-rotating";
	static final String ROTATION_PASSWORD = "change-me-secure-rotating";
	static final String ROTATION_USERNAME_PROPERTY = "app.security.secure-profile.rotation-username=" + ROTATION_USERNAME;
	static final String ROTATION_PASSWORD_PROPERTY = "app.security.secure-profile.rotation-password=" + ROTATION_PASSWORD;

	private SecureProfileTestCredentials() {
	}

	static RequestPostProcessor secureBasicAuth() {
		return httpBasic(USERNAME, PASSWORD);
	}

	static RequestPostProcessor primaryBasicAuth() {
		return httpBasic(PRIMARY_USERNAME, PRIMARY_PASSWORD);
	}

	static RequestPostProcessor rotationBasicAuth() {
		return httpBasic(ROTATION_USERNAME, ROTATION_PASSWORD);
	}
}
