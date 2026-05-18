# Sprint 2.1 Completion Report

## Sprint Summary
Sprint `2.1` strengthened persistence auditability for the existing fraud-evaluation slice without widening the public API. The sprint added additive schema support for row-level audit timestamps, introduced supporting retrieval-oriented indexes, kept business completion time distinct from persistence lifecycle time, and extended repository integration coverage around the new persistence behavior.

## Scope Completed
- Added richer audit metadata to persisted fraud-evaluation headers:
  - `created_at`
  - `updated_at`
- Preserved the distinction between:
  - `evaluated_at` as the business completion timestamp
  - `created_at` and `updated_at` as persistence lifecycle timestamps
- Added a new Flyway migration that:
  - extends `fraud_evaluations` with audit columns
  - adds audit and retrieval-focused indexes
- Updated the persistence entity model to auto-populate audit timestamps through JPA lifecycle hooks
- Kept the outward Phase 1 API contract unchanged
- Extended repository integration coverage to verify:
  - audit timestamp population on insert
  - `updated_at` refresh behavior on modification
- Updated `README`, RAG, and blueprint docs where the implemented persistence posture changed reviewer understanding

## Key Decisions Made
- Keep `evaluated_at` as the business-facing completion timestamp already used by the existing API and domain model.
- Treat `created_at` and `updated_at` as persistence-layer audit fields rather than forcing them into the current public response contracts.
- Prefer additive schema evolution over a persistence redesign so Phase 1 endpoints and retrieval behavior remain stable.
- Add indexes that support likely audit and retrieval paths now, rather than waiting for Phase 2 query expansion to make the table harder to evolve safely.
- Keep Compose and Testcontainers aligned on explicit PostgreSQL `18.3` pinning, with the Postgres `18+` volume layout handled in local Compose rather than downgrading the stack.

## Deviations From Original Plan
- No domain aggregate expansion was introduced for the new audit fields because the implemented need was satisfied by persistence-only timestamps in this sprint.
- The sprint also finalized the PostgreSQL `18.3` local-run correction by using the `18+`-compatible Compose volume mount path; this was adjacent infrastructure alignment rather than new feature scope.
- The planned mapper/domain translation work ended up being minimal because no public or domain-surface exposure of the new audit fields was required.

## Lessons Learned
- Auditability can be improved meaningfully without widening the public contract when the boundary between business timestamps and persistence timestamps is kept explicit.
- Postgres `18+` container behavior is not a generic instability issue, but it does require the correct Compose mount layout for a smooth local reviewer path.
- Repository-level tests remain the best place to prove timestamp defaulting and update semantics before future retrieval features build on that state.

## Technical Debt Accrued
- The generated Spring Security password warning still appears during local startup and remains unrelated residual cleanup work.
- SpringDoc exposure warnings still appear because documentation endpoints remain enabled by default for reviewer usability.
- Mockito/Testcontainers verification remains Docker-dependent, which means sandbox test runs are not authoritative when Docker socket access is restricted.
- The new audit fields are persistence-visible but not yet exposed through richer audit-specific retrieval contracts; that remains future work rather than a gap in this sprint.

## Future Considerations
- Use the new audit timestamps in future retrieval expansions such as operational review queries and timeline-oriented investigation endpoints.
- Consider whether `created_at` and `updated_at` should eventually be surfaced in a reviewer/admin-facing projection once Phase 2 retrieval scope broadens.
- Revisit startup-log cleanup for security and SpringDoc warnings once reviewer convenience is no longer the dominant concern.
- Continue Phase 2 by building richer retrieval behavior on top of the now more audit-friendly persistence model.

## File Inventory

| File | Status in Sprint 2.1 | Notes |
| --- | --- | --- |
| `src/main/resources/db/migration/V2__add_fraud_evaluation_audit_columns.sql` | Created | Adds audit columns and retrieval/audit indexes to `fraud_evaluations` |
| `src/main/java/com/capitec/fraudengine/infrastructure/persistence/entity/FraudEvaluationEntity.java` | Modified | Adds `createdAt`/`updatedAt` and JPA lifecycle auto-population |
| `src/test/java/com/capitec/fraudengine/infrastructure/persistence/repository/FraudEvaluationJpaRepositoryIntegrationTest.java` | Modified | Adds audit-persistence verification coverage |
| `src/test/java/com/capitec/fraudengine/TestcontainersConfiguration.java` | Modified | Keeps Testcontainers explicitly pinned to PostgreSQL `18.3` |
| `README.md` | Modified | Documents the richer audit posture and PostgreSQL `18+` local-run nuance |
| `docs/RAG/01-project-overview.md` | Modified | Updates current persistence baseline to include row-level audit timestamps |
| `docs/RAG/05-architecture-current-vs-target.md` | Modified | Aligns current-state architecture wording with the richer persistence model |
| `docs/blueprints/01-project-blueprint.md` | Modified | Updates verified current state and persistence model wording |

## RAG Update Summary
- `docs/RAG/01-project-overview.md` now reflects that persisted evaluation rows carry both business completion time and row-level audit timestamps.
- `docs/RAG/05-architecture-current-vs-target.md` now describes audit timestamps as part of the current persistence state rather than future intent.
- `docs/blueprints/01-project-blueprint.md` now aligns the verified current state and persistence model wording with the implemented auditability improvements.

## Verification Summary
- Sprint-scope code inspection confirms that Sprint `2.1.1` through `2.1.5` were implemented without widening the public API contract.
- Local runtime verification during the sprint confirmed that the application can connect successfully to PostgreSQL `18.3`, validate Flyway migrations through schema version `2`, and use the corrected Postgres `18+` Compose layout.
- A fresh sandbox `./mvnw test` run on May 13, 2026 was not authoritative because Docker socket access was blocked in the sandbox environment; the resulting Testcontainers failures were environmental, not application regressions.
- Prior local verification for the working tree confirmed the repository test suite at `33` passing tests after the Sprint `2.1` changes.

## Close-Out
Sprint `2.1` is complete. The persistence layer is now more audit-friendly and future-ready, the external fraud-evaluation contract remains stable, and the repo is positioned cleanly for Sprint `2.2` retrieval expansion.
