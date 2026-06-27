# Sprint 3.3 Implementation Plan

## Scope
This sprint deepens observability around governance and retrieval flows, then extends fraud capability only where traceability remains deterministic.

## Sprint Goal
Strengthen operational diagnostics and add explainable rule behavior extensions with matching test coverage.

## Task List

### Sprint 3.3.1
Add observability contract tests.
- test metrics emission and key request-correlation behavior end-to-end
- debt merged:
  - `TD-010`

### Sprint 3.3.2
Expand metric coverage beyond evaluation-only focus.
- add retrieval/governance/error metrics with consistent names and tags
- debt merged:
  - `TD-011`

### Sprint 3.3.3
Add `location anomaly` rule with deterministic explainability.
- implement a minimal heuristic with explicit evidence in `ruleResults`
- keep behavior configurable and testable
- debt merged:
  - `TD-017`

### Sprint 3.3.4
Add governance observability for lifecycle/version changes.
- emit structured audit log events and lifecycle/version mutation metrics
- correlate mutation actions with request IDs and secure-profile actor identity

## Public/API Changes
- optional response enrichment for location-anomaly evidence fields within existing `ruleResults` shape
- no required client-breaking contract changes

## Tests
- integration tests for observability contract behavior (metrics + correlation)
- unit and integration tests for location anomaly rule behavior
- governance observability assertions for lifecycle/version mutation flows

## Expected Output
- observability depth improves across evaluate, retrieve, and governance paths
- new fraud capability remains explainable and deterministic
- governance mutations gain stronger diagnostics and auditability

## Notes
- do not compromise deterministic rule explainability while adding new signal coverage
- prefer operator-value metrics over broad metric volume
