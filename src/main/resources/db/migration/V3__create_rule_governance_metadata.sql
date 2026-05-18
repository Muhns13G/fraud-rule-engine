CREATE TABLE fraud_rule_governance_metadata (
    rule_metadata_id BIGSERIAL PRIMARY KEY,
    rule_code VARCHAR(100) NOT NULL,
    rule_version VARCHAR(30) NOT NULL,
    rule_name VARCHAR(150) NOT NULL,
    lifecycle_status VARCHAR(20) NOT NULL,
    activation_state VARCHAR(20) NOT NULL,
    execution_source VARCHAR(40) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_rule_governance_metadata_rule_code_version UNIQUE (rule_code, rule_version)
);

CREATE INDEX idx_rule_governance_metadata_activation_state
    ON fraud_rule_governance_metadata (activation_state);

CREATE INDEX idx_rule_governance_metadata_rule_code
    ON fraud_rule_governance_metadata (rule_code);
