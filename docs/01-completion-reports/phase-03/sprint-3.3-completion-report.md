# Sprint 3.3 Completion Report

## Sprint Summary
Sprint `3.3` deepened operational observability across evaluation, retrieval, governance, and error paths, then introduced a deterministic `LOCATION_ANOMALY` fraud rule with explicit explainability evidence. The sprint closes the major observability and deferred location-anomaly gaps carried from earlier phases.

## Scope Completed
- `3.3.1` Observability contract tests:
  - added end-to-end assertions for core evaluation metric emission
  - added request-correlation contract assertions for valid and invalid `X-Request-Id` inputs
- `3.3.2` Expanded metric coverage:
  - added retrieval metrics for fraud-evaluation and governance read operations
  - added governance mutation metrics for state transition and version registration
  - added structured API error counters by status and exception type
- `3.3.3` Deterministic location anomaly rule:
  - implemented `LOCATION_ANOMALY` rule based on most recent comparable prior transaction location
  - added configurable behavior via `FraudRuleProperties` (`scoreContribution`, `compareCityWhenCountryMatches`)
  - enriched governance configuration read model for new rule configuration visibility
- `3.3.4` Governance mutation observability:
  - added structured audit events for lifecycle transitions and version registrations
  - added dedicated lifecycle/version mutation counters
  - correlated governance mutation audit events with request-id (MDC) and actor identity (security context)

## Key Decisions Made
- Keep observability additive and high-signal: do not add broad/noisy metrics that do not improve operator outcomes.
- Keep `LOCATION_ANOMALY` deterministic by comparing only against the most recent comparable prior transaction location.
- Keep explainability explicit in rule results (`reason`) rather than introducing new response contract fields.
- Treat governance mutation observability as a first-class surface: capture both aggregate counters and structured audit events.

## Technical Debt Impact
- `TD-010` Closed: observability contract tests are now explicit for metrics and request-correlation behavior.
- `TD-011` Closed: metrics coverage is now broader than evaluation-only and includes retrieval, governance, and API-error paths.
- `TD-017` Closed: deferred `location anomaly` heuristic is now implemented with deterministic behavior and tests.

## File Inventory

| File | Status in Sprint 3.3 | Notes |
| --- | --- | --- |
| `src/test/java/com/oitws/fraudengine/api/controller/ObservabilityContractIntegrationTest.java` | Modified | Added/expanded observability contract assertions for evaluation, retrieval, governance, and error metrics plus request-correlation behavior |
| `src/main/java/com/oitws/fraudengine/application/service/FraudEvaluationRetrievalService.java` | Modified | Added retrieval request metrics |
| `src/main/java/com/oitws/fraudengine/application/service/RuleGovernanceRetrievalService.java` | Modified | Added governance retrieval metrics |
| `src/main/java/com/oitws/fraudengine/application/service/RuleGovernanceMutationService.java` | Modified | Added mutation counters + lifecycle/version observability counters + structured audit events with actor/request-id context |
| `src/main/java/com/oitws/fraudengine/api/error/GlobalExceptionHandler.java` | Modified | Added API error counters by status/exception |
| `src/main/java/com/oitws/fraudengine/domain/rule/impl/LocationAnomalyFraudRule.java` | Created | New deterministic location anomaly fraud rule |
| `src/main/java/com/oitws/fraudengine/infrastructure/config/FraudRuleProperties.java` | Modified | Added location anomaly configuration section |
| `src/main/resources/application.yaml` | Modified | Added location anomaly property bindings/defaults |
| `src/main/java/com/oitws/fraudengine/application/service/RuleGovernanceConfigurationReadModelService.java` | Modified | Added configuration read-model mapping for `LOCATION_ANOMALY` |
| `src/test/java/com/oitws/fraudengine/domain/rule/impl/LocationAnomalyFraudRuleTest.java` | Created | Unit coverage for deterministic location anomaly behavior |
| `src/test/java/com/oitws/fraudengine/api/controller/FraudEvaluationControllerIntegrationTest.java` | Modified | Adjusted rule-result count expectation to include new rule |
| `docs/RAG/01-project-overview.md` | Modified | Updated Sprint 3.3 current-state summary |
| `docs/RAG/02-decisions-log.md` | Modified | Recorded Sprint 3.3 decisions |
| `docs/RAG/03-api-scope.md` | Modified | Updated rule surface notes |
| `docs/RAG/05-architecture-current-vs-target.md` | Modified | Updated observability and architecture state |
| `docs/03-technical-debt/technical-debt-registry-v1.md` | Modified | Updated TD closure statuses and notes |
| `docs/01-completion-reports/phase-03/sprint-3.3-completion-report.md` | Created | Sprint close-out report |

## Verification Summary
- `./mvnw -Dtest=ObservabilityContractIntegrationTest test` passed.
- `./mvnw -Dtest=RuleGovernanceControllerIntegrationTest,FraudEvaluationControllerIntegrationTest test` passed.
- `./mvnw -Dtest=LocationAnomalyFraudRuleTest,FraudEvaluationControllerIntegrationTest,RuleGovernanceMetadataBootstrapServiceIntegrationTest test` passed.
- `./mvnw -Dtest=ObservabilityContractIntegrationTest,RuleGovernanceControllerIntegrationTest test` passed after `3.3.4` observability updates.

## Close-Out
Sprint `3.3` is complete. Observability is now deeper and contract-tested across core operational paths, governance mutation actions now emit structured and measurable audit signals, and the deferred location-anomaly capability has been implemented in a deterministic, explainable, and configurable way.
