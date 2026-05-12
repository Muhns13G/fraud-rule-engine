package com.capitec.fraudengine.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.capitec.fraudengine.domain.model.enums.FraudDecision;
import com.capitec.fraudengine.domain.model.enums.MerchantCategory;
import com.capitec.fraudengine.domain.model.enums.TransactionChannel;
import com.capitec.fraudengine.domain.model.enums.TransactionType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "fraud_evaluations")
public class FraudEvaluationEntity {

	@Id
	@Column(name = "evaluation_id", nullable = false, updatable = false)
	private UUID evaluationId;

	@Column(name = "transaction_id", nullable = false, length = 100)
	private String transactionId;

	@Column(name = "account_id", nullable = false, length = 100)
	private String accountId;

	@Column(name = "customer_id", nullable = false, length = 100)
	private String customerId;

	@Column(name = "amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Column(name = "currency", nullable = false, length = 3)
	private String currency;

	@Column(name = "merchant_id", nullable = false, length = 100)
	private String merchantId;

	@Enumerated(EnumType.STRING)
	@Column(name = "merchant_category", nullable = false, length = 100)
	private MerchantCategory merchantCategory;

	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type", nullable = false, length = 100)
	private TransactionType transactionType;

	@Enumerated(EnumType.STRING)
	@Column(name = "channel", nullable = false, length = 100)
	private TransactionChannel channel;

	@Column(name = "event_timestamp", nullable = false)
	private OffsetDateTime eventTimestamp;

	@Column(name = "location_country_code", length = 3)
	private String locationCountryCode;

	@Column(name = "location_city", length = 120)
	private String locationCity;

	@Column(name = "reference", length = 255)
	private String reference;

	@Enumerated(EnumType.STRING)
	@Column(name = "decision", nullable = false, length = 20)
	private FraudDecision decision;

	@Column(name = "decision_score", nullable = false)
	private int decisionScore;

	@Column(name = "trace_summary", nullable = false)
	private String traceSummary;

	@Column(name = "evaluated_at", nullable = false)
	private OffsetDateTime evaluatedAt;

	@OneToMany(mappedBy = "fraudEvaluation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<FraudRuleResultEntity> ruleResults = new ArrayList<>();

	public UUID getEvaluationId() {
		return evaluationId;
	}

	public void setEvaluationId(UUID evaluationId) {
		this.evaluationId = evaluationId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public MerchantCategory getMerchantCategory() {
		return merchantCategory;
	}

	public void setMerchantCategory(MerchantCategory merchantCategory) {
		this.merchantCategory = merchantCategory;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public TransactionChannel getChannel() {
		return channel;
	}

	public void setChannel(TransactionChannel channel) {
		this.channel = channel;
	}

	public OffsetDateTime getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(OffsetDateTime eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	public String getLocationCountryCode() {
		return locationCountryCode;
	}

	public void setLocationCountryCode(String locationCountryCode) {
		this.locationCountryCode = locationCountryCode;
	}

	public String getLocationCity() {
		return locationCity;
	}

	public void setLocationCity(String locationCity) {
		this.locationCity = locationCity;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public FraudDecision getDecision() {
		return decision;
	}

	public void setDecision(FraudDecision decision) {
		this.decision = decision;
	}

	public int getDecisionScore() {
		return decisionScore;
	}

	public void setDecisionScore(int decisionScore) {
		this.decisionScore = decisionScore;
	}

	public String getTraceSummary() {
		return traceSummary;
	}

	public void setTraceSummary(String traceSummary) {
		this.traceSummary = traceSummary;
	}

	public OffsetDateTime getEvaluatedAt() {
		return evaluatedAt;
	}

	public void setEvaluatedAt(OffsetDateTime evaluatedAt) {
		this.evaluatedAt = evaluatedAt;
	}

	public List<FraudRuleResultEntity> getRuleResults() {
		return ruleResults;
	}

	public void setRuleResults(List<FraudRuleResultEntity> ruleResults) {
		this.ruleResults.clear();
		if (ruleResults != null) {
			ruleResults.forEach(this::addRuleResult);
		}
	}

	public void addRuleResult(FraudRuleResultEntity ruleResult) {
		ruleResult.setFraudEvaluation(this);
		this.ruleResults.add(ruleResult);
	}
}
