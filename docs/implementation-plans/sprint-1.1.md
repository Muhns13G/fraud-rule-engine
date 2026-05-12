# Sprint 1.1 Implementation Plan

## Scope
Phase 1, Sprint 1 focuses on establishing the first production-grade vertical slice plan for the Capitec fraud rule engine take-home. The target outcome is a code-ready set of tasks that build toward ingesting a categorized transaction event, evaluating fraud rules, persisting the result, and retrieving stored evaluations.

## Sprint Goal
Prepare and execute the first implementation slice without architectural drift.

## Task List

### Sprint 1.1.1
Lock the Phase 1 scope in planning artifacts.
- Confirm the first endpoints:
  - `POST /api/fraud-evaluations`
  - `GET /api/fraud-evaluations/{evaluationId}`
  - `GET /api/fraud-evaluations`
- Confirm the first decision model:
  - `ALLOW`
  - `REVIEW`
  - `BLOCK`
- Confirm the first rules:
  - high amount
  - velocity
  - risky merchant category
  - unusual time

### Sprint 1.1.2
Create the RAG starter set for future coding sessions.
- Add `01-project-overview.md`
- Add `02-decisions-log.md`
- Add `03-api-scope.md`
- Add `04-domain-glossary.md`
- Add `05-architecture-current-vs-target.md`

### Sprint 1.1.3
Define the initial implementation sequence.
- establish package structure
- define request and response DTOs
- define core domain model
- define rule contract and rule results
- add first migration
- add persistence model
- add service orchestration
- add controller endpoints
- add unit and integration tests

### Sprint 1.1.4
Defer non-critical scope explicitly.
- defer `location anomaly`
- defer advanced retrieval filters
- defer production auth implementation
- defer rule-management endpoints

## Expected Output
- canonical blueprints reflect the settled decisions
- `/docs/RAG` starter set exists
- Sprint planning is explicit and reusable for future phase or task planning

## Notes
- Use this sprint plan as the anchor for subsequent sprint/task plans.
- Future implementation plans can keep all tasks for a sprint in one sprint document, using the `Sprint X.Y.Z` convention.
- Sprint close-out should produce a completion report and update RAG docs where necessary.
