ALTER TABLE fraud_rule_governance_metadata
    ADD CONSTRAINT chk_rule_governance_active_requires_active_activation
        CHECK (lifecycle_status <> 'ACTIVE' OR activation_state = 'ACTIVE');

ALTER TABLE fraud_rule_governance_metadata
    ADD CONSTRAINT chk_rule_governance_draft_retired_require_inactive_activation
        CHECK (lifecycle_status NOT IN ('DRAFT', 'RETIRED') OR activation_state = 'INACTIVE');
