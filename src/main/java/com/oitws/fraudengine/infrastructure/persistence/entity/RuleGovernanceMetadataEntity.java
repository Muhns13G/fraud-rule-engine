package com.oitws.fraudengine.infrastructure.persistence.entity;

import java.time.OffsetDateTime;

import com.oitws.fraudengine.domain.model.enums.RuleActivationState;
import com.oitws.fraudengine.domain.model.enums.RuleExecutionSource;
import com.oitws.fraudengine.domain.model.enums.RuleLifecycleStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Persistence entity for rule governance metadata.
 */
@Entity
@Table(
	name = "fraud_rule_governance_metadata",
	uniqueConstraints = @UniqueConstraint(name = "uq_rule_governance_metadata_rule_code_version", columnNames = {
		"rule_code",
		"rule_version"
	})
)
public class RuleGovernanceMetadataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rule_metadata_id", nullable = false, updatable = false)
	private Long ruleMetadataId;

	@Column(name = "rule_code", nullable = false, length = 100)
	private String ruleCode;

	@Column(name = "rule_version", nullable = false, length = 30)
	private String ruleVersion;

	@Column(name = "rule_name", nullable = false, length = 150)
	private String ruleName;

	@Enumerated(EnumType.STRING)
	@Column(name = "lifecycle_status", nullable = false, length = 20)
	private RuleLifecycleStatus lifecycleStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "activation_state", nullable = false, length = 20)
	private RuleActivationState activationState;

	@Enumerated(EnumType.STRING)
	@Column(name = "execution_source", nullable = false, length = 40)
	private RuleExecutionSource executionSource;

	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;

	@PrePersist
	void onCreate() {
		OffsetDateTime now = OffsetDateTime.now();
		if (createdAt == null) {
			createdAt = now;
		}
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = OffsetDateTime.now();
	}

	public Long getRuleMetadataId() {
		return ruleMetadataId;
	}

	public void setRuleMetadataId(Long ruleMetadataId) {
		this.ruleMetadataId = ruleMetadataId;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getRuleVersion() {
		return ruleVersion;
	}

	public void setRuleVersion(String ruleVersion) {
		this.ruleVersion = ruleVersion;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public RuleLifecycleStatus getLifecycleStatus() {
		return lifecycleStatus;
	}

	public void setLifecycleStatus(RuleLifecycleStatus lifecycleStatus) {
		this.lifecycleStatus = lifecycleStatus;
	}

	public RuleActivationState getActivationState() {
		return activationState;
	}

	public void setActivationState(RuleActivationState activationState) {
		this.activationState = activationState;
	}

	public RuleExecutionSource getExecutionSource() {
		return executionSource;
	}

	public void setExecutionSource(RuleExecutionSource executionSource) {
		this.executionSource = executionSource;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(OffsetDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
