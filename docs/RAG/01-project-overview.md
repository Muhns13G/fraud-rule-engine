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
