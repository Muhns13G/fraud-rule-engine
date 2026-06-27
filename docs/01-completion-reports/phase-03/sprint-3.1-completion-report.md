# Sprint 3.1 Completion Report

## Sprint Summary
Sprint `3.1` moved Phase 3 from governance-read foundations into constrained governance mutation workflows while hardening retrieval correctness and validation usability. The sprint added metadata-only mutation capabilities, centralized fraud-threshold configuration, introduced paged retrieval responses, and closed retrieval edge cases around one-sided time bounds and deferred low-risk filters.

## Scope Completed
- `3.1.1` Governed state transitions:
  - added lifecycle/activation transition endpoint for governed rule metadata
  - enforced governance policy boundaries for valid/invalid transitions
  - kept runtime executable-rule logic non-mutable
- `3.1.2` Governed version registration:
  - added controlled version registration endpoint for existing rule codes
  - enforced `CODE_DEFINED` execution-source boundary
  - added not-found handling for unknown rule codes
- `3.1.3` Externalized thresholds/windows:
  - moved fraud-threshold and timing constants into validated configuration properties
  - kept defaults aligned with existing runtime behavior
  - surfaced active threshold configuration in governance visibility models
- `3.1.4` Paged retrieval contract:
  - changed `GET /api/fraud-evaluations` to return a paged summary response
  - added `page` and `size` request parameters
  - preserved existing filters and sort behavior
- `3.1.5` Retrieval edge-case hardening:
  - implemented one-sided time predicates (`from`-only and `to`-only)
  - added low-risk retrieval filters (`merchantCategory`, `channel`)
  - expanded integration coverage for new filter behavior

## Key Decisions Made
- Keep mutation scope constrained to governance metadata only; no runtime executable-rule mutation.
- Keep rule execution boundary code-defined while allowing governed metadata lifecycle/version operations.
- Evolve retrieval API shape to a paged contract for scalability and clearer review workflows.
- Resolve one-sided time-filter behavior at specification level instead of service-side branching.

## Technical Debt Addressed In Sprint 3.1
- `TD-001`: retrieval pagination support added.
- `TD-002`: one-sided date-range filtering fixed (`from`-only / `to`-only).
- `TD-012` / `TD-014`: governance mutation boundaries and deterministic state validation reinforced through constrained transition flows.
- `TD-015`: threshold/window constants externalized to validated configuration.
- `TD-018`: deferred low-risk retrieval filters (`merchantCategory`, `channel`) added.

## Technical Debt Remaining
- Retrieval still does not include rule-hit lookup filtering.
- Governance still lacks richer audit-history/event-stream style mutation tracking.
- Runtime rule execution remains intentionally code-defined (by design for current scope).

## File Inventory

| File | Status in Sprint 3.1 | Notes |
| --- | --- | --- |
| `src/main/java/com/oitws/fraudengine/api/controller/RuleGovernanceController.java` | Modified | Added governed mutation endpoints for state transitions and version registration |
| `src/main/java/com/oitws/fraudengine/application/service/RuleGovernanceMutationService.java` | Modified | Added/expanded constrained mutation use cases |
| `src/main/java/com/oitws/fraudengine/infrastructure/persistence/repository/RuleGovernanceMetadataJpaRepository.java` | Modified | Added query support for governed version operations |
| `src/main/resources/application.yaml` | Modified | Added validated threshold/window config defaults |
| `src/main/java/com/oitws/fraudengine/api/controller/FraudEvaluationController.java` | Modified | Added pagination and new retrieval filters |
| `src/main/java/com/oitws/fraudengine/application/service/FraudEvaluationRetrievalService.java` | Modified | Added paged retrieval, one-sided time handling path, and low-risk filter wiring |
| `src/main/java/com/oitws/fraudengine/infrastructure/persistence/repository/FraudEvaluationSpecifications.java` | Modified | Added one-sided time predicates and `merchantCategory`/`channel` predicates |
| `src/main/java/com/oitws/fraudengine/api/dto/FraudEvaluationSummaryPageResponseDto.java` | Created | Added paged response shape for retrieval summaries |
| `src/test/java/com/oitws/fraudengine/api/controller/FraudEvaluationControllerIntegrationTest.java` | Modified | Added pagination + one-sided time + new filter integration coverage |
| `src/test/java/com/oitws/fraudengine/api/controller/RuleGovernanceControllerIntegrationTest.java` | Modified | Added governance mutation/version workflow coverage |
| `docs/RAG/01-project-overview.md` | Modified | Updated current-state posture for Sprint 3.1 outcomes |
| `docs/RAG/02-decisions-log.md` | Modified | Recorded Sprint 3.1 strategy and retrieval decisions |
| `docs/RAG/03-api-scope.md` | Modified | Updated endpoint/filter contract for governance mutation and retrieval |
| `docs/RAG/05-architecture-current-vs-target.md` | Modified | Updated architecture snapshot with Sprint 3.1 capabilities |
| `docs/01-completion-reports/phase-03/sprint-3.1-completion-report.md` | Created | Sprint close-out report |

## Verification Summary
- `./mvnw test` passed after Sprint `3.1` changes.
- Test results: `67` tests run, `0` failures, `0` errors, `0` skipped.
- Additional focused verification completed during sprint delivery:
  - `./mvnw -Dtest=RuleGovernanceControllerIntegrationTest test`
  - `./mvnw -Dtest=FraudEvaluationControllerIntegrationTest test`

## Close-Out
Sprint `3.1` is complete. Phase 3 now has constrained governance mutation workflows, centralized rule-threshold configuration, and a more correct/scalable retrieval surface with pagination and one-sided time filtering.
