package com.oitws.fraudengine.domain.rule;

import java.util.List;

import com.oitws.fraudengine.domain.model.TransactionEvent;

/**
 * Input context made available to fraud rules during evaluation.
 *
 * @param transactionEvent transaction currently under evaluation
 * @param recentTransactions recent historical transactions relevant to history-based heuristics
 */
public record FraudRuleContext(
	TransactionEvent transactionEvent,
	List<TransactionEvent> recentTransactions
) {
	public FraudRuleContext {
		recentTransactions = recentTransactions == null ? List.of() : List.copyOf(recentTransactions);
	}
}
