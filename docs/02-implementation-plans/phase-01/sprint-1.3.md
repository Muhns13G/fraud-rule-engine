# Sprint 1.3 Implementation Plan

## Scope
Phase 1, Sprint 3 establishes the core code skeleton for the fraud evaluation vertical slice. This sprint should create the package structure, DTOs, domain model, rule contract, and database migration foundation without yet aiming for a fully wired end-to-end API.

## Sprint Goal
Create the core implementation building blocks so the project has a stable structure for persistence, evaluation logic, and API wiring.

## Task List

### Sprint 1.3.1
Establish the package structure under `com.capitec.fraudengine`.
- create `api`
- create `application`
- create `domain`
- create `infrastructure.persistence`
- create `infrastructure.security`
- create any minimal shared package needed for cross-cutting constants or errors

### Sprint 1.3.2
Create the Phase 1 request and response DTOs.
- request DTO for `POST /api/fraud-evaluations`
- response DTO for full evaluation retrieval
- summary DTO for list endpoint responses if needed early
- nested DTO for `location`
- DTO for individual rule results

### Sprint 1.3.3
Create the core domain model.
- `TransactionEvent`
- `FraudEvaluation`
- `RuleEvaluationResult`
- `FraudDecision`
- any supporting enums for:
  - severity
  - channel
  - transaction type
  - merchant category strategy

### Sprint 1.3.4
Create the rule engine contract and initial rule class skeletons.
- `FraudRule` interface
- abstract or shared support only if it reduces duplication cleanly
- skeleton implementations for:
  - high amount rule
  - velocity rule
  - risky merchant category rule
  - unusual time rule

### Sprint 1.3.5
Add Flyway and create the first migration.
- add Flyway dependency and configuration
- create initial schema for:
  - fraud evaluation header
  - rule result records
- keep schema aligned to the Phase 1 retrieval needs

### Sprint 1.3.6
Create persistence entry points.
- JPA entities for evaluation and rule results
- Spring Data repositories
- mapping approach between domain and persistence shapes

## Expected Output
- package structure exists
- DTO surface exists in code
- domain model exists in code
- rule contract and rule skeletons exist
- first Flyway migration exists
- persistence entities and repositories exist

## Notes
- Favor clear names and explainable model boundaries over premature abstractions.
- Do not overbuild for dynamic rule management in this sprint.
- Keep the rule logic framework-light and easy to unit test later.
