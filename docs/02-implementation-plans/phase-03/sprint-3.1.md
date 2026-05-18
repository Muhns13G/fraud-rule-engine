# Sprint 3.1 Implementation Plan

## Scope
This sprint opens the next stage of rule governance by introducing constrained mutation workflows while hardening retrieval behavior for correctness and reviewer usability.

## Sprint Goal
Enable auditable governance mutations and close high-impact retrieval debt without changing the current executable-rule boundary.

## Task List

### Sprint 3.1.1
Add governed mutation commands for rule metadata lifecycle transitions.
- add admin mutation endpoints for lifecycle/activation transitions only
- enforce transition policy and existing governance invariants
- keep executable rule logic non-mutable
- debt merged:
  - `TD-012`
  - `TD-014`

### Sprint 3.1.2
Add governed version registration workflow.
- add a controlled endpoint/use case to register a new metadata version for an existing rule code
- preserve `CODE_DEFINED` execution source boundary
- debt merged:
  - `TD-013`

### Sprint 3.1.3
Externalize thresholds/windows as validated properties and surface active values in governance read model.
- move current code constants into validated configuration properties
- keep default values equal to current runtime behavior
- debt merged:
  - `TD-015`

### Sprint 3.1.4
Add pagination to fraud-evaluation list endpoint while preserving current filters/sort.
- introduce pageable request parameters
- evolve list response contract with pagination metadata
- debt merged:
  - `TD-001`

### Sprint 3.1.5
Fix single-bound time filter behavior and optionally add deferred retrieval filters.
- implement one-sided time predicates (`from` only / `to` only)
- optionally add low-risk filters (`merchantCategory`, `channel`)
- debt merged:
  - `TD-002`
  - `TD-018`

## Public/API Changes
- new admin mutation endpoints under `/api/admin/rules/...` for lifecycle transitions and version registration
- `GET /api/fraud-evaluations` evolves to a paged response shape
- optional new list filters:
  - `merchantCategory`
  - `channel`

## Tests
- governance mutation integration tests for valid/invalid transitions
- pagination/filter/sort integration coverage including one-sided time filters
- backward-compat tests proving unchanged evaluate behavior

## Expected Output
- governed metadata mutation workflows are introduced with explicit constraints
- retrieval behavior is more correct and scalable for review workflows
- threshold/window values are centrally configurable with safe defaults

## Notes
- keep mutation scope constrained to governance metadata, not runtime executable rule logic
- retain explainable, deterministic evaluation behavior while governance capabilities expand
