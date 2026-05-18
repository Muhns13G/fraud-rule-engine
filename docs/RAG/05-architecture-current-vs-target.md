# Architecture: Current Vs Target

## Current State
- Single-module Spring Boot app
- Phase 1 API, application, domain, persistence, security, and config layers are implemented
- Flyway migration exists for the initial PostgreSQL schema
- Persisted evaluation headers now carry row-level audit timestamps in addition to business evaluation timestamps
- Testcontainers-backed integration tests and focused unit tests exist
- Lightweight observability conventions now exist:
  - structured evaluation and retrieval logs
  - request correlation via `X-Request-Id`
  - evaluation metrics through Micrometer
  - limited actuator exposure for local inspection
- Security is now profile-aware:
  - `default` profile remains permissive for reviewer usability
  - `secure` profile enforces HTTP Basic with env-backed in-memory credentials
  - secured endpoints include API, Swagger/OpenAPI, and actuator routes
- Rule governance groundwork now exists:
  - persisted metadata identity (`ruleCode + version`)
  - admin read and constrained mutation endpoints for governed rule metadata
  - deterministic lifecycle/activation boundary validation with DB constraints
- Retrieval layer is now review-oriented and scalable:
  - paged list responses for `GET /api/fraud-evaluations`
  - one-sided time filtering (`from`-only / `to`-only) and bounded ranges
  - additional low-risk filters (`merchantCategory`, `channel`)

## Target Phase 1 Shape
- `api`
  - controllers
  - DTOs
  - exception handling
- `application`
  - fraud evaluation use case
  - retrieval use case
- `domain`
  - transaction event model
  - fraud evaluation model
  - fraud rule contract and implementations
  - decision policy
- `infrastructure.persistence`
  - JPA entities
  - Spring Data repositories
  - Flyway migrations
- `infrastructure.security`
  - profile-aware security config (`default` open + `secure` authenticated)

## Architectural Priorities
- Keep rule logic framework-light and testable without Spring context.
- Keep persistence concerns out of the core rule logic.
- Optimize for explainability over sophistication.
- Prefer one strong vertical slice over broad partial features.
