ALTER TABLE fraud_evaluations
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX idx_fraud_evaluations_account_event_timestamp
    ON fraud_evaluations (account_id, event_timestamp);

CREATE INDEX idx_fraud_evaluations_account_evaluated_at
    ON fraud_evaluations (account_id, evaluated_at);

CREATE INDEX idx_fraud_evaluations_decision_account_evaluated_at
    ON fraud_evaluations (decision, account_id, evaluated_at);

CREATE INDEX idx_fraud_evaluations_created_at
    ON fraud_evaluations (created_at);

CREATE INDEX idx_fraud_evaluations_updated_at
    ON fraud_evaluations (updated_at);
