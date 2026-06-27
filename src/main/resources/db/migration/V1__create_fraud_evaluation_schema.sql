CREATE TABLE fraud_evaluations (
    evaluation_id UUID PRIMARY KEY,
    transaction_id VARCHAR(100) NOT NULL,
    account_id VARCHAR(100) NOT NULL,
    customer_id VARCHAR(100) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    currency CHAR(3) NOT NULL,
    merchant_id VARCHAR(100) NOT NULL,
    merchant_category VARCHAR(100) NOT NULL,
    transaction_type VARCHAR(100) NOT NULL,
    channel VARCHAR(100) NOT NULL,
    event_timestamp TIMESTAMPTZ NOT NULL,
    location_country_code VARCHAR(3),
    location_city VARCHAR(120),
    reference VARCHAR(255),
    decision VARCHAR(20) NOT NULL,
    decision_score INTEGER NOT NULL,
    trace_summary TEXT NOT NULL,
    evaluated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE fraud_rule_results (
    rule_result_id BIGSERIAL PRIMARY KEY,
    evaluation_id UUID NOT NULL,
    rule_code VARCHAR(100) NOT NULL,
    rule_name VARCHAR(150) NOT NULL,
    triggered BOOLEAN NOT NULL,
    severity VARCHAR(20) NOT NULL,
    score_contribution INTEGER NOT NULL,
    reason TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fraud_rule_results_evaluation
        FOREIGN KEY (evaluation_id)
        REFERENCES fraud_evaluations (evaluation_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_fraud_evaluations_account_id
    ON fraud_evaluations (account_id);

CREATE INDEX idx_fraud_evaluations_decision
    ON fraud_evaluations (decision);

CREATE INDEX idx_fraud_evaluations_evaluated_at
    ON fraud_evaluations (evaluated_at);

CREATE INDEX idx_fraud_rule_results_evaluation_id
    ON fraud_rule_results (evaluation_id);
