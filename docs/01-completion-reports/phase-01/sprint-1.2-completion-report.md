# Sprint 1.2 Completion Report

## Sprint Summary
Sprint `1.2` completed the final planning pass required before development. The sprint focused on eliminating the remaining implementation blockers by locking the Phase 1 contract surface, starter heuristics, score model, and Flyway timing.

## Scope Completed
- Moved Flyway into the active Phase 1 slice.
- Locked the Phase 1 request DTO shape.
- Locked the Phase 1 response DTO shape.
- Locked starter rule thresholds for:
  - high amount
  - velocity
  - risky merchant category
  - unusual time
- Locked the initial scoring and decision aggregation approach.
- Added a dedicated `Sprint 1.2` implementation plan.

## Key Decisions Made
- Flyway is part of Phase 1, not Phase 2.
- The first request contract includes:
  - required identifiers
  - amount and currency
  - merchant context
  - transaction context
  - timestamp
  - optional structured location
  - optional reference
- The first response contract includes:
  - `evaluationId`
  - `transactionId`
  - `decision`
  - `decisionScore`
  - `evaluatedAt`
  - `traceSummary`
  - `ruleResults`
- Phase 1 uses both:
  - outward business decisions: `ALLOW`, `REVIEW`, `BLOCK`
  - a simple internal numeric score for aggregation and traceability
- Starter thresholds are:
  - amount review at `>= 10000.00 ZAR`
  - amount block at `>= 25000.00 ZAR`
  - velocity at `>= 3` transactions within `5 minutes` for the same `accountId`
  - risky merchant categories starting with `GAMBLING`, `CRYPTO`, `MONEY_TRANSFER`
  - unusual time window from `00:00` to `04:00`

## Deviations From Original Plan
- The roadmap originally placed Flyway in Phase 2. That was corrected because persisted evaluations in Phase 1 require an explicit schema baseline from the first vertical slice.
- The planning set originally left thresholds, DTO details, and score strategy open. Sprint `1.2` intentionally closed those gaps rather than carrying them into implementation.

## Lessons Learned
- “Mostly settled” is not settled enough for smooth implementation. Locking concrete DTOs and thresholds before coding materially reduces churn.
- The roadmap, blueprint, RAG docs, and sprint plans need to evolve together. If one stays behind, ambiguity creeps back in.
- A small, explicit heuristic set is more useful than a vague “we’ll decide in code” approach for portfolio work.

## Technical Debt Accrued
- No code-level technical debt was introduced in Sprint `1.2` because the work remained planning-focused.
- Intentional design debt still carried into implementation:
  - exact enum naming for transaction type, channel, and merchant category
  - final list-endpoint projection shape
  - whether score constants need tuning after test examples and sample data exist

## Future Considerations
- Revisit the starter thresholds once seeded examples and tests exist; they are intentionally pragmatic defaults, not institution-calibrated fraud controls.
- If the numeric scoring model becomes awkward during implementation, preserve the outward decision model and simplify the internals rather than expanding complexity.
- If Phase 1 delivery is ahead of schedule, `location anomaly` remains the best candidate for a carefully documented follow-on enhancement.

## File Inventory

| File | Status in Sprint 1.2 | Notes |
| --- | --- | --- |
| `docs/blueprints/01-project-blueprint.md` | Modified | Locked request and response shape, starter thresholds, and score model |
| `docs/blueprints/02-development-roadmap.md` | Modified | Moved Flyway into Phase 1 and reduced remaining open decisions |
| `docs/RAG/02-decisions-log.md` | Modified | Recorded the settled thresholds and scoring approach |
| `docs/RAG/03-api-scope.md` | Modified | Added the expected Phase 1 request and response fields |
| `docs/implementation-plans/sprint-1.2.md` | Created | Formalized the Sprint `1.2` contract-locking work |

## RAG Update Summary
- Updated `02-decisions-log.md` to reflect settled thresholds and scoring.
- Updated `03-api-scope.md` to reflect the locked request and response surface.

## Close-Out
Sprint `1.2` is complete. It removed the last planning blockers ahead of development and left the project in a code-ready state for the first implementation sprint.
