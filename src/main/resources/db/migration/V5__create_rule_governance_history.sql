CREATE TABLE fraud_rule_governance_history (
    governance_history_id BIGSERIAL PRIMARY KEY,
    rule_code VARCHAR(100) NOT NULL,
    rule_version VARCHAR(30) NOT NULL,
    action_type VARCHAR(40) NOT NULL,
    actor VARCHAR(150) NOT NULL,
    request_id VARCHAR(64) NOT NULL,
    from_lifecycle_status VARCHAR(20),
    from_activation_state VARCHAR(20),
    to_lifecycle_status VARCHAR(20) NOT NULL,
    to_activation_state VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rule_governance_history_rule_code_version
    ON fraud_rule_governance_history (rule_code, rule_version);

CREATE INDEX idx_rule_governance_history_created_at
    ON fraud_rule_governance_history (created_at);
