package com.capitec.fraudengine.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.capitec.fraudengine.common.error.InvalidRuleGovernanceStateException;
import com.capitec.fraudengine.domain.model.RuleGovernanceMetadata;
import com.capitec.fraudengine.domain.model.RuleIdentity;
import com.capitec.fraudengine.domain.model.RuleLifecycleState;
import com.capitec.fraudengine.domain.model.enums.RuleActivationState;
import com.capitec.fraudengine.domain.model.enums.RuleExecutionSource;
import com.capitec.fraudengine.domain.model.enums.RuleLifecycleStatus;

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
