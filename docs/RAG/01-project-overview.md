# Project Overview

## What This Project Is
`fraud-rule-engine` is a Spring Boot `4.0.6` / Java `25` backend for processing categorized transaction events, evaluating fraud rules, storing the resulting decision trail, and exposing retrieval and governance APIs.

## What Matters Most
- The implementation should be production-style, not just functional.
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
  - `default` remains open for local validation usability
  - `secure` enforces HTTP Basic
  - `hardened`/`production` now enforce JWT token authentication
  - secure identity source is configurable (`IN_MEMORY` or `JDBC`)
  - secure secret-source strategy is explicit for in-memory mode (`ENV`, `PRE_ENCODED`, `EXTERNAL_MANAGER` seam)
  - secure credential rotation now follows explicit phases (`PREPARE`, `OVERLAP`, `CUTOVER`, `RETIRE`) with fail-fast validation
  - secure diagnostics now expose redacted credential/rotation posture through `/actuator/info`
  - operational secure rotation runbook now exists for bootstrap, cutover, and rollback procedures
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
  - hardened/production startup now requires `issuer-uri`, `jwk-set-uri`, and `audience`
  - hardened JWT validation now enforces issuer/audience trust boundaries
  - hosted deployments without external IdP can run on `secure` profile with authenticated actuator access
- Security and operations posture includes:
  - secure-profile startup guardrails fail fast on unsafe identity/secret/role and actuator configurations
  - resilience checks cover datasource unavailability, invalid secure identity setup, and role-restricted actuator access
  - a dedicated security and operations regression suite now gates access-control matrix behavior, identity-provider modes, and observability policy expectations
  - security-denial diagnostics are captured as structured logs and dedicated authn/authz counters
- Delivery and regression posture includes:
  - baseline CI workflow exists for compile/test/package
  - governance regression suite now verifies mutation, authorization, and retrieval consistency
  - Mockito/JDK 25 dynamic-agent warning path is addressed through explicit Surefire agent configuration
- API status:
  - evaluation and retrieval endpoints are implemented
  - admin rule-governance read visibility endpoints are now implemented
  - admin governance mutation endpoints now support lifecycle/activation transitions, controlled version registration, and semantic workflow actions
  - governance history is now durably persisted and retrievable through paged version/history admin reads
  - retrieval now supports paged responses, one-sided time filtering, review-oriented filters, and explicit summary sorting
  - retrieval now supports rule-hit filtering (`ruleHit`, `ruleHitMatch`) for investigation workflows
  - location anomaly rule is now implemented with deterministic explainability
  - secure-profile governance mutation endpoints now have explicit admin/non-admin authorization regression coverage
  - secure-profile role matrix coverage now includes `API_CLIENT`, `OPS_READER`, `GOVERNANCE_ADMIN`, and `PLATFORM_ADMIN`
  - secure-profile identity contract coverage now includes secret-source validation, JDBC query contract validation, and rotation-window authentication checks
  - secure-profile integration coverage now includes external secret resolution plus overlap/cutover/retire rotation flows
  - validation scripts now cover local + hosted secure-profile verification matrices
  - Swagger UI and OpenAPI spec are exposed for local review
- Delivery artifacts now exist:
  - runnable multi-stage `Dockerfile`
  - public-facing `README`
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
- Extended in later phases:
  - rule-hit retrieval filter (implemented in Sprint `5.4`)

## Why This Shape
- It matches the project brief directly.
- It is small enough to finish well.
- It is strong enough to discuss in a senior backend interview.
