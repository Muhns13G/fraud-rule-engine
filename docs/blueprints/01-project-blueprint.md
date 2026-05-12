# Project Blueprint

## Purpose
Build a Spring Boot service that evaluates fraud rules against transaction or event inputs and returns a decision that upstream systems can act on.

## Verified Current State
- The repo is a single-module Spring Boot `4.0.6` application on Java `25`.
- The only production code today is the bootstrap class: `com.capitec.fraudengine.FraudRuleEngineApplication`.
- The runtime stack already points toward a REST + persistence service:
  - Spring Web MVC
  - Spring Data JPA
  - Spring Validation
  - Spring Security
  - Spring Actuator
  - SpringDoc OpenAPI
  - PostgreSQL runtime driver
- Local development and tests both assume Docker-backed Postgres:
  - `compose.yaml` provides `postgres`
  - tests use Testcontainers Postgres through `TestcontainersConfiguration`
- No domain model, controllers, services, repositories, migrations, or business rules are implemented yet.

## Target System
- Expose an HTTP API for fraud assessment requests.
- Evaluate one or more rules against a normalized request model.
- Persist rules, rule versions, and decision/audit history in PostgreSQL.
- Return deterministic decision results with traceable reasons.
- Support future expansion from hard-coded rule execution to configurable rule management.

## Proposed Package Shape
- `api`: request/response contracts, controllers, exception mapping
- `application`: use cases such as evaluate transaction, list rules, activate rule version
- `domain`: rule definitions, evaluation result, risk signals, decision policies
- `infrastructure.persistence`: JPA entities, repositories, database adapters
- `infrastructure.security`: security configuration and auth integration
- `infrastructure.observability`: metrics, health, audit, structured logging

## Core Domain Concepts To Introduce
- `FraudEvaluationRequest`: the normalized input for a transaction or fraud event
- `FraudDecision`: allow, review, block, or similar explicit outcome
- `FraudRule`: a rule definition with business intent, status, and version
- `RuleEvaluationResult`: pass/fail outcome plus evidence or score contribution
- `DecisionTrace`: machine-readable explanation of why the final decision was produced

## Suggested Execution Flow
1. Validate and normalize inbound request payloads.
2. Load the active rule set for the relevant context.
3. Evaluate rules in a deterministic order.
4. Aggregate rule results into a final fraud decision.
5. Persist the request, rule hits, and final decision for auditability.
6. Return the decision plus a concise reason payload to the caller.

## Cross-Cutting Decisions Worth Preserving
- Keep the rule engine deterministic and explainable; avoid hidden heuristics in early versions.
- Separate domain rule evaluation from HTTP and JPA concerns so rules stay testable without Spring context.
- Treat Swagger/OpenAPI as a contract aid, not the source of truth; DTOs and tests should define behavior.
- Use Flyway or Liquibase before adding real persistence logic so schema history is explicit from the start.
- Decide early whether rules are code-defined, database-defined, or hybrid; that choice will shape the service boundaries.

## Immediate Gaps
- No API contract exists yet.
- No database schema or migration tooling exists yet.
- No authentication or authorization policy has been configured beyond Spring Security defaults.
- No observability conventions exist yet for logs, metrics, or audit events.
- No focused unit or integration test structure exists beyond context startup.
