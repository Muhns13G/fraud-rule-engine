# Sprint 1.5 Completion Report

## Sprint Summary
Sprint `1.5` hardened the Phase 1 fraud-evaluation slice into a production-style increment. The sprint added focused unit and integration coverage, OpenAPI polish, containerization, a real public-facing `README`, and a final cleanup pass that improved request-error handling and aligned the documentation layer to the implemented system.

## Scope Completed
- Added unit-test coverage for:
  - high amount rule
  - velocity rule
  - risky merchant category rule
  - unusual time rule
  - aggregation policy
- Added integration coverage for:
  - repository persistence and query behavior
  - `POST /api/fraud-evaluations`
  - `GET /api/fraud-evaluations/{evaluationId}`
  - `GET /api/fraud-evaluations`
- Added OpenAPI and endpoint-contract polish:
  - explicit API metadata
  - schema annotations
  - documentation endpoint verification
- Added containerization and local-run polish:
  - multi-stage `Dockerfile`
  - `.dockerignore`
  - refined `compose.yaml`
  - pinned PostgreSQL image versions in Compose and Testcontainers
- Added the first real `README` with:
  - project purpose
  - architecture summary
  - local run and test instructions
  - Docker usage
  - API examples
  - explicit Phase 1 security posture
- Completed a final cleanup pass:
  - added clean `400` handling for unsupported request-body enum-like values
  - disabled `spring.jpa.open-in-view`
  - added transactional boundaries to retrieval mapping
  - refreshed stale RAG and blueprint current-state sections

## Key Decisions Made
- Keep Sprint `1.5` focused on hardening and presentation quality rather than widening fraud scope.
- Treat local Docker-backed verification as the authoritative close-out signal when sandbox Docker access is restricted.
- Keep Phase 1 security intentionally permissive and document that posture clearly rather than introducing partial auth.
- Pin PostgreSQL to `18.3` for continuity with the local environment that `latest` had already resolved to.
- Preserve `spring.jpa.open-in-view = false` and fix the resulting lazy-loading issue with service-level read-only transactions rather than backing out the config improvement.
- Improve request-body enum normalization failures with explicit `400` responses instead of relying on generic `500` handling.

## Deviations From Original Plan
- A small shared test fixture helper was introduced in `1.5.1` to reduce duplication across unit tests.
- `1.5.2` exposed a real application defect: the fraud rules were not registered as Spring beans for the API path. The fix was folded into that sprint because the new integration tests surfaced it.
- `1.5.3` required a follow-on fix to ensure Flyway migrations ran consistently in tests and slice contexts.
- `1.5.6` surfaced a lazy-loading failure after `open-in-view` was disabled. The final solution was to add read-only transactional boundaries to the retrieval service.
- The generated Spring Security password warning still appears in local test logs despite the cleanup attempt; that item remains unresolved and is carried as residual technical debt rather than hidden.

## Lessons Learned
- The separation established in Sprints `1.3` and `1.4` made it straightforward to add unit, repository, and API tests without major refactoring.
- Docker-backed verification is essential in this repo; sandbox-only failures should not override successful local evidence when the project explicitly depends on Testcontainers.
- `open-in-view` cleanup is worth doing, but it has to be paired with explicit transactional boundaries where JPA entity graphs are mapped after retrieval.
- The final public-facing polish work uncovered real issues, not just cosmetic ones, especially around error handling and persistence fetch boundaries.

## Technical Debt Accrued
- The generated Spring Security password warning still appears during local test startup and should be cleaned up in a future pass.
- SpringDoc still logs production-exposure warnings because the API docs remain enabled by default for the current slice.
- Mockito still emits the Java 25 dynamic-agent warning during tests.
- Retrieval still relies on repository-method combinations rather than a more scalable query abstraction if filter growth is needed later.
- Thresholds and the velocity history window are still code-level constants rather than centralized configuration.
- Dynamic rule management, richer observability, and CI remain out of scope for Phase 1.

## Future Considerations
- Remove the lingering generated-password startup warning so verification logs are cleaner.
- Decide whether SpringDoc should become profile-specific after the public review path is no longer the primary audience.
- Centralize fraud thresholds and time-window configuration.
- Add profile-specific security if the project moves beyond the intentionally open Phase 1 posture.
- Expand retrieval filters and audit/observability depth only after the current slice’s simplicity is no longer a priority.
- Revisit deferred heuristics such as `location anomaly` only when the supporting history and explanation model are ready.

## File Inventory

| File | Status in Sprint 1.5 | Notes |
| --- | --- | --- |
| `src/test/java/com/oitws/fraudengine/support/DomainTestFixtures.java` | Created | Shared domain-level fixture helper for unit tests |
| `src/test/java/com/oitws/fraudengine/domain/rule/impl/HighAmountFraudRuleTest.java` | Created | Unit tests for high amount thresholds |
| `src/test/java/com/oitws/fraudengine/domain/rule/impl/VelocityFraudRuleTest.java` | Created | Unit tests for velocity behavior and time-window semantics |
| `src/test/java/com/oitws/fraudengine/domain/rule/impl/RiskyMerchantCategoryFraudRuleTest.java` | Created | Unit tests for flagged merchant categories |
| `src/test/java/com/oitws/fraudengine/domain/rule/impl/UnusualTimeFraudRuleTest.java` | Created | Unit tests for overnight-window boundaries |
| `src/test/java/com/oitws/fraudengine/domain/policy/FraudDecisionPolicyTest.java` | Created | Unit tests for decision aggregation |
| `src/test/java/com/oitws/fraudengine/api/controller/FraudEvaluationControllerIntegrationTest.java` | Created, then modified | API integration tests plus OpenAPI and error-handling verification |
| `src/test/java/com/oitws/fraudengine/infrastructure/persistence/repository/FraudEvaluationJpaRepositoryIntegrationTest.java` | Created, then modified | Repository integration tests with Flyway-backed Postgres |
| `src/test/java/com/oitws/fraudengine/TestcontainersConfiguration.java` | Modified | Made public for reuse and pinned PostgreSQL image to `18.3` |
| `src/main/java/com/oitws/fraudengine/domain/rule/impl/HighAmountFraudRule.java` | Modified | Registered as Spring bean for integration path |
| `src/main/java/com/oitws/fraudengine/domain/rule/impl/VelocityFraudRule.java` | Modified | Registered as Spring bean for integration path |
| `src/main/java/com/oitws/fraudengine/domain/rule/impl/RiskyMerchantCategoryFraudRule.java` | Modified | Registered as Spring bean for integration path |
| `src/main/java/com/oitws/fraudengine/domain/rule/impl/UnusualTimeFraudRule.java` | Modified | Registered as Spring bean for integration path |
| `src/main/java/com/oitws/fraudengine/infrastructure/config/OpenApiConfiguration.java` | Created | OpenAPI metadata configuration |
| `src/main/java/com/oitws/fraudengine/infrastructure/config/FlywayConfiguration.java` | Created | Explicit Flyway bootstrap for app and tests |
| `src/main/java/com/oitws/fraudengine/api/controller/FraudEvaluationController.java` | Modified | Added OpenAPI documentation polish |
| `src/main/java/com/oitws/fraudengine/api/dto/FraudEvaluationRequestDto.java` | Modified | Added schema metadata |
| `src/main/java/com/oitws/fraudengine/api/dto/FraudEvaluationResponseDto.java` | Modified | Added schema metadata |
| `src/main/java/com/oitws/fraudengine/api/dto/FraudEvaluationSummaryResponseDto.java` | Modified | Added schema metadata |
| `src/main/java/com/oitws/fraudengine/api/dto/RuleResultResponseDto.java` | Modified | Added schema metadata |
| `src/main/java/com/oitws/fraudengine/api/dto/LocationDto.java` | Modified | Added schema metadata |
| `src/main/java/com/oitws/fraudengine/api/error/ApiErrorResponse.java` | Modified | Added schema metadata |
| `src/main/java/com/oitws/fraudengine/application/mapper/FraudEvaluationApplicationMapper.java` | Modified | Added explicit invalid-value handling |
| `src/main/java/com/oitws/fraudengine/api/error/GlobalExceptionHandler.java` | Modified | Added `400` handling for unsupported request-body values |
| `src/main/java/com/oitws/fraudengine/application/service/FraudEvaluationRetrievalService.java` | Modified | Added transactional boundaries for retrieval mapping |
| `src/main/java/com/oitws/fraudengine/common/error/InvalidRequestValueException.java` | Created | Shared invalid-request exception |
| `src/main/java/com/oitws/fraudengine/common/error/package-info.java` | Created | Common error package marker |
| `src/main/resources/application.yaml` | Modified | Disabled `open-in-view` and retained Flyway config |
| `Dockerfile` | Created | Multi-stage container build |
| `.dockerignore` | Created | Leaner Docker build context |
| `compose.yaml` | Modified | Refined local Postgres setup and pinned image to `18.3` |
| `README.md` | Created, then modified | Public-facing project documentation with final cleanup alignment |
| `docs/RAG/01-project-overview.md` | Modified | Updated current-state baseline |
| `docs/RAG/05-architecture-current-vs-target.md` | Modified | Updated current-state architecture summary |
| `docs/blueprints/01-project-blueprint.md` | Modified | Updated current state and immediate gaps to match the implemented slice |

## RAG Update Summary
- `docs/RAG/01-project-overview.md` was updated to reflect that the Phase 1 slice, Dockerfile, README, and test layers now exist.
- `docs/RAG/05-architecture-current-vs-target.md` was updated so the “Current State” section no longer describes the repo as a skeleton.
- `docs/blueprints/01-project-blueprint.md` was updated so “Verified Current State” and “Immediate Gaps” reflect the implemented system rather than the pre-build state.
- No Phase 1 scope decisions changed; the updates were alignment work, not scope expansion.

## Verification Summary
- Local `./mvnw test` completed successfully on May 12, 2026 for Sprint `1.5.1`, with `22` passing tests.
- Local `./mvnw clean install` completed successfully on May 12, 2026 during the early hardening phase.
- Local `./mvnw test` completed successfully on May 13, 2026 after the full Sprint `1.5` work, with `32` passing tests.
- Local `docker build -t fraud-rule-engine:local .` completed successfully during Sprint `1.5.4`.
- The final chosen PostgreSQL pin remains `18.3`; a brief fallback to `17.9` during later local-run troubleshooting was not kept once the PostgreSQL `18+` Compose volume layout was corrected.
- Sandbox `./mvnw test` failures during the sprint were caused by Docker access restrictions and were not treated as authoritative sprint-close evidence.

## Close-Out
Sprint `1.5` is complete. The Phase 1 slice is now tested, documented, containerized, OpenAPI-enabled, and portfolio-ready. The repo also has a clear residual-debt list for any future post-release hardening, but no remaining Sprint `1.5` tasks block close-out.
