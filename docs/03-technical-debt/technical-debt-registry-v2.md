# Technical Debt Registry v2 (Active Items Only, Post-Phase 4)

## Scope
This `v2` registry includes only **active** technical debt after Phase 4 close-out.
- Ingested sources:
  - `docs/03-technical-debt/technical-debt-registry-v1.md`
  - all completion reports from Phase 1 to Phase 4
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

## Active Debt Table

| Debt ID | Title | Status | Severity | Source | Evidence |
| --- | --- | --- | --- | --- | --- |
| TD-003 | Default profile remains intentionally open for API surface | Open | Medium | Recorded+Observed | `src/main/java/com/capitec/fraudengine/infrastructure/security/PhaseOneSecurityConfiguration.java`, `docs/01-completion-reports/phase-04/sprint-4.4-completion-report.md` |
| TD-005 | No enterprise secret management/rotation orchestration for secure credentials | Partially addressed | Medium | Recorded+Observed | `src/main/resources/application.yaml`, `src/main/java/com/capitec/fraudengine/infrastructure/security/SecureProfileSecretSupplier.java`, `docs/01-completion-reports/phase-04/sprint-4.2-completion-report.md` |
| TD-012 | Rule governance workflow maturity remains limited (beyond basic mutation endpoints) | Partially addressed | Medium | Recorded+Observed | `src/main/java/com/capitec/fraudengine/api/controller/RuleGovernanceController.java`, `src/main/java/com/capitec/fraudengine/application/service/RuleGovernanceMutationService.java` |
| TD-014 | Promotion/deprecation operational workflow and history/audit depth are incomplete | Partially addressed | Medium | Recorded | `src/main/java/com/capitec/fraudengine/domain/policy/RuleGovernancePolicy.java`, `docs/01-completion-reports/phase-02/sprint-2.5-completion-report.md` |
| TD-018 | Retrieval still lacks rule-hit lookup filter for investigation workflows | Partially addressed | Low | Recorded | `docs/RAG/03-api-scope.md`, `docs/01-completion-reports/phase-01/sprint-1.1-completion-report.md` |
| TD-021 | Enterprise IAM/JWT/OAuth2 integration remains deferred | Open | Medium | Recorded+Observed | `docs/01-completion-reports/phase-04/sprint-4.1-completion-report.md`, `docs/00-blueprints/02-development-roadmap.md` |
| TD-022 | Governance list endpoint has no pagination | Open | Medium | Observed | `src/main/java/com/capitec/fraudengine/api/controller/RuleGovernanceController.java`, `src/main/java/com/capitec/fraudengine/application/service/RuleGovernanceRetrievalService.java` |
| TD-023 | Velocity rule counts future-dated transactions due to absolute time-difference logic | Open | Medium | Observed | `src/main/java/com/capitec/fraudengine/domain/rule/impl/VelocityFraudRule.java`, `src/test/java/com/capitec/fraudengine/domain/rule/impl/VelocityFraudRuleTest.java` |
| TD-024 | Repository includes `.DS_Store` artifacts in source/docs trees | Open | Low | Observed | `docs/.DS_Store`, `docs/00-blueprints/.DS_Store`, `docs/01-completion-reports/.DS_Store`, `src/main/java/.DS_Store`, `src/main/resources/.DS_Store`, `src/test/java/.DS_Store` |
| TD-025 | OpenAPI metadata still describes a "Phase 1 API" despite expanded scope | Open | Low | Observed | `src/main/java/com/capitec/fraudengine/infrastructure/config/OpenApiConfiguration.java` |
| TD-026 | Secure-profile test credentials are duplicated across multiple integration tests | Open | Low | Observed | `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileSecurityIntegrationTest.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileGovernanceAuthorizationIntegrationTest.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileDatasourceResilienceIntegrationTest.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileGovernanceAdminIntegrationTest.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfilePlatformAdminIntegrationTest.java`, `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileSecurityDiagnosticsIntegrationTest.java` |

---

## Detailed Entries

### TD-003
- Why it matters: `default` profile permissiveness is useful for reviewers but unsafe for non-local deployment if profile control drifts.
- Suggested resolution direction: enforce explicit non-local profile boot rules and move production-like runs to authenticated profiles only.
- Target phase candidate: `Phase 5`.

### TD-005
- Why it matters: secret-source strategy exists, but centralized enterprise rotation and secret-manager operations are not fully integrated.
- Suggested resolution direction: implement a concrete secret provider adapter and operational rotation runbook with overlap validation.
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
- Why it matters: enterprise identity posture (JWT/OAuth2/IAM) is deferred; secure profile relies on current basic profile strategy.
- Suggested resolution direction: introduce standards-based token authn/authz integration for non-local hardened modes.
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
- Why it matters: reviewer-facing API metadata understates implemented scope and can create confusion during API inspection.
- Suggested resolution direction: update OpenAPI description/version text to reflect current post-Phase-4 capabilities.
- Target phase candidate: `Phase 5`.

### TD-026
- Why it matters: duplicated secure-profile test credentials increase maintenance overhead and drift risk across integration suites.
- Suggested resolution direction: centralize shared secure-test credentials into a reusable test fixture/helper.
- Target phase candidate: `Phase 5`.

---

## Reconciliation Notes
- `v2` intentionally excludes all debt items already closed in `v1`.
- `v2` includes one newly formalized active item (`TD-021`) derived from explicit Phase 4 deferral notes.
