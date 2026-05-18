# Sprint 2.3 Completion Report

## Sprint Summary
Sprint `2.3` improved observability and operational clarity for the fraud-evaluation slice without changing the fraud rules or outward business behavior. The sprint introduced structured service and error logging, domain-relevant Micrometer metrics, a tighter and more useful local actuator posture, lightweight request correlation via `X-Request-Id`, and the corresponding documentation updates across the reviewer-facing and architecture-current-state docs.

## Scope Completed
- Added structured logging improvements around the core evaluation and retrieval flows:
  - fraud evaluation start
  - velocity-history lookup
  - fraud evaluation completion
  - retrieval-by-id lookups
  - summary-query execution
  - request validation and parameter parsing failures
  - request-body normalization failures
  - not-found responses
  - unexpected request-processing failures
- Added domain-relevant Micrometer metrics for the core evaluation path:
  - `fraud.evaluation.completed.total`
  - `fraud.evaluation.decision.count`
  - `fraud.evaluation.rule.triggered.count`
  - `fraud.evaluation.duration`
- Refined actuator exposure and health behavior for local/reviewer usage:
  - exposed `health`, `info`, and `metrics`
  - enabled health detail visibility
  - enabled health probes
- Added lightweight request-correlation readiness:
  - accepts or generates `X-Request-Id`
  - includes the request ID in the response header
  - propagates the request ID into the log MDC
  - includes the request ID in the console log pattern
- Updated the observability model in `README`, RAG, and roadmap docs.

## Key Decisions Made
- Keep observability additions lightweight and easy to explain rather than introducing full tracing or production-grade telemetry infrastructure.
- Use stable `key=value` log messages so the evaluation flow is readable in plain local logs without requiring JSON log infrastructure.
- Put metrics only on the core evaluation flow for now, rather than instrumenting every path.
- Expose a small local actuator surface (`health`, `info`, `metrics`) instead of the entire actuator catalog.
- Choose `X-Request-Id` correlation via a single servlet filter and MDC propagation as the simplest traceability improvement with high reviewer value.

## Deviations From Original Plan
- No additional custom health contributors were added in this sprint because the actuator exposure and health configuration changes already improved local inspection enough for the current repo size.
- Request-trace readiness was implemented through lightweight correlation IDs rather than broader tracing or observation infrastructure.
- Observability verification stayed at compile-level confirmation in the sandbox because Docker-backed end-to-end runtime verification is environment-sensitive here.

## Lessons Learned
- A small amount of thoughtfully placed logging adds much more value than broad, noisy instrumentation.
- Request correlation becomes immediately useful once structured evaluation and error logs already exist; the two changes reinforce each other well.
- Actuator usefulness comes as much from deliberate exposure choices as from adding new instrumentation.
- For a take-home repo, focused metrics and simple correlation IDs communicate better judgment than a larger but thinner observability stack.

## Technical Debt Accrued
- The metrics added in this sprint are evaluation-path only; retrieval and error-path metrics remain intentionally out of scope.
- The console logging pattern is now explicitly customized for request correlation and may need revisiting later if profile-specific logging conventions are introduced.
- There is still no dedicated automated test coverage for actuator exposure, metric publication, or request-correlation headers.
- Generated Spring Security password and SpringDoc exposure warnings remain separate residual cleanup items outside this sprint.

## Future Considerations
- Add targeted tests for `X-Request-Id` propagation and any future actuator contract decisions.
- Consider surfacing a reviewer-friendly actuator metrics example in the README once there is a stable sample run to reference.
- Extend metrics to retrieval workflows only if there is a clear operator need, not by default.
- Revisit whether any actuator endpoints should become profile-specific once the repo is no longer optimized for reviewer-local usability.

## File Inventory

| File | Status in Sprint 2.3 | Notes |
| --- | --- | --- |
| `src/main/java/com/capitec/fraudengine/application/service/FraudEvaluationService.java` | Modified | Added structured evaluation logs and Micrometer evaluation metrics |
| `src/main/java/com/capitec/fraudengine/application/service/FraudEvaluationRetrievalService.java` | Modified | Added structured retrieval logs |
| `src/main/java/com/capitec/fraudengine/api/error/GlobalExceptionHandler.java` | Modified | Added structured request-failure logging |
| `src/main/java/com/capitec/fraudengine/infrastructure/config/RequestCorrelationFilter.java` | Created | Added request-correlation ID propagation and response header support |
| `src/main/resources/application.yaml` | Modified | Added actuator exposure/health config and request-aware console log pattern |
| `README.md` | Modified | Documented observability behavior, actuator endpoints, request correlation, and metric surface |
| `docs/RAG/01-project-overview.md` | Modified | Updated current operational posture |
| `docs/RAG/05-architecture-current-vs-target.md` | Modified | Updated current-state architecture with observability conventions |
| `docs/blueprints/02-development-roadmap.md` | Modified | Updated roadmap wording to reflect the new lightweight observability baseline |

## RAG Update Summary
- `docs/RAG/01-project-overview.md` now reflects that request correlation, focused metrics, and limited actuator exposure exist in the current baseline.
- `docs/RAG/05-architecture-current-vs-target.md` now records the current observability conventions instead of leaving the operational story implicit.
- `docs/blueprints/02-development-roadmap.md` now treats the current observability layer as a lightweight baseline that later production work should extend rather than introduce from scratch.

## Verification Summary
- Sprint `2.3` file audit against `docs/implementation-plans/phase-02/sprint-2.3.md` found the planned work present across logging, metrics, actuator/health behavior, request correlation, and documentation.
- One small documentation drift was found during the audit: the README security/actuator access note still only mentioned `/actuator/health`. That was corrected before close-out so the current local actuator surface is documented consistently.
- `./mvnw -DskipTests compile` passed after the Sprint `2.3` implementation.
- Sandbox full integration/runtime verification was not used as the close-out signal because local reviewer paths for this repo depend on Docker-backed services.

## Close-Out
Sprint `2.3` is complete. The service now has a lightweight but credible observability baseline: structured logs, request correlation, domain-relevant metrics, and a deliberate local actuator surface, all documented in the current-state docs and reviewer-facing README.
