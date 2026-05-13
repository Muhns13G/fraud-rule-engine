# Sprint 2.2 Implementation Plan

## Scope
This sprint expands retrieval and review usability beyond the Phase 1 minimum while still staying within the current fraud-evaluation service shape. The work should focus on query capability, list usability, and query-layer cleanliness rather than new fraud rules.

## Sprint Goal
Make persisted fraud evaluations easier to query and review without turning retrieval into an overly broad search platform.

## Task List

### Sprint 2.2.1
Decide and implement the next approved retrieval filters.
- likely candidates:
  - `customerId`
  - `transactionId`
  - `merchantCategory`
  - `channel`
- keep the chosen set intentionally small and interview-defensible

### Sprint 2.2.2
Refactor retrieval query handling away from repository-method explosion if needed.
- assess whether current method combinations remain maintainable
- introduce a cleaner query strategy if the chosen filters make the current approach too brittle
- preserve readable behavior over premature abstraction

### Sprint 2.2.3
Improve list response usability.
- decide whether the list endpoint needs pagination, sorting, or both
- add the minimum shape needed for realistic review usage
- keep the single-item detailed response distinct from the lighter list projection

### Sprint 2.2.4
Add integration tests for the expanded retrieval behavior.
- verify each newly supported filter
- verify combined-filter behavior where relevant
- verify any pagination or sorting rules if introduced

### Sprint 2.2.5
Update documentation and OpenAPI for the retrieval surface.
- document new query parameters
- keep examples aligned to the implemented behavior
- refresh RAG/blueprints only if the locked Phase 1 retrieval assumptions are intentionally superseded

## Expected Output
- retrieval supports a slightly richer review workflow
- query handling remains maintainable and testable
- list endpoint behavior is clearer for reviewer and operator use
- OpenAPI and docs match the implemented retrieval behavior

## Notes
- Do not turn the service into a generic ad hoc reporting engine.
- Add only the retrieval surface that clearly improves fraud-review usability.
- Keep response-shape changes deliberate and minimal.
