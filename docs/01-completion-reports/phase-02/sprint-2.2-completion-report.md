# Sprint 2.2 Completion Report

## Sprint Summary
Sprint `2.2` expanded retrieval and review usability beyond the original Phase 1 minimum without turning the service into a generic reporting surface. The sprint added a small, defensible set of new review filters, refactored retrieval querying to a cleaner JPA specification approach, introduced deliberate list sorting, extended controller integration coverage for the richer retrieval behavior, and aligned the documentation layer to the implemented API surface.

## Scope Completed
- Added the next approved retrieval filters to `GET /api/fraud-evaluations`:
  - `customerId`
  - `transactionId`
- Kept the existing retrieval filters in place:
  - `decision`
  - `accountId`
  - `from`
  - `to`
- Refactored retrieval query handling away from service-level branching and repository-method explosion.
- Introduced explicit list sorting support with:
  - `NEWEST_FIRST`
  - `OLDEST_FIRST`
- Preserved the existing list response shape rather than introducing pagination or a wrapped response body in this sprint.
- Added integration-test coverage for:
  - `customerId` filtering
  - `transactionId` filtering
  - combined-filter behavior
  - default and explicit sort behavior
- Updated `README`, OpenAPI descriptions, RAG, and roadmap/blueprint docs to reflect the current retrieval surface.

## Key Decisions Made
- Choose `customerId` and `transactionId` as the next retrieval filters because they are review-friendly, easy to explain, and do not over-widen the query surface.
- Do not add `merchantCategory` or `channel` yet; those remain useful future filters but were not needed to make the review workflow meaningfully better.
- Replace the growing retrieval branch logic with JPA specifications instead of continuing to add repository method combinations.
- Add sorting, but not pagination, as the minimum realistic list-usability improvement for this sprint.
- Keep the detailed single-item response and lightweight list projection separate, preserving the original endpoint-shape intent.

## Deviations From Original Plan
- `2.2.1` intentionally chose only two new filters instead of adopting the full candidate set from the plan.
- `2.2.3` implemented sorting only, rather than pagination or pagination-plus-sorting, because that delivered immediate review value without forcing a response-wrapper redesign.
- `2.2.2` centralized retrieval composition in a specification helper rather than introducing a custom query service or bespoke reporting abstraction.

## Lessons Learned
- Retrieval scope can grow meaningfully while still staying interview-defensible if each addition is clearly tied to fraud-review workflows.
- JPA specifications were the right middle ground here: cleaner than service-side branching, but much lighter than a custom search framework.
- Sorting alone meaningfully improves operator/reviewer usability and is often a better first step than pagination when the dataset and review flow are still small.
- Integration tests become more important once retrieval behavior grows beyond a couple of trivial filter combinations.

## Technical Debt Accrued
- List retrieval still does not support pagination; for larger result sets, that will become the next obvious usability and operational gap.
- The current sort model is intentionally minimal and not yet configurable beyond the two explicit review-oriented options.
- Retrieval still does not support broader review filters such as `merchantCategory`, `channel`, or rule-hit lookup.
- The generated Spring Security password warning and SpringDoc exposure warning remain unrelated residual cleanup items outside this sprint.

## Future Considerations
- Add pagination only when the review list is large enough to justify response-shape expansion.
- Consider adding broader review filters such as `merchantCategory` and `channel` once there is a clearer operator need.
- If retrieval keeps growing, consider a dedicated query DTO or parameter object to reduce controller/service signature growth.
- Revisit whether reviewer-facing list summaries should eventually expose additional audit-oriented fields now that Sprint `2.1` added row-level audit timestamps.

## File Inventory

| File | Status in Sprint 2.2 | Notes |
| --- | --- | --- |
| `src/main/java/com/capitec/fraudengine/api/controller/FraudEvaluationController.java` | Modified | Added new retrieval query params and sort parameter metadata |
| `src/main/java/com/capitec/fraudengine/application/service/FraudEvaluationRetrievalService.java` | Modified | Reworked retrieval orchestration to use specification-based filtering and explicit sorting |
| `src/main/java/com/capitec/fraudengine/application/service/FraudEvaluationSummarySortOrder.java` | Created | Defines supported list sort modes |
| `src/main/java/com/capitec/fraudengine/infrastructure/persistence/repository/FraudEvaluationJpaRepository.java` | Modified | Extended with `JpaSpecificationExecutor` while retaining existing repository contracts |
| `src/main/java/com/capitec/fraudengine/infrastructure/persistence/repository/FraudEvaluationSpecifications.java` | Created | Centralized retrieval filter composition for the list endpoint |
| `src/test/java/com/capitec/fraudengine/api/controller/FraudEvaluationControllerIntegrationTest.java` | Modified | Added filter and sort integration coverage for the expanded retrieval surface |
| `README.md` | Modified | Updated retrieval filters, sort behavior, and examples |
| `docs/RAG/01-project-overview.md` | Modified | Updated current-state note for richer retrieval support |
| `docs/RAG/02-decisions-log.md` | Modified | Updated retrieval-surface decision record |
| `docs/RAG/03-api-scope.md` | Modified | Updated supported query params and sort values |
| `docs/blueprints/01-project-blueprint.md` | Modified | Updated current query scope and list-ordering posture |
| `docs/blueprints/02-development-roadmap.md` | Modified | Updated roadmap wording to match implemented retrieval scope and pinned infra posture |

## RAG Update Summary
- `docs/RAG/01-project-overview.md` now reflects that retrieval supports a modest review-oriented filter set and explicit summary sorting.
- `docs/RAG/02-decisions-log.md` now records the implemented retrieval surface rather than the original narrower Phase 1-only view.
- `docs/RAG/03-api-scope.md` now reflects `customerId`, `transactionId`, and `sort` support, along with the supported sort values.
- `docs/blueprints/01-project-blueprint.md` and `docs/blueprints/02-development-roadmap.md` were updated so the broader retrieval story and pinned infrastructure posture stay aligned with the implemented system.

## Verification Summary
- `./mvnw -DskipTests compile` passed after the retrieval refactor and list-sorting changes.
- `./mvnw -DskipTests test-compile` passed after the expanded controller integration tests were added.
- Sandbox full-suite verification remains non-authoritative when Docker-backed Testcontainers access is restricted, so local Docker-backed runs remain the decisive evidence for full integration behavior.

## Close-Out
Sprint `2.2` is complete. Retrieval is now meaningfully more useful for review workflows, query handling is cleaner and more maintainable, list behavior is more deliberate, and the documentation layer matches the implemented API surface.
