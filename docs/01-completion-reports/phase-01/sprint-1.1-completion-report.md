# Sprint 1.1 Completion Report

## Sprint Summary
Sprint `1.1` established the canonical planning baseline for the Capitec fraud rule engine take-home. The sprint focused on locking the Phase 1 direction, creating retrieval-friendly RAG context, documenting the phased roadmap, and explicitly deferring non-critical scope so implementation can begin without drift.

## Scope Completed
- Locked the Phase 1 endpoint direction:
  - `POST /api/fraud-evaluations`
  - `GET /api/fraud-evaluations/{evaluationId}`
  - `GET /api/fraud-evaluations`
- Locked the Phase 1 outward decision model:
  - `ALLOW`
  - `REVIEW`
  - `BLOCK`
- Locked the initial Phase 1 rule set:
  - high amount
  - velocity
  - risky merchant category
  - unusual time
- Created the initial blueprint set.
- Created the initial numbered RAG starter set.
- Created the initial sprint implementation plan set.
- Explicitly deferred `location anomaly`, advanced retrieval filters, production auth implementation, and rule-management endpoints.

## Key Decisions Made
- The take-home is implemented as a categorized transaction fraud evaluation service, not a generic event platform.
- Maven remains the build tool.
- PostgreSQL remains the primary persistence store for local development and tests.
- Spring Boot `4.0.6` and Java `25` remain the runtime baseline.
- The project optimizes for one strong production-grade vertical slice rather than broad platform scope.
- RAG docs are part of the planning baseline and should be kept current as decisions or implementation meaningfully change.

## Deviations From Original Plan
- `Sprint 1.1.3` originally called for only a high-level implementation sequence. During planning, the work naturally expanded into a more detailed follow-on planning sprint, `Sprint 1.2`, to lock DTO shape, thresholds, score model, and Flyway timing before coding.
- The original blueprint direction briefly mentioned `location anomaly` as part of the early rule set. This was intentionally reduced to a deferred item for Phase 1 to preserve explainability and delivery focus.

## Lessons Learned
- The repo needed a stronger distinction between canonical planning docs and imported conversational notes. This is now in place through the numbered blueprints and RAG set.
- Locking naming early matters. Keeping API naming consistent around `fraud-evaluations` avoids churn later.
- Small retrieval-friendly docs are valuable even before implementation starts, as long as they stay close to the canonical blueprint decisions.
- Deferring attractive but fuzzy ideas such as `location anomaly` improves take-home credibility because it keeps the first slice explainable.

## Technical Debt Accrued
- No code-level technical debt was introduced in Sprint `1.1` because the work was planning-only.
- Planning debt that remained at the end of Sprint `1.1`:
  - exact request and response DTO shape
  - exact starter thresholds
  - internal scoring model
  - Flyway phase timing

## Future Considerations
- Add a completion report at the end of every sprint.
- Update RAG docs whenever a sprint changes:
  - locked decisions
  - API scope
  - architecture shape
  - domain terminology
- Once implementation starts, future completion reports should also capture:
  - verification run details
  - tests added or skipped
  - schema changes
  - API examples changed
- If `location anomaly` is later introduced, document the heuristic carefully and explain why it is acceptable for the take-home.

## File Inventory

| File | Status in Sprint 1.1 | Notes |
| --- | --- | --- |
| `docs/blueprints/README.md` | Created, then updated | Established blueprint usage and later linked the numbered RAG set |
| `docs/blueprints/01-project-blueprint.md` | Created, then updated | Became the canonical architecture and scope blueprint |
| `docs/blueprints/02-development-roadmap.md` | Created, then updated | Became the phased roadmap with locked decisions |
| `docs/RAG/01-project-overview.md` | Created | Primary read-first RAG context |
| `docs/RAG/02-decisions-log.md` | Created | Locked decisions log |
| `docs/RAG/03-api-scope.md` | Created | Phase 1 API scope summary |
| `docs/RAG/04-domain-glossary.md` | Created | Domain vocabulary for future sessions |
| `docs/RAG/05-architecture-current-vs-target.md` | Created | Architecture summary for current vs target |
| `docs/implementation-plans/sprint-1.1.md` | Created | Sprint `1.1` implementation plan |

## RAG Update Summary
- Added the initial numbered RAG starter set.
- Established the intended read order:
  - `01-project-overview.md`
  - `02-decisions-log.md`
  - `03-api-scope.md`
  - `04-domain-glossary.md`
  - `05-architecture-current-vs-target.md`

## Close-Out
Sprint `1.1` is complete. Its main deliverable was a clean planning baseline rather than executable code, and it left the project ready for the next planning or implementation step with a much smaller ambiguity surface.
