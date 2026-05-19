# Project Overview

## What This Project Is
`fraud-rule-engine` is a Spring Boot `4.0.6` / Java `25` take-home project for Capitec. The goal is to process categorized transaction events, evaluate fraud rules per transaction, store the resulting decision trail, and expose retrieval APIs.

## What Matters Most
- The submission must be production-grade, not just functional.
- The primary vertical slice is:
  - ingest transaction event
  - evaluate fraud rules
  - persist evaluation and rule hits
  - retrieve stored evaluations by API
- Non-code deliverables are mandatory:
  - runnable `Dockerfile`
  - real `README`
  - explainable architecture and design decisions

## Current Technical Baseline
- Build tool: Maven wrapper (`./mvnw`)
- Runtime: Spring Boot `4.0.6`, Java `25`
- Database: PostgreSQL
- Local development dependency: Docker
- Testing dependency: Testcontainers PostgreSQL
- Runtime persistence: Flyway-managed PostgreSQL schema with fraud evaluation and rule-result tables
- Persistence posture: evaluation rows retain business completion time plus row-level audit timestamps
- Operational posture: request correlation, focused evaluation metrics, and limited actuator exposure now exist for local inspection
- Observability posture is now broader and test-backed:
  - evaluation, retrieval, governance, and API-error metric coverage
  - governance lifecycle/version mutation audit events
  - security denial diagnostics and counters for authn/authz outcomes
  - request correlation propagation validated in observability contract tests
  - profile-specific observability contract tests for secure and production modes
- Security posture is now profile-aware and policy-explicit:
  - `default` remains open for reviewer usability
  - `secure` enforces HTTP Basic
  - `hardened`/`production` now enforce JWT token authentication
  - secure identity source is configurable (`IN_MEMORY` or `JDBC`)
  - secure secret-source strategy is explicit for in-memory mode (`ENV`, `PRE_ENCODED`, `EXTERNAL_MANAGER` seam)
  - secure credential rotation readiness hooks now support controlled overlap windows
  - Swagger/OpenAPI and actuator exposure are profile-driven
  - secure authorization is now role-segmented by surface:
    - `API_CLIENT` for core fraud-evaluation API
    - `OPS_READER` for governance read + actuator diagnostics
    - `GOVERNANCE_ADMIN` for governance mutation
    - `PLATFORM_ADMIN` as optional superset role
  - hardened/production JWT contract properties now include:
    - `issuer-uri`
    - `jwk-set-uri`
    - `audience`
    - `principal-claim`
    - `roles-claim`
    - `clock-skew-seconds`
  - reviewer-hosted deployments without external IdP are now explicitly documented to run on `secure` profile with authenticated actuator access
- Security and operations posture is now Phase 4 close-out complete:
  - secure-profile startup guardrails fail fast on unsafe identity/secret/role and actuator configurations
  - resilience checks cover datasource unavailability, invalid secure identity setup, and role-restricted actuator access
  - a dedicated Phase 4 regression suite now gates access-control matrix behavior, identity-provider modes, and observability policy expectations
  - security-denial diagnostics are captured as structured logs and dedicated authn/authz counters
- Delivery and regression posture is now Phase 3 close-out ready:
  - baseline CI workflow exists for compile/test/package
  - governance regression suite now verifies mutation, authorization, and retrieval consistency
  - Mockito/JDK 25 dynamic-agent warning path is addressed through explicit Surefire agent configuration
- API status:
  - Phase 1 evaluation and retrieval endpoints are implemented
  - admin rule-governance read visibility endpoints are now implemented
  - admin governance mutation endpoints now support lifecycle/activation transitions and controlled version registration
  - retrieval now supports paged responses, one-sided time filtering, review-oriented filters, and explicit summary sorting
  - location anomaly rule is now implemented with deterministic explainability
  - secure-profile governance mutation endpoints now have explicit admin/non-admin authorization regression coverage
  - secure-profile role matrix coverage now includes `API_CLIENT`, `OPS_READER`, `GOVERNANCE_ADMIN`, and `PLATFORM_ADMIN`
  - secure-profile identity contract coverage now includes secret-source validation, JDBC query contract validation, and rotation-window authentication checks
  - Swagger UI and OpenAPI spec are exposed for local review
- Delivery artifacts now exist:
  - runnable multi-stage `Dockerfile`
  - reviewer-facing `README`
  - unit and integration test coverage for the Phase 1 slice

## Phase 1 Locked Scope
- API:
  - `POST /api/fraud-evaluations`
  - `GET /api/fraud-evaluations/{evaluationId}`
  - `GET /api/fraud-evaluations`
- Decision model:
  - `ALLOW`
  - `REVIEW`
  - `BLOCK`
- Rules:
  - high amount
  - velocity
  - risky merchant category
  - unusual time
- Deferred unless time allows:
  - rule-hit retrieval filter

## Why This Shape
- It matches the project brief directly.
- It is small enough to finish well.
- It is strong enough to discuss in a senior backend interview.
