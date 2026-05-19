# Fraud Rule Engine

Spring Boot service for evaluating categorized transaction events against a deterministic Phase 1 fraud rule set, persisting the resulting decision trail, and exposing retrieval APIs for review.

This project was built as a Capitec take-home submission. The goal of the current slice is not to model a full fraud platform, but to deliver a production-grade, explainable vertical slice that is easy to run, test, and discuss.

## Tech Stack

- Java 25
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Flyway
- Spring Security
- SpringDoc OpenAPI
- Testcontainers
- Maven Wrapper

## Phase 1 Scope

Implemented API surface:

- `POST /api/fraud-evaluations`
- `GET /api/fraud-evaluations/{evaluationId}`
- `GET /api/fraud-evaluations`
- `GET /api/admin/rules`
- `GET /api/admin/rules/{ruleCode}/versions/{version}`

Implemented fraud rules:

- high amount
- velocity
- risky merchant category
- unusual time

Implemented decision model:

- `ALLOW`
- `REVIEW`
- `BLOCK`

Current retrieval filters:

- `decision`
- `accountId`
- `customerId`
- `transactionId`
- `from`
- `to`
- `sort`

Rule governance visibility:

- metadata is persisted per `ruleCode + version`
- active-rule list defaults to `GET /api/admin/rules`
- identity-level inspection is available at `GET /api/admin/rules/{ruleCode}/versions/{version}`

## Architecture Summary

The service follows a layered structure to keep fraud logic separate from transport and persistence concerns:

- `api`
  - controllers, DTOs, and exception handling
- `application`
  - orchestration for evaluation and retrieval use cases
- `domain`
  - transaction model, fraud rules, and decision policy
- `infrastructure.persistence`
  - JPA entities, repositories, and persistence mappers
  - includes a dedicated rule-governance metadata table with lifecycle constraints
- `infrastructure.security`
  - Phase 1 local/test security configuration
- `infrastructure.config`
  - OpenAPI and Flyway bootstrap configuration

Evaluation flow:

1. validate and normalize the inbound transaction payload
2. load recent transaction context for history-based checks
3. evaluate the active rule set in deterministic order
4. aggregate rule outcomes into a final fraud decision
5. persist the evaluation header and per-rule results
6. return the decision, score, and traceable rule trail

## Fraud Rules

Current thresholds and heuristics:

- high amount
  - `REVIEW` at `>= 10000.00 ZAR`
  - `BLOCK` at `>= 25000.00 ZAR`
- velocity
  - `REVIEW` when `>= 3` transactions occur for the same `accountId` within `5 minutes`
- risky merchant category
  - flagged categories: `GAMBLING`, `CRYPTO`, `MONEY_TRANSFER`
- unusual time
  - `REVIEW` for transactions between `00:00` and `04:00`

Score model:

- review-level rule hit: `40`
- block-level rule hit: `100`
- final decision:
  - `ALLOW` when no rules trigger
  - `REVIEW` when score is `1-99`
  - `BLOCK` when any blocking rule triggers or total score is `>= 100`

## Running Locally

### Prerequisites

- Java 25
- Docker Desktop or another running Docker engine

### Option 1: Run with Spring Boot and Docker Compose

This is the easiest local path. Spring Boot will use `compose.yaml` to start PostgreSQL automatically.

```bash
./mvnw spring-boot:run
```

Useful local URLs:

- API base: `http://localhost:8080/api/fraud-evaluations`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI spec: `http://localhost:8080/v3/api-docs`
- Actuator health: `http://localhost:8080/actuator/health`
- Actuator info: `http://localhost:8080/actuator/info`
- Actuator metrics: `http://localhost:8080/actuator/metrics`

### Option 1b: Run in secure profile (HTTP Basic)

Use this mode to verify the Sprint 2.4 secured posture.

```bash
SPRING_PROFILES_ACTIVE=secure \
FRAUD_ENGINE_SECURE_USER=secure-user \
FRAUD_ENGINE_SECURE_PASSWORD=change-me-secure \
FRAUD_ENGINE_SECURE_ROLE=API_CLIENT \
./mvnw spring-boot:run
```

Example authenticated request:

```bash
curl -u secure-user:change-me-secure \
  http://localhost:8080/actuator/health
```

### Option 2: Run PostgreSQL yourself, then start the app

Start the database:

```bash
docker compose up -d postgres
```

Then run the app:

```bash
./mvnw spring-boot:run
```

The local Compose service is pinned to `postgres:18.3`.

For PostgreSQL `18+`, the Compose volume is mounted at `/var/lib/postgresql` rather than `/var/lib/postgresql/data` to match the image's major-version-aware data layout.

## Running Tests

All tests require Docker because integration tests and the context test use Testcontainers.

Run the full suite:

```bash
./mvnw test
```

Build the jar without rerunning tests:

```bash
./mvnw -DskipTests package
```

Current automated coverage includes:

- unit tests for all four fraud rules
- unit tests for the decision aggregation policy
- integration tests for the repository layer
- integration tests for the API layer
- application context startup test

## Docker

Build the application image:

```bash
docker build -t fraud-rule-engine:local .
```

Run the database service:

```bash
docker compose up -d postgres
```

The application `Dockerfile` is multi-stage and uses pinned Eclipse Temurin Java 25 images for both build and runtime.

## API Summary

### Create evaluation

`POST /api/fraud-evaluations`

Example request:

```json
{
  "transactionId": "txn-001",
  "accountId": "account-001",
  "customerId": "customer-001",
  "amount": 26000.00,
  "currency": "ZAR",
  "merchantId": "merchant-123",
  "merchantCategory": "RETAIL",
  "transactionType": "PURCHASE",
  "channel": "ONLINE",
  "eventTimestamp": "2026-05-12T10:00:00+02:00",
  "location": {
    "countryCode": "ZA",
    "city": "Cape Town"
  },
  "reference": "sample-request"
}
```

Example `curl`:

```bash
curl -X POST http://localhost:8080/api/fraud-evaluations \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "txn-001",
    "accountId": "account-001",
    "customerId": "customer-001",
    "amount": 26000.00,
    "currency": "ZAR",
    "merchantId": "merchant-123",
    "merchantCategory": "RETAIL",
    "transactionType": "PURCHASE",
    "channel": "ONLINE",
    "eventTimestamp": "2026-05-12T10:00:00+02:00",
    "location": {
      "countryCode": "ZA",
      "city": "Cape Town"
    },
    "reference": "sample-request"
  }'
```

### Retrieve one evaluation

`GET /api/fraud-evaluations/{evaluationId}`

Example:

```bash
curl http://localhost:8080/api/fraud-evaluations/{evaluationId}
```

### List governed rules (admin read)

`GET /api/admin/rules`

Examples:

```bash
curl http://localhost:8080/api/admin/rules
curl http://localhost:8080/api/admin/rules?activeOnly=false
```

### Retrieve governed rule metadata by identity (admin read)

`GET /api/admin/rules/{ruleCode}/versions/{version}`

Example:

```bash
curl http://localhost:8080/api/admin/rules/HIGH_AMOUNT/versions/1.0.0
```

### List evaluation summaries

`GET /api/fraud-evaluations`

Supported filters:

- `decision`
- `accountId`
- `customerId`
- `transactionId`
- `from`
- `to`
- `sort`

Example:

```bash
curl "http://localhost:8080/api/fraud-evaluations?decision=REVIEW&accountId=account-001&customerId=customer-001&from=2026-05-12T09:00:00%2B02:00&to=2026-05-12T12:00:00%2B02:00&sort=NEWEST_FIRST"
```

Supported sort values:

- `NEWEST_FIRST` (default)
- `OLDEST_FIRST`

## Data Model

The initial schema contains:

- `fraud_evaluations`
  - normalized transaction fields
  - final decision
  - decision score
  - trace summary
  - evaluation timestamp
  - persistence audit timestamps for row creation and last update
- `fraud_rule_results`
  - one row per evaluated rule result
  - severity, score contribution, and reason

Flyway manages schema evolution from `src/main/resources/db/migration`.

## Observability

Current local observability posture includes:

- structured `key=value` service logs around:
  - fraud evaluation start
  - recent-history lookup
  - fraud evaluation completion
  - retrieval queries
  - request-validation and request-parsing failures
- lightweight request correlation via `X-Request-Id`
  - inbound `X-Request-Id` values are reused when provided
  - otherwise the service generates a UUID
  - the request ID is echoed back in the response header
  - the request ID is included in the console log pattern
- Micrometer metrics for the evaluation path:
  - `fraud.evaluation.completed.total`
  - `fraud.evaluation.decision.count`
  - `fraud.evaluation.rule.triggered.count`
  - `fraud.evaluation.duration`
- Actuator exposure limited to:
  - `health`
  - `info`
  - `metrics`

This is intentionally lightweight and optimized for local review, not production-scale observability infrastructure.

## Security Posture

Security is now profile-aware:

- `default` profile:
  - intentionally open for local/reviewer usability
  - `/api/**`, Swagger/OpenAPI, and exposed actuator endpoints are reachable without auth
  - Swagger/OpenAPI exposure defaults to enabled
  - actuator exposure defaults to `health,info,metrics`
- `secure` profile:
  - HTTP Basic authentication enabled
  - protected surface:
    - `/api/**`
    - `/swagger-ui.html`
    - `/swagger-ui/**`
    - `/v3/api-docs/**`
    - `/actuator/**`
  - Swagger/OpenAPI exposure defaults to disabled (can be re-enabled by secure-profile env overrides)
  - actuator exposure defaults to `health,info` (can be widened by secure-profile env overrides)
  - credentials come from env-backed `app.security.secure-profile.*` properties
  - identity provider is configurable:
    - `IN_MEMORY` (default) for local/reviewer secure mode
    - `JDBC` for persistent-friendly identity sourcing
  - secure credentials support either:
    - raw password (`password`) for local use, or
    - pre-encoded password (`password-encoded`) for non-local secret workflows
  - health details are restricted with `management.endpoint.health.show-details=when_authorized`

This remains a pragmatic take-home baseline, not full enterprise identity integration.

## Known Simplifications

- rules are code-defined, not database-authored
- no dynamic rule management or rule version administration yet
- `location anomaly` is intentionally deferred
- retrieval remains intentionally bounded and is not intended to become a generic reporting surface
- no CI pipeline exists yet
- security is profile-aware with configurable identity provider, but enterprise IAM integration is still out of scope
- SpringDoc and actuator exposure are now explicitly profile-driven

## Future Improvements

- centralize fraud thresholds and history-window configuration
- evolve secured mode beyond in-memory Basic Auth when enterprise identity requirements are in scope
- introduce richer observability and metrics
- expand retrieval filters and audit support
- add governed rule lifecycle management
- revisit `location anomaly` once a clean heuristic and data strategy are agreed

## Verification Snapshot

Most recent local verification for the current Phase 2.4 slice:

- `./mvnw test`
  - passed
  - `44` tests
- `docker build -t fraud-rule-engine:local .`
  - passed

## Notes

`HELP.md` is the generated Spring starter help file. This `README` is the primary reviewer-facing project document.
