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
- Security posture: profile-aware baseline now exists (`default` open, `secure` HTTP Basic with env-backed credentials)
- API status:
  - Phase 1 evaluation and retrieval endpoints are implemented
  - retrieval now supports a modest review-oriented filter set plus explicit summary sorting
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
  - location anomaly

## Why This Shape
- It matches the project brief directly.
- It is small enough to finish well.
- It is strong enough to discuss in a senior backend interview.
