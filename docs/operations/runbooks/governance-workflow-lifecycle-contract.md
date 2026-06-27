# Governance Workflow Lifecycle Contract

## Purpose
Define the operational governance lifecycle contract for rule metadata promotion/deprecation workflows before introducing explicit workflow action APIs.

This contract is aligned to the currently enforced domain policy in:
- `RuleGovernancePolicy`
- existing lifecycle/activation constraints
- current governance mutation/read endpoints

## Scope Boundary
- Applies to governed **metadata** (`ruleCode + version`), not executable rule code changes.
- Runtime execution source remains `CODE_DEFINED`.

## Lifecycle States
- `DRAFT`
- `ACTIVE`
- `DEPRECATED`
- `RETIRED`

## Activation States
- `ACTIVE`
- `INACTIVE`

## Invariants (Must Always Hold)
1. `ACTIVE` lifecycle requires activation `ACTIVE`.
2. `DRAFT` and `RETIRED` lifecycles require activation `INACTIVE`.
3. `DEPRECATED` may be `ACTIVE` or `INACTIVE`.
4. Execution source must remain `CODE_DEFINED`.

## Allowed Lifecycle Transitions

| From | To | Allowed |
| --- | --- | --- |
| `DRAFT` | `ACTIVE` | Yes |
| `DRAFT` | `RETIRED` | Yes |
| `ACTIVE` | `DEPRECATED` | Yes |
| `ACTIVE` | `RETIRED` | Yes |
| `DEPRECATED` | `ACTIVE` | Yes |
| `DEPRECATED` | `RETIRED` | Yes |
| `RETIRED` | any | No |

All non-listed transitions are disallowed.

## Workflow Action Semantics (Contract Layer)
These are semantic workflow actions mapped to valid lifecycle transitions:

1. `PROMOTE`
- Intended transition: `DRAFT -> ACTIVE`
- Activation expectation: resulting state is `ACTIVE`

2. `DEPRECATE`
- Intended transition: `ACTIVE -> DEPRECATED`
- Activation expectation: resulting state may be `ACTIVE` or `INACTIVE`

3. `REACTIVATE`
- Intended transition: `DEPRECATED -> ACTIVE`
- Activation expectation: resulting state is `ACTIVE`

4. `RETIRE`
- Intended transition:
  - `DRAFT -> RETIRED`, or
  - `ACTIVE -> RETIRED`, or
  - `DEPRECATED -> RETIRED`
- Activation expectation: resulting state is `INACTIVE`

## Current API Alignment
- Current governance mutation surface is state-based:
  - `PATCH /api/admin/rules/{ruleCode}/versions/{version}/state`
- Current version registration surface:
  - `POST /api/admin/rules/{ruleCode}/versions`
- This contract is the action-level semantics baseline for explicit workflow operations.

## Operational Guardrails
- Lifecycle/activation combinations outside invariants must fail fast.
- Disallowed transitions must be rejected deterministically.
- `RETIRED` is terminal in the current contract.

## Follow-On Work
- implement explicit workflow action operations
- persist durable workflow and lifecycle action history
- expose history and version read surfaces with pagination and least privilege
