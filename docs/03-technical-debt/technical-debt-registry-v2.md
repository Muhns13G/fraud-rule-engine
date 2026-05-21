# Technical Debt Registry v2 (Post-Phase 5.4 Reconciled)

## Scope
This `v2` registry is reconciled through Sprint 5.4 close-out.
- Ingested sources:
  - `docs/03-technical-debt/technical-debt-registry-v1.md`
  - all completion reports from Phase 1 to Phase 5.4
- Exclusion rule:
  - items already marked `Closed` in `v1` are not carried into `v2`

## Completion Reports Ingested
1. `docs/01-completion-reports/phase-01/sprint-1.1-completion-report.md`
2. `docs/01-completion-reports/phase-01/sprint-1.2-completion-report.md`
3. `docs/01-completion-reports/phase-01/sprint-1.3-completion-report.md`
4. `docs/01-completion-reports/phase-01/sprint-1.4-completion-report.md`
5. `docs/01-completion-reports/phase-01/sprint-1.5-completion-report.md`
6. `docs/01-completion-reports/phase-02/sprint-2.1-completion-report.md`
7. `docs/01-completion-reports/phase-02/sprint-2.2-completion-report.md`
8. `docs/01-completion-reports/phase-02/sprint-2.3-completion-report.md`
9. `docs/01-completion-reports/phase-02/sprint-2.4-completion-report.md`
10. `docs/01-completion-reports/phase-02/sprint-2.5-completion-report.md`
11. `docs/01-completion-reports/phase-03/sprint-3.1-completion-report.md`
12. `docs/01-completion-reports/phase-03/sprint-3.2-completion-report.md`
13. `docs/01-completion-reports/phase-03/sprint-3.3-completion-report.md`
14. `docs/01-completion-reports/phase-03/sprint-3.4-completion-report.md`
15. `docs/01-completion-reports/phase-03/phase-03-summary.md`
16. `docs/01-completion-reports/phase-04/sprint-4.1-completion-report.md`
17. `docs/01-completion-reports/phase-04/sprint-4.2-completion-report.md`
18. `docs/01-completion-reports/phase-04/sprint-4.3-completion-report.md`
19. `docs/01-completion-reports/phase-04/sprint-4.4-completion-report.md`
20. `docs/01-completion-reports/phase-04/phase-04-summary.md`
21. `docs/01-completion-reports/phase-05/sprint-5.1-completion-report.md`
22. `docs/01-completion-reports/phase-05/sprint-5.2-completion-report.md`
23. `docs/01-completion-reports/phase-05/sprint-5.3-completion-report.md`
24. `docs/01-completion-reports/phase-05/sprint-5.4-completion-report.md`

## Active Debt Table

| Debt ID | Title | Status | Severity | Source | Evidence |
| --- | --- | --- | --- | --- | --- |
| TD-003 | Default profile remains intentionally open for API surface | Partially addressed | Medium | Recorded+Observed | `src/main/java/com/capitec/fraudengine/infrastructure/security/PhaseOneSecurityConfiguration.java`, `src/main/java/com/capitec/fraudengine/infrastructure/security/DefaultProfileSecurityGuardrails.java`, `docs/01-completion-reports/phase-05/sprint-5.1-completion-report.md` |
| TD-005 | No enterprise secret management/rotation orchestration for secure credentials | Partially addressed | Medium | Recorded+Observed | `src/main/resources/application.yaml`, `src/main/java/com/capitec/fraudengine/infrastructure/security/SecureProfileSecretSupplier.java`, `src/main/java/com/capitec/fraudengine/infrastructure/security/EnvExternalManagerSecretSupplier.java`, `docs/operations/runbooks/secure-credential-rotation-runbook.md`, `docs/01-completion-reports/phase-05/sprint-5.2-completion-report.md` |
| TD-012 | Rule governance workflow maturity remains limited (beyond basic mutation endpoints) | Closed | Medium | Recorded+Observed | `src/main/java/com/capitec/fraudengine/api/controller/RuleGovernanceController.java`, `src/main/java/com/capitec/fraudengine/application/service/RuleGovernanceMutationService.java`, `docs/01-completion-reports/phase-05/sprint-5.3-completion-report.md` |
| TD-014 | Promotion/deprecation operational workflow and history/audit depth are incomplete | Closed | Medium | Recorded+Observed | `src/main/java/com/capitec/fraudengine/infrastructure/persistence/entity/RuleGovernanceHistoryEntity.java`, `src/main/resources/db/migration/V5__create_rule_governance_history.sql`, `docs/01-completion-reports/phase-05/sprint-5.3-completion-report.md` |
| TD-018 | Retrieval still lacks rule-hit lookup filter for investigation workflows | Closed | Low | Recorded+Observed | `src/main/java/com/capitec/fraudengine/api/controller/FraudEvaluationController.java`, `src/main/java/com/capitec/fraudengine/infrastructure/persistence/repository/FraudEvaluationSpecifications.java`, `src/main/resources/db/migration/V6__add_rule_hit_lookup_indexes.sql`, `docs/01-completion-reports/phase-05/sprint-5.4-completion-report.md` |
| TD-021 | Enterprise IAM/JWT/OAuth2 integration remains deferred | Partially addressed | Medium | Recorded+Observed | `src/main/java/com/capitec/fraudengine/infrastructure/security/HardenedProfileSecurityConfiguration.java`, `docs/01-completion-reports/phase-05/sprint-5.1-completion-report.md` |
| TD-022 | Governance list endpoint has no pagination | Closed | Medium | Observed | `src/main/java/com/capitec/fraudengine/api/controller/RuleGovernanceController.java`, `src/main/java/com/capitec/fraudengine/application/service/RuleGovernanceRetrievalService.java`, `docs/01-completion-reports/phase-05/sprint-5.3-completion-report.md` |
| TD-023 | Velocity rule counts future-dated transactions due to absolute time-difference logic | Closed | Medium | Observed | `src/main/java/com/capitec/fraudengine/domain/rule/impl/VelocityFraudRule.java`, `src/test/java/com/capitec/fraudengine/domain/rule/impl/VelocityFraudRuleTest.java`, `docs/01-completion-reports/phase-05/sprint-5.4-completion-report.md` |
| TD-024 | Repository includes `.DS_Store` artifacts in source/docs trees | Closed | Low | Observed | `.gitignore`, `scripts/run-repo-hygiene-checks.sh`, `docs/01-completion-reports/phase-05/sprint-5.4-completion-report.md` |
| TD-025 | OpenAPI metadata still describes a "Phase 1 API" despite expanded scope | Closed | Low | Observed | `src/main/java/com/capitec/fraudengine/infrastructure/config/OpenApiConfiguration.java`, `docs/01-completion-reports/phase-05/sprint-5.1-completion-report.md` |
| TD-026 | Secure-profile test credentials are duplicated across multiple integration tests | Closed | Low | Observed | `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileTestCredentials.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileSecurityIntegrationTest.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileGovernanceAuthorizationIntegrationTest.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileGovernanceAdminIntegrationTest.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfilePlatformAdminIntegrationTest.java`, `docs/01-completion-reports/phase-05/sprint-5.4-completion-report.md` |
| TD-027 | Hardened JWT path does not yet enforce issuer/audience validation | Partially addressed | Medium | Observed | `src/main/java/com/capitec/fraudengine/infrastructure/security/HardenedProfileSecurityConfiguration.java`, `src/main/java/com/capitec/fraudengine/infrastructure/security/HardenedProfileSecurityProperties.java`, `src/test/java/com/capitec/fraudengine/infrastructure/security/HardenedProfileSecurityConfigurationTest.java` |

---

## Detailed Entries

### TD-003
- Why it matters: `default` profile permissiveness is useful for reviewers but unsafe for non-local deployment if profile control drifts.
- Current state: hosted runtime is now blocked under `default` via startup guardrails, but local open behavior remains by design.
- Suggested resolution direction: keep `default` local-only and continue enforcing hosted runtime profile controls.
- Target phase candidate: `Phase 5`.

### TD-005
- Why it matters: secure secret and rotation posture is now substantially improved, but centralized enterprise secret-manager and organization-level rotation orchestration are still not fully integrated.
- Current state: concrete env-backed external secret adapter, explicit rotation phases, diagnostics, and runbook are implemented.
- Suggested resolution direction: complete enterprise-managed integration (provider-native secret retrieval, policy-driven rotation orchestration, and org-level rollout controls).
- Target phase candidate: `Phase 5`.

### TD-012
- Why it mattered: mutation endpoints existed, but governance workflow lifecycle semantics and operator flow depth were previously basic.
- Current state: closed in Sprint `5.3` via explicit workflow actions, contract-aligned transitions, and expanded governance workflow regression coverage.
- Resolution evidence:
  - `src/main/java/com/capitec/fraudengine/api/controller/RuleGovernanceController.java`
  - `src/main/java/com/capitec/fraudengine/application/service/RuleGovernanceMutationService.java`
  - `docs/01-completion-reports/phase-05/sprint-5.3-completion-report.md`

### TD-014
- Why it mattered: promotion/deprecation semantics needed durable traceability and deeper operational workflow support.
- Current state: closed in Sprint `5.3` via durable governance history persistence and retrieval for lifecycle auditability.
- Resolution evidence:
  - `src/main/java/com/capitec/fraudengine/infrastructure/persistence/entity/RuleGovernanceHistoryEntity.java`
  - `src/main/resources/db/migration/V5__create_rule_governance_history.sql`
  - `docs/01-completion-reports/phase-05/sprint-5.3-completion-report.md`

### TD-018
- Why it mattered: investigation workflows lacked direct lookup by triggered rule code(s).
- Current state: closed in Sprint `5.4` via `ruleHit`/`ruleHitMatch` retrieval filters plus query/index hardening.
- Resolution evidence:
  - `src/main/java/com/capitec/fraudengine/api/controller/FraudEvaluationController.java`
  - `src/main/java/com/capitec/fraudengine/infrastructure/persistence/repository/FraudEvaluationSpecifications.java`
  - `src/main/resources/db/migration/V6__add_rule_hit_lookup_indexes.sql`

### TD-021
- Why it matters: enterprise identity posture is partially implemented; hardened JWT path exists, but full enterprise rollout still depends on IdP integration and stricter token validation policy.
- Current state: hardened/production JWT resource-server path is implemented and test-covered.
- Suggested resolution direction: complete live IdP rollout contract and tighten token validation policy (issuer/audience).
- Target phase candidate: `Phase 5`.

### TD-022
- Why it mattered: `GET /api/admin/rules` previously returned unbounded governance read payloads.
- Current state: closed in Sprint `5.3` by introducing pagination contract for governance list/read surfaces.
- Resolution evidence:
  - `src/main/java/com/capitec/fraudengine/api/controller/RuleGovernanceController.java`
  - `src/main/java/com/capitec/fraudengine/application/service/RuleGovernanceRetrievalService.java`
  - `docs/01-completion-reports/phase-05/sprint-5.3-completion-report.md`

### TD-023
- Why it mattered: velocity semantics should only count prior transactions; absolute timestamp difference over-counted under clock skew/future-dated events.
- Current state: closed in Sprint `5.4` by ignoring future-dated events in velocity-window evaluation and adding regression coverage.
- Resolution evidence:
  - `src/main/java/com/capitec/fraudengine/domain/rule/impl/VelocityFraudRule.java`
  - `src/test/java/com/capitec/fraudengine/domain/rule/impl/VelocityFraudRuleTest.java`

### TD-024
- Why it mattered: committed `.DS_Store` artifacts created noisy diffs and reduced reviewer confidence.
- Current state: closed in Sprint `5.4` by enforcing hygiene checks, blocking `.DS_Store` regressions, and cleaning tracked artifacts.
- Resolution evidence:
  - `.gitignore`
  - `scripts/run-repo-hygiene-checks.sh`

### TD-025
- Why it mattered: reviewer-facing API metadata understated implemented scope and could create confusion during API inspection.
- Current state: closed in Sprint `5.1` by updating OpenAPI description to reflect current implemented scope.
- Resolution evidence: `src/main/java/com/capitec/fraudengine/infrastructure/config/OpenApiConfiguration.java`.

### TD-026
- Why it mattered: duplicated secure-profile test credentials increased maintenance overhead and drift risk.
- Current state: closed in Sprint `5.4` by centralizing secure-profile credentials in a shared test fixture consumed by integration suites.
- Resolution evidence:
  - `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileTestCredentials.java`
  - `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileSecurityIntegrationTest.java`
  - `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileGovernanceAuthorizationIntegrationTest.java`
  - `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileGovernanceAdminIntegrationTest.java`
  - `src/test/java/com/capitec/fraudengine/api/controller/SecureProfilePlatformAdminIntegrationTest.java`

### TD-027
- Why it matters: production token trust boundaries depend on strict issuer and audience validation.
- Current state: partially addressed via hardened decoder fail-fast requirements (`issuer-uri`, `jwk-set-uri`, `audience`) and validator-level regression coverage for invalid issuer/audience claims.
- Suggested resolution direction: complete live external IdP rollout verification in hosted production-mode environments.
- Target phase candidate: `Phase 6+`.

---

## Reconciliation Notes
- `v2` intentionally excludes all debt items already closed in `v1`.
- `v2` includes one newly formalized active item (`TD-021`) derived from explicit Phase 4 deferral notes.
- Sprint 5.1 reconciliation updates:
  - `TD-003` moved to `Partially addressed` (default-profile hosted-runtime guardrail enforced)
  - `TD-021` moved to `Partially addressed` (hardened JWT/OIDC path implemented)
  - `TD-025` is now explicitly tracked as `Closed` for visible audit history
  - `TD-027` added for issuer/audience enforcement carry-forward
- Sprint 5.2 reconciliation updates:
  - `TD-005` remains `Partially addressed` but now includes concrete adapter, phase contract, diagnostics, integration coverage, and runbook evidence
- Sprint 5.3 reconciliation updates:
  - `TD-012` closed via explicit governance workflow action implementation and regression coverage
  - `TD-014` closed via durable lifecycle-history persistence and retrieval
  - `TD-022` closed via paginated governance list/read contracts
- Sprint 5.4 reconciliation updates:
  - `TD-018` closed via rule-hit retrieval filters and query/index hardening
  - `TD-023` closed via velocity temporal-correctness fix and regression coverage
  - `TD-024` closed via hygiene enforcement and `.DS_Store` cleanup
  - `TD-026` closed via shared secure-profile credential test fixture
- Phase 6 policy lock:
  - `TD-021` and `TD-027` remain intentionally `Partially addressed` until live external IdP rollout is implemented and verified end-to-end.
  - Reviewer-hosted verification continues to target `secure` profile; hardened/production trust-boundary hardening is validated locally via code+tests.
