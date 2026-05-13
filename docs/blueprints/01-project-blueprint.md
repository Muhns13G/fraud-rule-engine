# Project Blueprint

## Purpose
Build a Spring Boot service that processes categorized transaction events, evaluates fraud rules per transaction, stores the resulting decision trail, and exposes retrieval APIs for review.

## Submission Boundary
- This is a Capitec take-home, not an internal long-lived platform build.
- The submission must look production-grade and interview-defensible.
- Non-code deliverables are part of the product:
  - runnable `Dockerfile`
  - real `README` with build, run, and test instructions
  - architecture decisions that can be explained clearly in interview discussion

## Verified Current State
- The repo is a single-module Spring Boot `4.0.6` application on Java `25`.
- The runtime stack for the implemented Phase 1 slice includes:
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
- The Phase 1 vertical slice is implemented:
  - domain model, rule set, and decision policy exist
  - persistence uses Flyway-managed PostgreSQL tables
  - controllers, services, repositories, and mappers are in place
  - Dockerfile and README now exist as deliverables

## Brief-Aligned Target System
- Accept categorized transaction events over HTTP.
- Evaluate a deterministic set of fraud rules against each transaction.
- Persist the inbound event, rule hits, aggregate decision, and explanation trail in PostgreSQL.
- Expose retrieval APIs for individual fraud evaluations and filtered review queries.
- Be structured so the initial code-defined rules can later evolve into governed rule management.

## First Vertical Slice
- `POST /api/fraud-evaluations`
  - accepts one categorized transaction event
  - evaluates the active static rule set
  - persists the decision record and rule hit details
  - returns the decision plus traceable reasons
- `GET /api/fraud-evaluations/{evaluationId}`
  - returns the persisted evaluation, rule hits, and final decision
- `GET /api/fraud-evaluations`
  - supports simple review filters such as `decision`, `accountId`, and time range

## First Request Shape
- Model the first input as a categorized card or account transaction event, not a generic catch-all envelope.
- Locked request fields for the first API version:
  - `transactionId`: required string
  - `accountId`: required string
  - `customerId`: required string
  - `amount`: required decimal
  - `currency`: required string, ISO-style uppercase code such as `ZAR`
  - `merchantId`: required string
  - `merchantCategory`: required string or enum-backed string
  - `transactionType`: required string or enum-backed string
  - `channel`: required string or enum-backed string
  - `eventTimestamp`: required offset datetime
  - `location`: optional structured object with:
    - `countryCode`
    - `city`
  - `reference`: optional string
- Keep the payload intentionally compact. The goal is enough signal for realistic rule evaluation, not a complete banking event taxonomy.

## First Response Shape
- The first evaluation response should return:
  - `evaluationId`
  - `transactionId`
  - `decision`
  - `decisionScore`
  - `evaluatedAt`
  - `ruleResults`
  - `traceSummary`
- Each `ruleResult` should return:
  - `ruleCode`
  - `ruleName`
  - `triggered`
  - `severity`
  - `scoreContribution`
  - `reason`
- The list endpoint can use a lighter summary projection, but the single-item retrieval should include the full rule hit trail.

## Proposed Package Shape
- `api`: request/response contracts, controllers, exception mapping
- `application`: use cases such as evaluate transaction and retrieve evaluation history
- `domain`: transaction event model, rule definitions, evaluation result, risk signals, decision policies
- `infrastructure.persistence`: JPA entities, repositories, database adapters
- `infrastructure.security`: security configuration and auth integration
- `infrastructure.observability`: metrics, health, audit, structured logging

## Core Domain Concepts To Introduce
- `TransactionEvent`: the normalized categorized transaction payload
- `FraudDecision`: `ALLOW`, `REVIEW`, or `BLOCK`
- `FraudRule`: a rule definition with business intent, status, and version
- `RuleEvaluationResult`: hit or miss outcome plus evidence and score contribution
- `FraudEvaluation`: the persisted outcome for one transaction event
- `DecisionTrace`: machine-readable explanation of why the final decision was produced

## First Rule Set
- Start with code-defined rules, not database-authored rules.
- The first release should implement 3-4 explicit rules that are easy to explain and test:
  - high-amount threshold rule
  - rapid-repeat transaction velocity rule for the same account or customer
  - risky merchant category rule
  - unusual time rule
- Defer `location anomaly` unless the first vertical slice is already stable and there is time for a clearly explainable heuristic.
- Starter thresholds for Phase 1:
  - high amount:
    - trigger `REVIEW` signal at `>= 10000.00 ZAR`
    - trigger `BLOCK` signal at `>= 25000.00 ZAR`
  - velocity:
    - trigger when `>= 3` transactions occur for the same `accountId` within `5 minutes`
  - risky merchant category:
    - start with a small flagged set such as `GAMBLING`, `CRYPTO`, `MONEY_TRANSFER`
  - unusual time:
    - trigger when the local transaction time falls between `00:00` and `04:00`
- Each rule should contribute:
  - a stable rule code
  - a human-readable reason
  - a severity or score contribution
  - supporting evidence fields needed for audit and debugging

## First Decision Policy
- Use a tiered outcome model: `ALLOW`, `REVIEW`, `BLOCK`.
- Prefer a transparent policy over clever scoring:
  - `BLOCK` for clearly unacceptable conditions
  - `REVIEW` for suspicious but non-terminal combinations
  - `ALLOW` when no material risk indicators are hit
- For Phase 1, use both:
  - an outward business decision of `ALLOW`, `REVIEW`, or `BLOCK`
  - a simple internal numeric score for aggregation and traceability
- Suggested starting score model:
  - `REVIEW`-level rule hit: `40` points
  - `BLOCK`-level rule hit: `100` points
- Suggested decision thresholds:
  - `ALLOW`: no triggered rules
  - `REVIEW`: total score from `1` to `99`
  - `BLOCK`: any blocking rule hit or total score `>= 100`

## Persistence Model
- Persist the transaction event separately from the evaluation result only if that separation makes querying clearer; for the take-home, a direct evaluation aggregate is acceptable.
- Minimum persisted records:
  - fraud evaluation header
  - normalized transaction fields used for decisioning
  - final decision and score
  - per-rule hit details
  - audit timestamps
- Design the schema so one evaluation can have many rule results.

## Suggested Execution Flow
1. Validate and normalize inbound request payloads.
2. Load recent transaction context needed for velocity checks and other history-based heuristics.
3. Evaluate rules in a deterministic order.
4. Aggregate rule results into a final fraud decision.
5. Persist the request, rule hits, and final decision for auditability.
6. Return the decision plus a concise reason payload to the caller.

## Cross-Cutting Decisions Worth Preserving
- Keep the rule engine deterministic and explainable; avoid hidden heuristics in early versions.
- Separate domain rule evaluation from HTTP and JPA concerns so rules stay testable without Spring context.
- Treat Swagger/OpenAPI as a contract aid, not the source of truth; DTOs and tests should define behavior.
- Use Flyway before adding real persistence logic so schema history is explicit from the start.
- Stay on Maven for this repo; it matches the generated project and keeps the submission easier to understand in a conservative enterprise setting.
- Default security behavior must be replaced with an intentional local and test setup before the API is presented as complete.
- For the take-home, prefer no real auth in local development; document that choice explicitly and describe likely production hardening paths in the README.
- Pin image versions before final submission; `postgres:latest` is acceptable scaffolding, not a final production-grade choice.

## Phase 1 Query Scope
- Retrieval filters for the first release are intentionally narrow:
  - `decision`
  - `accountId`
  - time range
- Defer additional filters such as `customerId`, `transactionId`, `merchantCategory`, `channel`, or rule-hit lookups until the core evaluation slice is stable.

## Immediate Gaps
- No dynamic rule management or rule version lifecycle exists yet.
- No authentication or authorization strategy exists beyond the intentionally open Phase 1 local/test posture.
- No observability conventions exist yet for fraud-domain metrics, correlation, or audit events beyond persisted evaluation history.
- No CI pipeline or deployment automation exists yet.
- Additional retrieval filters and deferred heuristics such as `location anomaly` remain out of scope for Phase 1.
