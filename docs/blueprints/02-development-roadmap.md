# Development Roadmap

## Planning Principle
Move from a working vertical slice to a configurable rules platform. Do not introduce dynamic rule authoring before the service can reliably evaluate, persist, and explain a small static rule set.

## Phase 1: Foundation Slice
- Add a real package structure under `api`, `application`, `domain`, and `infrastructure`.
- Define the first API contract for fraud evaluation.
- Implement a minimal in-memory rule engine with 2-3 explicit rules.
- Replace default security behavior with an intentional local-development configuration.
- Add focused unit tests for rule evaluation outside the Spring context.

## Phase 2: Persistence And Auditability
- Add migration tooling and create the first PostgreSQL schema.
- Persist evaluation requests, decisions, and rule hit details.
- Introduce JPA entities and repository adapters without leaking persistence types into the domain layer.
- Add integration tests that prove persistence and decision history behavior.

## Phase 3: Rule Management
- Model rule identity, status, versioning, and activation lifecycle.
- Add admin-facing endpoints for listing and activating rule versions.
- Decide whether rule logic remains code-backed or becomes data-driven with a constrained expression model.
- Add validation to prevent invalid or ambiguous rule configurations.

## Phase 4: Security And Operations
- Replace generated-password defaults with explicit authentication and authorization strategy.
- Add actuator exposure policy, structured logging, and domain metrics such as evaluation counts and rule hit rates.
- Define error handling, correlation IDs, and audit event boundaries.
- Add environment-specific configuration patterns for local, test, and production.

## Phase 5: Production Hardening
- Add CI for compile, test, and packaging.
- Add test layers:
  - unit tests for rule logic
  - repository integration tests
  - API tests for request/response behavior
- Pin infrastructure image versions instead of using `postgres:latest`.
- Revisit performance characteristics for high-throughput decision evaluation.

## First Concrete Deliverables
- `POST /api/fraud-evaluations` endpoint
- request and response DTOs
- domain rule evaluator abstraction
- one persisted decision history table
- explicit security config for local development and test
- first migration set

## Open Design Decisions
- What is the first evaluation input shape: card transaction, account event, login risk, or a generic event envelope?
- Should the first decision model be binary (`ALLOW` / `BLOCK`) or tiered (`ALLOW` / `REVIEW` / `BLOCK`)?
- Will rule thresholds and parameters live in code, configuration, or database tables in the first release?
- What audit detail is required for the take-home outcome versus a production-ready bank integration?
