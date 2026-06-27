package com.oitws.fraudengine.infrastructure.persistence.entity;

import java.time.OffsetDateTime;

import com.oitws.fraudengine.domain.model.enums.RuleActivationState;
import com.oitws.fraudengine.domain.model.enums.RuleLifecycleStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Durable history trail for governance mutation timeline events.
 */
@Entity
@Table(name = "fraud_rule_governance_history")
public class RuleGovernanceHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "governance_history_id", nullable = false, updatable = false)
	private Long governanceHistoryId;

	@Column(name = "rule_code", nullable = false, length = 100)
	private String ruleCode;

	@Column(name = "rule_version", nullable = false, length = 30)
	private String ruleVersion;

	@Column(name = "action_type", nullable = false, length = 40)
	private String actionType;

	@Column(name = "actor", nullable = false, length = 150)
	private String actor;

	@Column(name = "request_id", nullable = false, length = 64)
	private String requestId;

	@Enumerated(EnumType.STRING)
	@Column(name = "from_lifecycle_status", length = 20)
	private RuleLifecycleStatus fromLifecycleStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "from_activation_state", length = 20)
	private RuleActivationState fromActivationState;

	@Enumerated(EnumType.STRING)
	@Column(name = "to_lifecycle_status", nullable = false, length = 20)
	private RuleLifecycleStatus toLifecycleStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "to_activation_state", nullable = false, length = 20)
	private RuleActivationState toActivationState;

	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@PrePersist
	void onCreate() {
		if (createdAt == null) {
			createdAt = OffsetDateTime.now();
		}
	}

	public Long getGovernanceHistoryId() {
		return governanceHistoryId;
	}

	public void setGovernanceHistoryId(Long governanceHistoryId) {
		this.governanceHistoryId = governanceHistoryId;
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

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public RuleLifecycleStatus getFromLifecycleStatus() {
		return fromLifecycleStatus;
	}

	public void setFromLifecycleStatus(RuleLifecycleStatus fromLifecycleStatus) {
		this.fromLifecycleStatus = fromLifecycleStatus;
	}

	public RuleActivationState getFromActivationState() {
		return fromActivationState;
	}

	public void setFromActivationState(RuleActivationState fromActivationState) {
		this.fromActivationState = fromActivationState;
	}

	public RuleLifecycleStatus getToLifecycleStatus() {
		return toLifecycleStatus;
	}

	public void setToLifecycleStatus(RuleLifecycleStatus toLifecycleStatus) {
		this.toLifecycleStatus = toLifecycleStatus;
	}

	public RuleActivationState getToActivationState() {
		return toActivationState;
	}

	public void setToActivationState(RuleActivationState toActivationState) {
		this.toActivationState = toActivationState;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
