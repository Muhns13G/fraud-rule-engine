CREATE INDEX idx_fraud_rule_results_rule_code_triggered_evaluation
    ON fraud_rule_results (rule_code, triggered, evaluation_id);

CREATE INDEX idx_fraud_rule_results_triggered_rule_code
    ON fraud_rule_results (triggered, rule_code);
