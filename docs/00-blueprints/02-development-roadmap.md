# Development Roadmap

## Planning Principle
Move from a working take-home vertical slice to a configurable rules platform. Do not introduce dynamic rule authoring before the service can reliably accept categorized transaction events, evaluate a small static rule set, persist decisions, and expose retrieval APIs.

## Phase 1: Take-Home Vertical Slice
- Add a real package structure under `api`, `application`, `domain`, and `infrastructure`.
- Define the first API contract for one categorized transaction event.
- Implement a code-defined rule engine with 3-4 explicit fraud rules.
- Support `ALLOW`, `REVIEW`, and `BLOCK` decisions with traceable reasons.
- Add Flyway and create the first PostgreSQL schema.
- Persist the evaluation header and per-rule results in PostgreSQL.
- Add `POST /api/fraud-evaluations`, `GET /api/fraud-evaluations/{id}`, and a filtered list endpoint.
- Replace default security behavior with an intentional local-development configuration.
- Add a runnable `Dockerfile` and a real `README`.
- Add focused unit tests for rule evaluation outside the Spring context.

## Phase 2: Persistence And Auditability
- Persist evaluation requests, decisions, and rule hit details with query-friendly indexing.
- Introduce JPA entities and repository adapters without leaking persistence types into the domain layer.
- Add integration tests that prove persistence and decision history behavior.
- Add richer audit fields and retention-minded timestamps.

## Phase 3: Rule Management
- Model rule identity, status, versioning, and activation lifecycle.
- Add admin-facing endpoints for listing and activating rule versions.
- Decide whether rule logic remains code-backed or becomes data-driven with a constrained expression model.
- Add validation to prevent invalid or ambiguous rule configurations.
- Status update:
  - Phase 3 is completed through Sprint `3.4`.
  - Current scope now includes governed rule mutation (state transition + version registration), profile-aware authorization boundaries, observability contracts, and close-out regression/CI gates.

## Phase 4: Security And Operations
- Replace generated-password defaults with explicit authentication and authorization strategy suitable for a banking-flavored API.
- Extend the lightweight observability baseline with stronger operational policies and richer production-facing diagnostics.
- Build on the existing actuator exposure policy, structured logging, domain metrics, and request correlation conventions.
- Further define error handling, correlation boundaries, and audit event boundaries for production readiness.
- Add environment-specific configuration patterns for local, test, and production.
- Status update:
  - Sprint `4.1` establishes explicit secure-profile role segmentation for API, governance read/mutation, actuator, and docs surfaces.
  - `default` profile remains intentionally open with explicit guardrail messaging for local/reviewer mode.
  - enterprise IAM/JWT/OAuth2 remains intentionally deferred to avoid premature complexity in current scope.

## Phase 5: Production Hardening
- Add CI for compile, test, and packaging.
- Add test layers:
  - unit tests for rule logic
  - repository integration tests
  - API tests for request/response behavior
- Keep infrastructure image versions explicitly pinned and reviewer-reproducible.
- Revisit performance characteristics for high-throughput decision evaluation.
- Status update:
  - The baseline CI gate for compile/test/package was delivered in Sprint `3.4.1`.
  - Remaining Phase 5 hardening still includes broader pipeline maturity and performance-focused quality gates.

## First Concrete Deliverables
- `POST /api/fraud-evaluations` endpoint
- `GET /api/fraud-evaluations/{id}` endpoint
- filtered list endpoint for review use cases
- request and response DTOs for categorized transaction events
- domain rule evaluator abstraction
- one persisted fraud evaluation aggregate with child rule results
- explicit security config for local development and test
- first Flyway migration set
- runnable `Dockerfile`
- production-grade `README`

## Locked-In Decisions
- The take-home brief is implemented as a categorized transaction fraud evaluation service, not a generic event platform.
- The first input model is a transaction event, not a broad multi-domain envelope.
- The first decision model is tiered: `ALLOW`, `REVIEW`, `BLOCK`.
- The first rule set is code-defined and deterministic.
- Phase 1 rules are:
  - high amount
  - velocity
  - risky merchant category
  - unusual time
- `location anomaly` is deferred unless the first vertical slice is already stable.
- Build tool stays Maven.
- Persistence stays PostgreSQL-backed for both local development and tests.
- Security baseline now uses profile-aware behavior: `default` open for reviewer-local usability and `secure` with authenticated access controls.
- The current retrieval surface supports `decision`, `accountId`, `customerId`, `transactionId`, time range, and explicit summary sorting.
- A foundational rule-governance slice now exists with:
  - persisted rule metadata (`ruleCode + version`)
  - admin read visibility (`/api/admin/rules`)
  - deterministic lifecycle/activation boundaries enforced by policy + DB constraints

## Remaining Design Decisions
- Whether internal scoring should be numeric only, categorical only, or both while keeping the outward decision model simple
