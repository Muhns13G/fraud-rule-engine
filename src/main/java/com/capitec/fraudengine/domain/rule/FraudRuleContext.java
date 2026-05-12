package com.capitec.fraudengine.domain.rule;

import java.util.List;

import com.capitec.fraudengine.domain.model.TransactionEvent;

public record FraudRuleContext(
	TransactionEvent transactionEvent,
	List<TransactionEvent> recentTransactions
) {
	public FraudRuleContext {
		recentTransactions = recentTransactions == null ? List.of() : List.copyOf(recentTransactions);
	}
}
