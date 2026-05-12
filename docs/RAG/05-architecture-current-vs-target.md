# Architecture: Current Vs Target

## Current State
- Single-module Spring Boot app
- Only bootstrap production class exists
- Testcontainers-backed Postgres smoke test exists
- No business domain, API, persistence model, migrations, or rule engine yet

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
