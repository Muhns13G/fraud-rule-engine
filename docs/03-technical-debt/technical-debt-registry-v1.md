# Technical Debt Registry (Phase 2 Close-Out, Updated Through Sprint 3.4)

## Baseline and Scope
This registry is the canonical debt register at Phase 2 close-out. It is built from:
- all 10 completion reports (historical baseline), and
- a targeted live code/doc audit pass (current-state validation and additional observed debt).

Audit mode:
- Scope: `Reports + Observed`
- Output: `Debt registry only`

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

## Normalized Audit Table

| Debt ID | Title | Source | First Reported Sprint | Current Status | Severity | Evidence Path |
| --- | --- | --- | --- | --- | --- | --- |
| TD-001 | Retrieval endpoint has no pagination | Recorded+Observed | 2.2 | Closed | High | `docs/01-completion-reports/phase-02/sprint-2.2-completion-report.md`, `src/main/java/com/capitec/fraudengine/api/controller/FraudEvaluationController.java` |
| TD-002 | Single-bound time filtering (`from` only / `to` only) is not applied | Observed | N/A | Closed | Medium | `src/main/java/com/capitec/fraudengine/infrastructure/persistence/repository/FraudEvaluationSpecifications.java` |
| TD-003 | Default profile remains intentionally open for API surface | Recorded+Observed | 1.4 | Open | Medium | `docs/01-completion-reports/phase-01/sprint-1.4-completion-report.md`, `src/main/java/com/capitec/fraudengine/infrastructure/security/PhaseOneSecurityConfiguration.java` |
| TD-004 | Secure profile uses in-memory user store only | Recorded+Observed | 2.4 | Closed | Medium | `docs/01-completion-reports/phase-02/sprint-2.4-completion-report.md`, `src/main/java/com/capitec/fraudengine/infrastructure/security/SecureProfileSecurityConfiguration.java` |
| TD-005 | No enterprise secret management/rotation for secure credentials | Recorded+Observed | 2.4 | Partially addressed | Medium | `docs/01-completion-reports/phase-02/sprint-2.4-completion-report.md`, `src/main/resources/application.yaml`, `README.md` |
| TD-006 | Default-profile openness behavior is not explicitly tested | Recorded | 2.4 | Closed | Low | `docs/01-completion-reports/phase-02/sprint-2.4-completion-report.md`, `src/test/java/com/capitec/fraudengine/api/controller/DefaultProfileSecurityIntegrationTest.java` |
| TD-007 | Generated Spring Security password warning still appears in logs | Recorded+Observed | 1.5 | Partially addressed | Low | `docs/01-completion-reports/phase-01/sprint-1.5-completion-report.md`, `src/main/java/com/capitec/fraudengine/infrastructure/security/PhaseOneSecurityConfiguration.java` |
| TD-008 | SpringDoc production-exposure warning remains unresolved | Recorded+Observed | 1.5 | Closed | Low | `docs/01-completion-reports/phase-01/sprint-1.5-completion-report.md`, `src/main/resources/application.yaml`, `README.md` |
| TD-009 | Mockito Java 25 dynamic-agent warning remains unresolved | Recorded | 1.5 | Closed | Low | `docs/01-completion-reports/phase-01/sprint-1.5-completion-report.md`, `pom.xml` |
| TD-010 | Observability tests are incomplete (metrics/correlation/actuator contract) | Recorded+Observed | 2.3 | Closed | Medium | `docs/01-completion-reports/phase-02/sprint-2.3-completion-report.md`, `src/test/java/com/capitec/fraudengine/api/controller/ObservabilityContractIntegrationTest.java` |
| TD-011 | Metrics coverage is evaluation-focused; retrieval/error metrics remain limited | Recorded+Observed | 2.3 | Closed | Low | `docs/01-completion-reports/phase-02/sprint-2.3-completion-report.md`, `src/main/java/com/capitec/fraudengine/application/service/FraudEvaluationRetrievalService.java`, `src/main/java/com/capitec/fraudengine/application/service/RuleGovernanceMutationService.java`, `src/main/java/com/capitec/fraudengine/api/error/GlobalExceptionHandler.java` |
| TD-012 | Rule governance is read-only; mutation endpoints/workflows are deferred | Recorded+Observed | 2.5 | Partially addressed | Medium | `docs/01-completion-reports/phase-02/sprint-2.5-completion-report.md`, `src/main/java/com/capitec/fraudengine/api/controller/RuleGovernanceController.java` |
| TD-013 | Governance metadata versioning is bootstrap-fixed at `1.0.0` | Recorded+Observed | 2.5 | Closed | Medium | `docs/01-completion-reports/phase-02/sprint-2.5-completion-report.md`, `src/main/java/com/capitec/fraudengine/application/service/RuleGovernanceMetadataBootstrapService.java` |
| TD-014 | No controlled governance promotion/deprecation workflow yet | Recorded | 2.5 | Partially addressed | Medium | `docs/01-completion-reports/phase-02/sprint-2.5-completion-report.md` |
| TD-015 | Thresholds/window constants still code-level, not centrally configurable | Recorded+Observed | 1.4 | Closed | Medium | `docs/01-completion-reports/phase-01/sprint-1.4-completion-report.md`, `src/main/java/com/capitec/fraudengine/domain/rule/impl/HighAmountFraudRule.java`, `src/main/java/com/capitec/fraudengine/application/service/FraudEvaluationService.java` |
| TD-016 | CI pipeline for compile/test/package is still missing | Recorded | 1.5 | Closed | Medium | `docs/01-completion-reports/phase-01/sprint-1.5-completion-report.md`, `.github/workflows/ci.yml` |
| TD-017 | Deferred `location anomaly` heuristic remains out of scope | Recorded | 1.1 | Closed | Low | `docs/01-completion-reports/phase-01/sprint-1.1-completion-report.md`, `src/main/java/com/capitec/fraudengine/domain/rule/impl/LocationAnomalyFraudRule.java` |
| TD-018 | Broader retrieval filters (`merchantCategory`, `channel`, rule-hit lookup) remain deferred | Recorded | 1.1 | Partially addressed | Low | `docs/01-completion-reports/phase-01/sprint-1.1-completion-report.md`, `docs/01-completion-reports/phase-02/sprint-2.2-completion-report.md` |
| TD-019 | Phase 1 planning debt (DTO/threshold/score/Flyway timing) | Recorded | 1.1 | Closed | Low | `docs/01-completion-reports/phase-01/sprint-1.1-completion-report.md`, `docs/01-completion-reports/phase-01/sprint-1.2-completion-report.md` |
| TD-020 | Placeholder-rule/repo/controller hardening debt from 1.3/1.4 | Recorded | 1.3 | Closed | Low | `docs/01-completion-reports/phase-01/sprint-1.3-completion-report.md`, `docs/01-completion-reports/phase-01/sprint-1.4-completion-report.md`, `docs/01-completion-reports/phase-01/sprint-1.5-completion-report.md` |

---

## Detailed Entries

### TD-001
- Debt ID: `TD-001`
- Title: Retrieval endpoint has no pagination
- Status: `Closed`
- Severity: `High`
- Source: `Recorded+Observed`
- Evidence:
  - `docs/01-completion-reports/phase-02/sprint-2.2-completion-report.md`
  - `src/main/java/com/capitec/fraudengine/api/controller/FraudEvaluationController.java`
- Why it matters: Unbounded list results are an operational and API-consumer scaling risk.
- Resolution note: Closed in Sprint `3.1.4` via paged retrieval contract (`page`, `size`, `totalElements`, `totalPages`).
- Suggested resolution direction: Introduce pageable query contract and response metadata.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.1`

### TD-002
- Debt ID: `TD-002`
- Title: Single-bound time filtering (`from` only / `to` only) is not applied
- Status: `Closed`
- Severity: `Medium`
- Source: `Observed`
- Evidence:
  - `src/main/java/com/capitec/fraudengine/infrastructure/persistence/repository/FraudEvaluationSpecifications.java`
- Why it matters: Filter behavior can be surprising and silently broader than caller intent.
- Resolution note: Closed in Sprint `3.1.5` via one-sided `>= from` and `<= to` predicates.
- Suggested resolution direction: Add `>= from` and `<= to` predicates for one-sided bounds.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.1`

### TD-003
- Debt ID: `TD-003`
- Title: Default profile remains intentionally open for API surface
- Status: `Open`
- Severity: `Medium`
- Source: `Recorded+Observed`
- Evidence:
  - `docs/01-completion-reports/phase-01/sprint-1.4-completion-report.md`
  - `src/main/java/com/capitec/fraudengine/infrastructure/security/PhaseOneSecurityConfiguration.java`
- Why it matters: Useful for reviewer ergonomics, but not suitable for non-local deployment posture.
- Suggested resolution direction: Keep local-open policy explicit while adding stricter non-local profile policy.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.2`

### TD-004
- Debt ID: `TD-004`
- Title: Secure profile uses in-memory user store only
- Status: `Closed`
- Severity: `Medium`
- Source: `Recorded+Observed`
- Evidence:
  - `docs/01-completion-reports/phase-02/sprint-2.4-completion-report.md`
  - `src/main/java/com/capitec/fraudengine/infrastructure/security/SecureProfileSecurityConfiguration.java`
- Why it matters: In-memory-only identity is brittle and not operationally realistic for secure mode.
- Resolution note: Closed by Sprint `4.2.2` with hardened JDBC identity configuration, safe default queries, and fail-fast query contract validation, backed by secure-config tests.
- Suggested resolution direction: Move secure profile identity source to externalized/persistent backing.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.2`

### TD-005
- Debt ID: `TD-005`
- Title: No enterprise secret management/rotation for secure credentials
- Status: `Partially addressed`
- Severity: `Medium`
- Source: `Recorded+Observed`
- Evidence:
  - `docs/01-completion-reports/phase-02/sprint-2.4-completion-report.md`
  - `src/main/resources/application.yaml`
- Why it matters: Plain env credentials alone are practical locally but incomplete for stronger operational controls.
- Resolution note: Sprint `4.2` added explicit secret-source strategies, external-secret integration seam, and credential-rotation overlap hooks. Still partial because concrete enterprise secret-manager integration and centralized rotation orchestration remain future work.
- Suggested resolution direction: Introduce profile-based secret provider integration for non-local environments.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.2`

### TD-006
- Debt ID: `TD-006`
- Title: Default-profile openness behavior is not explicitly tested
- Status: `Closed`
- Severity: `Low`
- Source: `Recorded`
- Evidence:
  - `docs/01-completion-reports/phase-02/sprint-2.4-completion-report.md`
- Why it matters: Important security-behavior contract is implicit rather than asserted by dedicated tests.
- Resolution note: Closed in Sprint `3.2.3` with explicit default-profile integration coverage.
- Suggested resolution direction: Add profile-targeted integration tests for default-open matrix.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.2`

### TD-007
- Debt ID: `TD-007`
- Title: Generated Spring Security password warning still appears in logs
- Status: `Partially addressed`
- Severity: `Low`
- Source: `Recorded+Observed`
- Evidence:
  - `docs/01-completion-reports/phase-01/sprint-1.5-completion-report.md`
  - `src/main/java/com/capitec/fraudengine/infrastructure/security/PhaseOneSecurityConfiguration.java`
- Why it matters: Noise in verification output and reviewer confusion around active security posture.
- Resolution note: Sprint `3.2.4` replaced exclusion-based handling with explicit profile beans to remove generated-user confusion paths; retain as partial until all startup contexts are consistently warning-free.
- Suggested resolution direction: Revisit auto-configuration exclusions and profile-specific security defaults.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.2`

### TD-008
- Debt ID: `TD-008`
- Title: SpringDoc production-exposure warning remains unresolved
- Status: `Closed`
- Severity: `Low`
- Source: `Recorded+Observed`
- Evidence:
  - `docs/01-completion-reports/phase-01/sprint-1.5-completion-report.md`
  - `src/main/resources/application.yaml`
  - `README.md`
- Why it matters: Acceptable for review mode but unresolved for tighter non-local defaults.
- Resolution note: Closed in Sprint `3.2.4` with explicit profile-driven Swagger/OpenAPI exposure defaults and documented override strategy.
- Suggested resolution direction: Add profile-specific docs endpoint exposure policy.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.2`

### TD-009
- Debt ID: `TD-009`
- Title: Mockito Java 25 dynamic-agent warning remains unresolved
- Status: `Closed`
- Severity: `Low`
- Source: `Recorded`
- Evidence:
  - `docs/01-completion-reports/phase-01/sprint-1.5-completion-report.md`
  - `pom.xml`
- Why it matters: Not functionally blocking, but creates repetitive test-log noise.
- Resolution note: Closed in Sprint `3.4.2` by adding the Mockito Java agent configuration to the Maven Surefire test runtime.
- Suggested resolution direction: Align test JVM/Mockito agent strategy with JDK 25 guidance.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.4`

### TD-010
- Debt ID: `TD-010`
- Title: Observability tests are incomplete (metrics/correlation/actuator contract)
- Status: `Closed`
- Severity: `Medium`
- Source: `Recorded+Observed`
- Evidence:
  - `docs/01-completion-reports/phase-02/sprint-2.3-completion-report.md`
  - `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileSecurityIntegrationTest.java`
- Why it matters: Some security actuator behavior is tested, but metrics/correlation contract verification remains thin.
- Resolution note: Closed in Sprint `3.3.1` with dedicated observability contract integration tests that assert metrics emission and request-correlation propagation behavior.
- Suggested resolution direction: Maintain contract tests as observability surface evolves.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.3`

### TD-011
- Debt ID: `TD-011`
- Title: Metrics coverage is evaluation-focused; retrieval/error metrics remain limited
- Status: `Closed`
- Severity: `Low`
- Source: `Recorded+Observed`
- Evidence:
  - `docs/01-completion-reports/phase-02/sprint-2.3-completion-report.md`
  - `src/main/java/com/capitec/fraudengine/application/service/FraudEvaluationService.java`
- Why it matters: Operational visibility is good for evaluate path but uneven across full API behavior.
- Resolution note: Closed in Sprint `3.3.2` by adding retrieval, governance mutation, and API error metrics with consistent naming/tags, then expanded in Sprint `3.3.4` with lifecycle/version mutation observability.
- Suggested resolution direction: Maintain naming/tag conventions and add only high-signal metrics.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.3`

### TD-012
- Debt ID: `TD-012`
- Title: Rule governance is read-only; mutation endpoints/workflows are deferred
- Status: `Partially addressed`
- Severity: `Medium`
- Source: `Recorded+Observed`
- Evidence:
  - `docs/01-completion-reports/phase-02/sprint-2.5-completion-report.md`
  - `src/main/java/com/capitec/fraudengine/api/controller/RuleGovernanceController.java`
- Why it matters: Governance visibility exists, but operational governance actions were initially absent.
- Resolution note: Sprint `3.1.1` and `3.1.2` introduced constrained mutation endpoints (state transition + version registration). Remaining work is deeper workflow/audit-history maturity.
- Suggested resolution direction: Add constrained mutation endpoints with explicit validation and auth controls.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.1`

### TD-013
- Debt ID: `TD-013`
- Title: Governance metadata versioning is bootstrap-fixed at `1.0.0`
- Status: `Closed`
- Severity: `Medium`
- Source: `Recorded+Observed`
- Evidence:
  - `docs/01-completion-reports/phase-02/sprint-2.5-completion-report.md`
  - `src/main/java/com/capitec/fraudengine/application/service/RuleGovernanceMetadataBootstrapService.java`
- Why it matters: Version management was originally not flexible or workflow-driven.
- Resolution note: Closed in Sprint `3.1.2` with controlled version registration endpoint for existing rule codes.
- Suggested resolution direction: Introduce controlled version registration/promotion model.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.1`

### TD-014
- Debt ID: `TD-014`
- Title: No controlled governance promotion/deprecation workflow yet
- Status: `Partially addressed`
- Severity: `Medium`
- Source: `Recorded`
- Evidence:
  - `docs/01-completion-reports/phase-02/sprint-2.5-completion-report.md`
- Why it matters: Lifecycle states exist but require controlled operational transitions.
- Resolution note: Sprint `3.1.1` introduced controlled transition APIs and policy checks; richer promotion/deprecation audit workflows remain future work.
- Suggested resolution direction: Define state-transition workflow and audit trail.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.1`

### TD-015
- Debt ID: `TD-015`
- Title: Thresholds/window constants are still code-level
- Status: `Closed`
- Severity: `Medium`
- Source: `Recorded+Observed`
- Evidence:
  - `docs/01-completion-reports/phase-01/sprint-1.4-completion-report.md`
  - `src/main/java/com/capitec/fraudengine/domain/rule/impl/HighAmountFraudRule.java`
  - `src/main/java/com/capitec/fraudengine/application/service/FraudEvaluationService.java`
- Why it matters: Operational tuning required code change/redeploy instead of controlled configuration updates.
- Resolution note: Closed in Sprint `3.1.3` by externalizing thresholds/windows into validated configuration properties.
- Suggested resolution direction: Centralize thresholds and windows in validated configuration properties.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.1`

### TD-016
- Debt ID: `TD-016`
- Title: CI pipeline for compile/test/package is still missing
- Status: `Closed`
- Severity: `Medium`
- Source: `Recorded`
- Evidence:
  - `docs/01-completion-reports/phase-01/sprint-1.5-completion-report.md`
  - `.github/workflows/ci.yml`
- Why it matters: No automated gate to protect quality and reproducibility across branches.
- Resolution note: Closed in Sprint `3.4.1` by introducing a baseline GitHub Actions workflow for compile, test, and package checks.
- Suggested resolution direction: Add minimal CI for build, tests, and packaging checks.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.4`

### TD-017
- Debt ID: `TD-017`
- Title: Deferred `location anomaly` heuristic remains out of scope
- Status: `Closed`
- Severity: `Low`
- Source: `Recorded`
- Evidence:
  - `docs/01-completion-reports/phase-01/sprint-1.1-completion-report.md`
- Why it matters: Known feature-gap versus broader fraud-domain expectations.
- Resolution note: Closed in Sprint `3.3.3` with deterministic `LOCATION_ANOMALY` rule behavior, explicit evidence in rule-result reasons, and targeted unit/integration coverage.
- Suggested resolution direction: Keep behavior deterministic and explicitly explainable.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.3`

### TD-018
- Debt ID: `TD-018`
- Title: Broader retrieval filters remain deferred (`merchantCategory`, `channel`, rule-hit lookup)
- Status: `Partially addressed`
- Severity: `Low`
- Source: `Recorded`
- Evidence:
  - `docs/01-completion-reports/phase-01/sprint-1.1-completion-report.md`
  - `docs/01-completion-reports/phase-02/sprint-2.2-completion-report.md`
- Why it matters: Review API can be too narrow for investigator workflows.
- Resolution note: Sprint `3.1.5` added `merchantCategory` and `channel`; only rule-hit lookup remains deferred.
- Suggested resolution direction: Expand filters incrementally with query-performance guardrails.
- Target phase/sprint candidate: `Phase 3 / Sprint 3.1`

### TD-019
- Debt ID: `TD-019`
- Title: Phase 1 planning debt (DTO/threshold/score/Flyway timing)
- Status: `Closed`
- Severity: `Low`
- Source: `Recorded`
- Evidence:
  - `docs/01-completion-reports/phase-01/sprint-1.1-completion-report.md`
  - `docs/01-completion-reports/phase-01/sprint-1.2-completion-report.md`
- Why it matters: Historical debt was resolved before implementation began.
- Suggested resolution direction: None (retain as closure record).
- Target phase/sprint candidate: `N/A`

### TD-020
- Debt ID: `TD-020`
- Title: Placeholder-rule/repo/controller hardening debt from 1.3/1.4
- Status: `Closed`
- Severity: `Low`
- Source: `Recorded`
- Evidence:
  - `docs/01-completion-reports/phase-01/sprint-1.3-completion-report.md`
  - `docs/01-completion-reports/phase-01/sprint-1.4-completion-report.md`
  - `docs/01-completion-reports/phase-01/sprint-1.5-completion-report.md`
- Why it matters: Historical debt resolved by Sprint 1.5 hardening and test coverage.
- Suggested resolution direction: None (retain as closure record).
- Target phase/sprint candidate: `N/A`

---

## Reconciliation Notes
- Deduplication was performed by behavior (not wording).
- Source labels:
  - `Recorded`: found in completion reports only.
  - `Observed`: found in live scan only.
  - `Recorded+Observed`: historically recorded and still visible in current code/docs.
- Status labels:
  - `Open`: active and unresolved.
  - `Partially addressed`: some progress exists but debt is not fully closed.
  - `Closed`: historical debt that is now resolved.

## Validation Checklist
- Completion reports referenced: **10/10**
- Every debt entry includes at least one concrete evidence path: **Yes**
- Duplicate/conflicting debt items: **None found after reconciliation**
- Every debt entry has explicit status (`Open`, `Partially addressed`, `Closed`): **Yes**
- Registry phase-planning readiness: **Yes** (sortable by severity and target phase/sprint)
