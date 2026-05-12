# Sprint 1.5 Implementation Plan

## Scope
Phase 1, Sprint 5 hardens the first vertical slice into a submission-quality take-home increment. This sprint should focus on tests, API documentation, containerization, README quality, and final polish for the Phase 1 scope.

## Sprint Goal
Turn the working vertical slice into a production-grade Phase 1 submission candidate.

## Task List

### Sprint 1.5.1
Add unit tests for core rule and aggregation logic.
- high amount rule tests
- velocity rule tests
- risky merchant category rule tests
- unusual time rule tests
- aggregation policy tests

### Sprint 1.5.2
Add integration tests for persistence and API behavior.
- repository integration tests with Postgres/Testcontainers
- API tests for `POST /api/fraud-evaluations`
- API tests for `GET /api/fraud-evaluations/{evaluationId}`
- API tests for filtered list retrieval

### Sprint 1.5.3
Add OpenAPI and endpoint polish.
- verify Swagger/OpenAPI exposure
- ensure endpoint contracts match the locked Phase 1 docs
- add any missing validation or error response details

### Sprint 1.5.4
Add containerization and local run polish.
- create multi-stage `Dockerfile`
- decide whether `compose.yaml` needs refinement for local usage
- pin image versions before final submission

### Sprint 1.5.5
Create the first real project `README`.
- project purpose
- architecture summary
- how to run locally
- how to run tests
- endpoint summary
- known simplifications and future improvements
- explicit note about local Phase 1 auth posture

### Sprint 1.5.6
Do Phase 1 cleanup and final review.
- remove obvious dead code or scaffolding leftovers
- align package names, DTO names, and enum names
- review logs, comments, and error messages for interview readiness
- update blueprints and RAG docs if implementation changed any assumptions

## Expected Output
- rule and aggregation logic are tested
- persistence and API behavior are tested
- OpenAPI is usable
- `Dockerfile` exists
- `README` exists and is reviewer-friendly
- Phase 1 vertical slice is presentable as a production-grade take-home increment

## Notes
- Do not widen functional scope in this sprint unless a defect forces it.
- Prefer finishing, testing, and documenting the chosen slice over adding bonus features.
- If `location anomaly` is still deferred, keep it deferred unless all Phase 1 quality goals are already satisfied.
