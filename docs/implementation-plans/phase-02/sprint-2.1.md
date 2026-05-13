# Sprint 2.1 Implementation Plan

## Scope
Phase 2 begins by strengthening persistence and auditability without changing the external fraud-evaluation contract. This sprint should focus on richer persisted metadata, cleaner audit timestamps, and the supporting schema and mapping updates needed to carry that information safely.

## Sprint Goal
Make the persisted fraud-evaluation record more audit-friendly and future-ready while preserving the current Phase 1 API behavior.

## Task List

### Sprint 2.1.1
Add richer audit fields to the persistence model.
- introduce created and updated timestamps for the evaluation header
- decide whether request receipt time and evaluation completion time should remain distinct
- keep existing Phase 1 response payloads stable unless a defect requires a change

### Sprint 2.1.2
Add a new Flyway migration for audit-focused schema refinement.
- extend `fraud_evaluations` with the chosen audit columns
- add or refine indexes that support retrieval and audit lookup
- keep the migration additive and backward-safe

### Sprint 2.1.3
Update JPA entities, mappers, and domain translation for the new audit fields.
- extend persistence entities and mapper logic
- decide whether the domain aggregate should surface all new audit values or keep some persistence-only
- avoid leaking storage concerns into the core rule model unnecessarily

### Sprint 2.1.4
Add repository and integration-test coverage for the new audit persistence behavior.
- verify new audit fields are persisted
- verify defaulting/auto-population behavior where applicable
- keep tests aligned to the existing Docker/Testcontainers path

### Sprint 2.1.5
Review docs for auditability alignment.
- update `README` if new persisted behavior affects reviewer understanding
- update RAG or blueprints only if the implemented persistence model meaningfully changes the documented current state

## Expected Output
- persisted fraud evaluations carry richer audit metadata
- schema and mapping layers are updated without breaking Phase 1 endpoints
- integration tests prove the new persistence behavior
- documentation reflects the new auditability posture where needed

## Notes
- Do not introduce new public fraud-evaluation endpoints in this sprint.
- Prefer additive schema evolution over destructive redesign.
- Keep the outward API stable unless a concrete defect forces a contract change.
