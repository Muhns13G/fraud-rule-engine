# Sprint 2.5 Implementation Plan

## Scope
This sprint lays the first foundations for rule management without fully turning the system into a dynamic rule-authoring platform. The goal is to model rule identity and lifecycle cleanly enough that the current code-defined rules can evolve into something more governable later.

## Sprint Goal
Introduce the minimum viable rule-management foundation while keeping live rule evaluation deterministic and understandable.

## Task List

### Sprint 2.5.1
Define the rule identity and lifecycle model.
- model concepts such as:
  - rule code
  - version
  - status
  - activation state
- decide what remains code-backed versus persisted metadata
- chosen in this task:
  - executable rule logic remains code-defined
  - governance model is introduced in domain as:
    - `RuleIdentity` (`ruleCode`, `version`)
    - `RuleLifecycleState` (`lifecycleStatus`, `activationState`)
    - `RuleGovernanceMetadata` (identity + name + lifecycle + execution source)
  - lifecycle status set: `DRAFT`, `ACTIVE`, `DEPRECATED`, `RETIRED`
  - activation state set: `INACTIVE`, `ACTIVE`
  - execution source currently locked to `CODE_DEFINED`

### Sprint 2.5.2
Introduce persistence for rule metadata only if it meaningfully improves the model.
- keep executable rule logic code-defined for now unless a narrower data-driven step is clearly justified
- avoid a premature generic expression engine

### Sprint 2.5.3
Add admin-facing read APIs for rule visibility.
- list active rules
- inspect rule metadata/version/status
- optionally expose currently configured thresholds in a read-only way

### Sprint 2.5.4
Define activation and validation boundaries.
- decide what can and cannot be changed at runtime
- add validation so invalid or ambiguous rule metadata states are rejected
- keep live evaluation deterministic even if metadata evolves

### Sprint 2.5.5
Add tests and documentation for the rule-management foundation.
- unit and integration coverage for rule metadata behavior
- `README` and blueprint updates for the new platform direction
- RAG updates reflecting the move from pure static-rule framing to governed rule lifecycle groundwork

## Expected Output
- rule identity and lifecycle concepts are formalized
- the service exposes a small, clear rule-visibility surface
- the codebase is better positioned for future rule governance
- current evaluation behavior stays deterministic and stable

## Notes
- Do not build a full no-code rule engine in this sprint.
- Prefer read-only or tightly constrained governance capabilities over broad dynamic mutation.
- Keep the transition path explainable from “code-defined rules” to “governed rule platform.”
