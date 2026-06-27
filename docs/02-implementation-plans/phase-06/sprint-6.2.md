# Sprint 6.2 Implementation Plan

## Scope
Validation usability hardening and validation ergonomics after Phase 6.1 trust-boundary enforcement.

## Sprint Goal
Reduce validation friction during hosted and local validation by improving request parsing clarity, input compatibility normalization, and turnkey Postman-based verification flows.

## Debt Policy
- Keep `TD-021` and `TD-027` as `Partially addressed` (no live external IdP rollout in this sprint).
- Do not widen domain enums unless required by rule behavior; prefer compatibility aliases at API normalization boundaries.

## Task List

### Sprint 6.2.1
Request-body parsing feedback hardening.
- handle request-body parse failures via API exception mapping
- ensure invalid timestamp payloads return `400` with actionable guidance
- preserve standardized API error payload structure

### Sprint 6.2.2
Compatibility alias normalization for validation-friendly payloads.
- normalize common inbound aliases at application-mapper boundary:
  - `POS` -> `CARD_PRESENT`
  - `ECOM` -> `ONLINE`
  - `CARD_PAYMENT` -> `PAYMENT`
  - `CASH_WITHDRAWAL` -> `WITHDRAWAL`
  - `MONEYTRANSFER` -> `MONEY_TRANSFER`
- keep canonical persisted/domain enum values unchanged

### Sprint 6.2.3
Regression coverage for parser and alias contract behavior.
- add integration tests for alias acceptance
- add integration tests for invalid/no-offset timestamp handling (`400` expectation)
- keep existing secure/production observability profile tests aligned with hardened-property contract

### Sprint 6.2.4
Validation operations docs and Postman verification pack.
- add Postman collection with positive + negative evaluation scenarios
- add dedicated local and hosted validation Postman environments
- update README with:
  - validation quick start
  - Postman quick start
  - enum value/alias reference
  - timestamp and query-encoding guardrails

## Public/API Changes
- No new business endpoints.
- Request normalization accepts additional compatibility aliases.
- Parse failure responses become clearer and consistently `400` for malformed request payloads.

## Tests
- targeted integration tests for alias and timestamp parsing behavior
- compile sanity validation
- hosted/local validation flow validation via Postman and scripted curl paths

## Expected Output
- Lower validation setup friction for hosted and local execution paths.
- Clearer error behavior for malformed timestamps.
- Stable canonical domain model with compatibility handled at API mapping boundary.
