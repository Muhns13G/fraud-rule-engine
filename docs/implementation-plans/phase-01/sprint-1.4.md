# Sprint 1.4 Implementation Plan

## Scope
Phase 1, Sprint 4 wires the core evaluation flow together. This sprint should make the fraud engine actually evaluate transactions, persist results, and expose the first working endpoints.

## Sprint Goal
Deliver the first working end-to-end fraud evaluation path from request DTO to persisted result and retrieval API.

## Task List

### Sprint 1.4.1
Implement rule logic.
- implement high amount rule
- implement velocity rule using recent evaluation or transaction history lookups
- implement risky merchant category rule
- implement unusual time rule
- ensure each rule returns:
  - rule code
  - rule name
  - triggered state
  - severity
  - score contribution
  - reason

### Sprint 1.4.2
Implement the aggregation policy.
- combine rule results into final `ALLOW` / `REVIEW` / `BLOCK`
- enforce blocking behavior for block-level hits
- calculate and expose `decisionScore`
- generate `traceSummary`

### Sprint 1.4.3
Implement the application service layer.
- evaluation orchestration service
- retrieval service
- mapping from request DTO to domain model
- mapping from domain result to response DTO

### Sprint 1.4.4
Implement persistence write and read paths.
- persist evaluation header
- persist per-rule results
- support retrieval by:
  - evaluation id
  - `decision`
  - `accountId`
  - time range

### Sprint 1.4.5
Implement the controller layer.
- `POST /api/fraud-evaluations`
- `GET /api/fraud-evaluations/{evaluationId}`
- `GET /api/fraud-evaluations`
- validation handling
- exception handling and consistent error responses

### Sprint 1.4.6
Replace default Spring Security behavior with intentional Phase 1 local/test behavior.
- configure permissive local access
- ensure default generated-password flow is not the developer experience
- keep the security story explicitly temporary and documented

## Expected Output
- first working fraud evaluation flow exists
- first retrieval endpoints exist
- persisted evaluation data can be queried back
- local development behavior is intentional instead of default Spring Security behavior

## Notes
- Keep the list endpoint minimal and review-focused.
- If velocity lookup is awkward, prefer a straightforward query-based implementation over premature optimization.
- Preserve explainability in the API responses.
