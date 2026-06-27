# Sprint 5.4 Implementation Plan

## Scope
Close remaining investigation/retrieval gap and strengthen production-hardening quality gates.

## Sprint Goal
Finish Phase 5 with high-signal operator usability and stronger non-functional validation.

## Debt Merged
- `TD-018`
- `TD-023`
- `TD-024`
- `TD-026`

## Task List

### Sprint 5.4.1
Add rule-hit retrieval filter contract.
- define filter semantics for querying evaluations by triggered rule(s)
- keep backward compatibility with existing retrieval filters

### Sprint 5.4.2
Implement rule-hit retrieval querying with performance guardrails.
- add query path using specifications/join strategy with bounded behavior
- validate indexes and query plan assumptions

### Sprint 5.4.3
Expand CI hardening gates.
- extend CI beyond baseline checks with focused production-hardening validations
- include targeted security/ops regression execution path
- add repository hygiene checks (including `.DS_Store` exclusion and workspace cleanliness assertions)

### Sprint 5.4.4
Add performance and reliability smoke checks.
- add repeatable load-smoke/latency sanity checks for evaluation and retrieval paths
- capture pass/fail thresholds for regression use
- include velocity-rule temporal-correctness regression checks to ensure future-dated events are not counted toward velocity windows

### Sprint 5.4.5
Phase close-out and debt reconciliation.
- update debt registry statuses with evidence from Phase 5 outcomes
- publish Sprint 5.4 and Phase 5 completion artifacts
- include test-suite maintainability cleanup (shared secure-profile test credential fixtures)

## Public/API Changes
- `GET /api/fraud-evaluations` gains rule-hit-based filter support.
- No breaking change to existing endpoint contracts expected.

## Tests
- Retrieval integration tests for rule-hit filter combinations.
- Query-performance guard tests.
- CI gate validation for expanded workflow.

## Expected Output
- Investigation workflows gain missing rule-hit lookup capability.
- Phase 5 closes with stronger operational and quality confidence.
