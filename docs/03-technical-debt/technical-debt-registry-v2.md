# Technical Debt Registry v2 (Active Items Only, Post-Phase 5.2)

## Scope
This `v2` registry includes only **active** technical debt after Sprint 5.2 close-out.
- Ingested sources:
  - `docs/03-technical-debt/technical-debt-registry-v1.md`
  - all completion reports from Phase 1 to Phase 5.2
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

## Active Debt Table

| Debt ID | Title | Status | Severity | Source | Evidence |
| --- | --- | --- | --- | --- | --- |
| TD-003 | Default profile remains intentionally open for API surface | Partially addressed | Medium | Recorded+Observed | `src/main/java/com/capitec/fraudengine/infrastructure/security/PhaseOneSecurityConfiguration.java`, `src/main/java/com/capitec/fraudengine/infrastructure/security/DefaultProfileSecurityGuardrails.java`, `docs/01-completion-reports/phase-05/sprint-5.1-completion-report.md` |
| TD-005 | No enterprise secret management/rotation orchestration for secure credentials | Partially addressed | Medium | Recorded+Observed | `src/main/resources/application.yaml`, `src/main/java/com/capitec/fraudengine/infrastructure/security/SecureProfileSecretSupplier.java`, `src/main/java/com/capitec/fraudengine/infrastructure/security/EnvExternalManagerSecretSupplier.java`, `docs/operations/runbooks/secure-credential-rotation-runbook.md`, `docs/01-completion-reports/phase-05/sprint-5.2-completion-report.md` |
| TD-012 | Rule governance workflow maturity remains limited (beyond basic mutation endpoints) | Partially addressed | Medium | Recorded+Observed | `src/main/java/com/capitec/fraudengine/api/controller/RuleGovernanceController.java`, `src/main/java/com/capitec/fraudengine/application/service/RuleGovernanceMutationService.java` |
| TD-014 | Promotion/deprecation operational workflow and history/audit depth are incomplete | Partially addressed | Medium | Recorded | `src/main/java/com/capitec/fraudengine/domain/policy/RuleGovernancePolicy.java`, `docs/01-completion-reports/phase-02/sprint-2.5-completion-report.md` |
| TD-018 | Retrieval still lacks rule-hit lookup filter for investigation workflows | Partially addressed | Low | Recorded | `docs/RAG/03-api-scope.md`, `docs/01-completion-reports/phase-01/sprint-1.1-completion-report.md` |
| TD-021 | Enterprise IAM/JWT/OAuth2 integration remains deferred | Partially addressed | Medium | Recorded+Observed | `src/main/java/com/capitec/fraudengine/infrastructure/security/HardenedProfileSecurityConfiguration.java`, `docs/01-completion-reports/phase-05/sprint-5.1-completion-report.md` |
| TD-022 | Governance list endpoint has no pagination | Open | Medium | Observed | `src/main/java/com/capitec/fraudengine/api/controller/RuleGovernanceController.java`, `src/main/java/com/capitec/fraudengine/application/service/RuleGovernanceRetrievalService.java` |
| TD-023 | Velocity rule counts future-dated transactions due to absolute time-difference logic | Open | Medium | Observed | `src/main/java/com/capitec/fraudengine/domain/rule/impl/VelocityFraudRule.java`, `src/test/java/com/capitec/fraudengine/domain/rule/impl/VelocityFraudRuleTest.java` |
| TD-024 | Repository includes `.DS_Store` artifacts in source/docs trees | Open | Low | Observed | `docs/.DS_Store`, `docs/00-blueprints/.DS_Store`, `docs/01-completion-reports/.DS_Store`, `src/main/java/.DS_Store`, `src/main/resources/.DS_Store`, `src/test/java/.DS_Store` |
| TD-025 | OpenAPI metadata still describes a "Phase 1 API" despite expanded scope | Closed | Low | Observed | `src/main/java/com/capitec/fraudengine/infrastructure/config/OpenApiConfiguration.java`, `docs/01-completion-reports/phase-05/sprint-5.1-completion-report.md` |
| TD-026 | Secure-profile test credentials are duplicated across multiple integration tests | Open | Low | Observed | `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileSecurityIntegrationTest.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileGovernanceAuthorizationIntegrationTest.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileDatasourceResilienceIntegrationTest.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileGovernanceAdminIntegrationTest.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfilePlatformAdminIntegrationTest.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileSecurityDiagnosticsIntegrationTest.java` |
| TD-027 | Hardened JWT path does not yet enforce issuer/audience validation | Open | Medium | Observed | `src/main/java/com/capitec/fraudengine/infrastructure/security/HardenedProfileSecurityConfiguration.java`, `src/main/java/com/capitec/fraudengine/infrastructure/security/HardenedProfileSecurityProperties.java` |

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
- Why it matters: mutation endpoints exist, but full governance workflow lifecycle (approval-oriented controls, richer operator flow) is still basic.
- Suggested resolution direction: add explicit governance workflow states/actions and operational safeguards around mutation.
- Target phase candidate: `Phase 5`.

### TD-014
- Why it matters: lifecycle transitions are validationally constrained, but long-lived promotion/deprecation traceability and operator workflow depth are limited.
- Suggested resolution direction: add durable lifecycle history trail and richer promotion/deprecation workflow semantics.
- Target phase candidate: `Phase 5`.

### TD-018
- Why it matters: investigation workflows still cannot filter summaries by specific rule-hit criteria.
- Suggested resolution direction: add rule-hit-based retrieval filter with query-performance guardrails and coverage.
- Target phase candidate: `Phase 5`.

### TD-021
- Why it matters: enterprise identity posture is partially implemented; hardened JWT path exists, but full enterprise rollout still depends on IdP integration and stricter token validation policy.
- Current state: hardened/production JWT resource-server path is implemented and test-covered.
- Suggested resolution direction: complete live IdP rollout contract and tighten token validation policy (issuer/audience).
- Target phase candidate: `Phase 5`.

### TD-022
- Why it matters: `GET /api/admin/rules` currently returns an unbounded list, which can degrade governance-read performance and operator UX as versions grow.
- Suggested resolution direction: add pageable governance-read contract (`page`, `size`, optional sort) with backward-compatible defaults.
- Target phase candidate: `Phase 5`.

### TD-023
- Why it matters: velocity semantics should generally consider prior transactions only; current `.abs()` logic allows later/future-dated events to count, which can over-trigger review decisions under timestamp skew.
- Suggested resolution direction: count only transactions where `previousTimestamp <= currentTimestamp` and age is within the configured window.
- Target phase candidate: `Phase 5`.

### TD-024
- Why it matters: committed `.DS_Store` artifacts create noisy diffs and reduce submission polish.
- Suggested resolution direction: remove tracked `.DS_Store` files and extend `.gitignore` rules to prevent re-introduction.
- Target phase candidate: `Phase 5`.

### TD-025
- Why it mattered: reviewer-facing API metadata understated implemented scope and could create confusion during API inspection.
- Current state: closed in Sprint `5.1` by updating OpenAPI description to reflect current implemented scope.
- Resolution evidence: `src/main/java/com/capitec/fraudengine/infrastructure/config/OpenApiConfiguration.java`.

### TD-026
- Why it matters: duplicated secure-profile test credentials increase maintenance overhead and drift risk across integration suites.
- Suggested resolution direction: centralize shared secure-test credentials into a reusable test fixture/helper.
- Target phase candidate: `Phase 5`.

### TD-027
- Why it matters: hardened JWT configuration currently enforces `jwk-set-uri` presence but does not yet validate `issuer` and `audience` claims, which weakens production token trust boundaries.
- Suggested resolution direction: enforce issuer/audience validation in hardened JWT decoder setup and add negative integration tests for invalid issuer/audience tokens.
- Target phase candidate: `Phase 5`.

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
