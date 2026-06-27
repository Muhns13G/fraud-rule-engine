package com.oitws.fraudengine.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class DefaultProfileSecurityGuardrailsTest {

	@Test
	void shouldAllowDefaultProfileWhenHostedPortIsNotPresent() {
		MockEnvironment environment = new MockEnvironment();

		assertThatCode(() -> DefaultProfileSecurityGuardrails.validateLocalOnlyDefaultProfile(environment))
			.doesNotThrowAnyException();
	}

	@Test
	void shouldRejectDefaultProfileWhenHostedPortIsPresent() {
		MockEnvironment environment = new MockEnvironment().withProperty("PORT", "8080");

		assertThatThrownBy(() -> DefaultProfileSecurityGuardrails.validateLocalOnlyDefaultProfile(environment))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Default profile cannot run in hosted runtime");
	}
}
