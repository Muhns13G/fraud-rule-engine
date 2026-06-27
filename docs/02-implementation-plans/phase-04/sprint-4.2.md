# Sprint 4.2 Implementation Plan

## Scope
Mature secure identity and secret posture from local-friendly defaults to operations-ready patterns.

## Sprint Goal
Make secure-profile identity and credential management safer, more maintainable, and easier to operate in non-local environments.

## Task List

### Sprint 4.2.1
Introduce explicit secure-profile secret-source strategies.
- define supported secret source patterns for:
  - local env vars
  - external secret managers (integration seam)
  - pre-encoded credential fallback
- validate startup behavior for misconfigured secret combinations
- debt merged:
  - `TD-005`

### Sprint 4.2.2
Harden JDBC-backed identity mode.
- document/validate schema/query expectations for JDBC identity provider
- enforce safer defaults and clearer validation errors for missing/invalid queries
- keep in-memory mode as local fallback only
- debt merged:
  - `TD-004`

### Sprint 4.2.3
Add credential-rotation readiness hooks.
- define operational rotation workflow in docs and config contract
- avoid hard-coding credential assumptions in test fixtures

### Sprint 4.2.4
Add secure-config contract tests.
- test invalid secure identity configuration paths fail predictably
- test boot behavior for each supported identity-provider strategy

### Sprint 4.2.5
Close-out documentation for identity and secret posture.
- update README + RAG + technical debt statuses for secret/identity outcomes

## Public/API Changes
- no endpoint contract changes
- secure-profile runtime configuration contract becomes stricter and better validated

## Tests
- configuration contract tests for in-memory and JDBC identity modes
- failure-mode tests for missing or conflicting secure-profile properties

## Expected Output
- better non-local credential posture
- clearer operations guidance and safer startup failure behavior

## Notes
- keep implementation vendor-neutral; avoid locking to one secret manager in this sprint
