package com.oitws.fraudengine.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.oitws.fraudengine.common.error.InvalidRuleGovernanceStateException;
import com.oitws.fraudengine.domain.model.RuleGovernanceMetadata;
import com.oitws.fraudengine.domain.model.RuleIdentity;
import com.oitws.fraudengine.domain.model.RuleLifecycleState;
import com.oitws.fraudengine.domain.model.enums.RuleActivationState;
import com.oitws.fraudengine.domain.model.enums.RuleExecutionSource;
import com.oitws.fraudengine.domain.model.enums.RuleLifecycleStatus;

class RuleGovernancePolicyTest {

	private final RuleGovernancePolicy policy = new RuleGovernancePolicy();

	@Test
	void shouldAllowActiveLifecycleWithActiveActivation() {
		RuleGovernanceMetadata metadata = metadata(
			"HIGH_AMOUNT",
			"1.0.0",
			"High Amount Rule",
			RuleLifecycleStatus.ACTIVE,
			RuleActivationState.ACTIVE,
			RuleExecutionSource.CODE_DEFINED
		);

		assertDoesNotThrow(() -> policy.validateState(metadata));
		assertDoesNotThrow(() -> policy.validateExecutionBoundary(metadata));
	}

	@Test
	void shouldRejectActiveLifecycleWithInactiveActivation() {
		RuleGovernanceMetadata metadata = metadata(
			"HIGH_AMOUNT",
			"1.0.0",
			"High Amount Rule",
			RuleLifecycleStatus.ACTIVE,
			RuleActivationState.INACTIVE,
			RuleExecutionSource.CODE_DEFINED
		);

		assertThrows(InvalidRuleGovernanceStateException.class, () -> policy.validateState(metadata));
	}

	@Test
	void shouldRejectDraftLifecycleWithActiveActivation() {
		RuleGovernanceMetadata metadata = metadata(
			"HIGH_AMOUNT",
			"1.0.0",
			"High Amount Rule",
			RuleLifecycleStatus.DRAFT,
			RuleActivationState.ACTIVE,
			RuleExecutionSource.CODE_DEFINED
		);

		assertThrows(InvalidRuleGovernanceStateException.class, () -> policy.validateState(metadata));
	}

	@Test
	void shouldAllowDeprecatedLifecycleWithEitherActivationState() {
		RuleGovernanceMetadata activeDeprecated = metadata(
			"HIGH_AMOUNT",
			"1.0.0",
			"High Amount Rule",
			RuleLifecycleStatus.DEPRECATED,
			RuleActivationState.ACTIVE,
			RuleExecutionSource.CODE_DEFINED
		);
		RuleGovernanceMetadata inactiveDeprecated = metadata(
			"HIGH_AMOUNT",
			"1.0.1",
			"High Amount Rule",
			RuleLifecycleStatus.DEPRECATED,
			RuleActivationState.INACTIVE,
			RuleExecutionSource.CODE_DEFINED
		);

		assertDoesNotThrow(() -> policy.validateState(activeDeprecated));
		assertDoesNotThrow(() -> policy.validateState(inactiveDeprecated));
	}

	@Test
	void shouldAllowCodeDefinedExecutionSourceWithinCurrentBoundary() {
		RuleGovernanceMetadata metadata = metadata(
			"HIGH_AMOUNT",
			"1.0.0",
			"High Amount Rule",
			RuleLifecycleStatus.ACTIVE,
			RuleActivationState.ACTIVE,
			RuleExecutionSource.CODE_DEFINED
		);

		assertDoesNotThrow(() -> policy.validateExecutionBoundary(metadata));
	}

	@Test
	void shouldRejectBlankRuleIdentityFields() {
		RuleGovernanceMetadata metadata = metadata(
			" ",
			" ",
			"High Amount Rule",
			RuleLifecycleStatus.ACTIVE,
			RuleActivationState.ACTIVE,
			RuleExecutionSource.CODE_DEFINED
		);

		assertThrows(InvalidRuleGovernanceStateException.class, () -> policy.validateState(metadata));
	}

	@Test
	void shouldAllowLifecycleTransitionFromActiveToDeprecated() {
		RuleGovernanceMetadata current = metadata(
			"HIGH_AMOUNT",
			"1.0.0",
			"High Amount Rule",
			RuleLifecycleStatus.ACTIVE,
			RuleActivationState.ACTIVE,
			RuleExecutionSource.CODE_DEFINED
		);
		RuleGovernanceMetadata target = metadata(
			"HIGH_AMOUNT",
			"1.0.0",
			"High Amount Rule",
			RuleLifecycleStatus.DEPRECATED,
			RuleActivationState.INACTIVE,
			RuleExecutionSource.CODE_DEFINED
		);

		assertDoesNotThrow(() -> policy.validateTransition(current, target));
	}

	@Test
	void shouldRejectLifecycleTransitionFromRetiredToActive() {
		RuleGovernanceMetadata current = metadata(
			"HIGH_AMOUNT",
			"1.0.0",
			"High Amount Rule",
			RuleLifecycleStatus.RETIRED,
			RuleActivationState.INACTIVE,
			RuleExecutionSource.CODE_DEFINED
		);
		RuleGovernanceMetadata target = metadata(
			"HIGH_AMOUNT",
			"1.0.0",
			"High Amount Rule",
			RuleLifecycleStatus.ACTIVE,
			RuleActivationState.ACTIVE,
			RuleExecutionSource.CODE_DEFINED
		);

		assertThrows(InvalidRuleGovernanceStateException.class, () -> policy.validateTransition(current, target));
	}

	private RuleGovernanceMetadata metadata(
		String ruleCode,
		String version,
		String ruleName,
		RuleLifecycleStatus lifecycleStatus,
		RuleActivationState activationState,
		RuleExecutionSource executionSource
	) {
		return new RuleGovernanceMetadata(
			new RuleIdentity(ruleCode, version),
			ruleName,
			new RuleLifecycleState(lifecycleStatus, activationState),
			executionSource
		);
	}
}
