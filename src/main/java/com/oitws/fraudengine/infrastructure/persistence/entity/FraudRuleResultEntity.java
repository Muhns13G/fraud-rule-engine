package com.oitws.fraudengine.infrastructure.persistence.entity;

import java.time.OffsetDateTime;

import com.oitws.fraudengine.domain.model.enums.RuleSeverity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Persistence entity for an individual rule result linked to a fraud evaluation.
 */
@Entity
@Table(name = "fraud_rule_results")
public class FraudRuleResultEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rule_result_id", nullable = false, updatable = false)
	private Long ruleResultId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "evaluation_id", nullable = false)
	private FraudEvaluationEntity fraudEvaluation;

	@Column(name = "rule_code", nullable = false, length = 100)
	private String ruleCode;

	@Column(name = "rule_name", nullable = false, length = 150)
	private String ruleName;

	@Column(name = "triggered", nullable = false)
	private boolean triggered;

	@Enumerated(EnumType.STRING)
	@Column(name = "severity", nullable = false, length = 20)
	private RuleSeverity severity;

	@Column(name = "score_contribution", nullable = false)
	private int scoreContribution;

	@Column(name = "reason", nullable = false)
	private String reason;

	@Column(name = "created_at", insertable = false, updatable = false)
	private OffsetDateTime createdAt;

	public Long getRuleResultId() {
		return ruleResultId;
	}

	public void setRuleResultId(Long ruleResultId) {
		this.ruleResultId = ruleResultId;
	}

	public FraudEvaluationEntity getFraudEvaluation() {
		return fraudEvaluation;
	}

	public void setFraudEvaluation(FraudEvaluationEntity fraudEvaluation) {
		this.fraudEvaluation = fraudEvaluation;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public boolean isTriggered() {
		return triggered;
	}

	public void setTriggered(boolean triggered) {
		this.triggered = triggered;
	}

	public RuleSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(RuleSeverity severity) {
		this.severity = severity;
	}

	public int getScoreContribution() {
		return scoreContribution;
	}

	public void setScoreContribution(int scoreContribution) {
		this.scoreContribution = scoreContribution;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
}
