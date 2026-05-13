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
- Phase 1 local security is intentionally permissive for reviewer usability

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
  - intentional permissive local/test security config

## Architectural Priorities
- Keep rule logic framework-light and testable without Spring context.
- Keep persistence concerns out of the core rule logic.
- Optimize for explainability over sophistication.
- Prefer one strong vertical slice over broad partial features.
