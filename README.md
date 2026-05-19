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

## Current Implemented Scope

Implemented API surface:

- `POST /api/fraud-evaluations`
- `GET /api/fraud-evaluations/{evaluationId}`
- `GET /api/fraud-evaluations`
- `GET /api/admin/rules`
- `GET /api/admin/rules/{ruleCode}/versions/{version}`
- `PATCH /api/admin/rules/{ruleCode}/versions/{version}/state`
- `POST /api/admin/rules/{ruleCode}/versions`

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
- constrained state mutation is available via `PATCH /api/admin/rules/{ruleCode}/versions/{version}/state`
- controlled version registration is available via `POST /api/admin/rules/{ruleCode}/versions`

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

### Environment Configuration Templates

Reference templates are available under:

- `docs/operations/env/local-reviewer.env.template`
- `docs/operations/env/secure.env.template`
- `docs/operations/env/production.env.template`

These templates define the baseline property contracts for local/reviewer, secure, and production-style runs.

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
- secure-profile and default-profile security behavior matrix tests
- governance mutation/retrieval regression coverage

CI baseline:
- GitHub Actions workflow at `.github/workflows/ci.yml` runs compile, test, and package checks.
- Phase 4 security/operations regression gate is also wired in CI and can be run locally via:
  - `./scripts/run-phase4-security-ops-regression.sh`

Phase status:
- Phase 4 (Security and Operations) is now closed through Sprint `4.4`, including profile policy hardening, resilience validation, and cross-sprint regression gating.

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
- Additional operational metrics:
  - `fraud.security.authn.denied.total`
  - `fraud.security.authz.denied.total`
  - retrieval/governance/API-error counters (see `ObservabilityContractIntegrationTest`)
- Actuator exposure limited to:
  - `health`
  - `info`
  - `metrics`

This is intentionally lightweight and optimized for local review, not production-scale observability infrastructure.

### Operational Runbook Baseline

Profile-specific observability access expectations:

- `default` profile:
  - actuator defaults: `health,info,metrics`
  - Swagger/OpenAPI enabled
  - no authentication enforced (local-only posture)
- `secure` profile:
  - actuator defaults: `health,info` (authenticated)
  - Swagger/OpenAPI disabled by default
  - auth required for API and observability surfaces
- `production` profile:
  - actuator defaults: `health` only
  - health details disabled (`show-details=never`)
  - Swagger/OpenAPI disabled

Incident triage signals:

1. Start with the request ID (`X-Request-Id`) from the client/response.
2. Trace logs by request ID:
   - evaluation/retrieval flow logs
   - security denial diagnostics:
     - `security_authn_denied` (`401` path)
     - `security_authz_denied` (`403` path)
3. Check counters in `/actuator/metrics` (where exposed) for:
   - denial-rate changes (`fraud.security.authn.denied.total`, `fraud.security.authz.denied.total`)
   - evaluation/retrieval/governance/error-volume shifts
4. Confirm profile exposure contract before deeper debugging:
   - hidden endpoints should return `404` for disabled surfaces in production mode.

Safe local-vs-non-local posture:

- local reviewer workflow: `default` profile is acceptable by design.
- any shared/non-local environment: use `secure` (or stricter) profile.
- production-like environments: use `production` profile endpoint exposure defaults and keep docs endpoints disabled.

## Security Posture

Security is now profile-aware:

- `default` profile:
  - intentionally open for local/reviewer usability
  - `/api/**`, Swagger/OpenAPI, and exposed actuator endpoints are reachable without auth
  - Swagger/OpenAPI exposure defaults to enabled
  - actuator exposure defaults to `health,info,metrics`
  - startup guardrail warning is emitted to make local-only intent explicit
  - should not be used for non-local or internet-exposed deployment
- `secure` profile:
  - HTTP Basic authentication enabled
  - least-privilege role model:
    - `API_CLIENT` for core fraud-evaluation API access
    - `OPS_READER` for governance reads and actuator diagnostics
    - `GOVERNANCE_ADMIN` for governance mutation
    - `PLATFORM_ADMIN` optional super-role for broad secure-surface access
  - protected surface:
    - `/api/**`
    - `/swagger-ui.html`
    - `/swagger-ui/**`
    - `/v3/api-docs/**`
    - `/actuator/**`
  - secure route authorization model:
    - `PATCH/POST /api/admin/rules/**` requires `GOVERNANCE_ADMIN` or `PLATFORM_ADMIN`
    - `GET /api/admin/rules/**` requires `OPS_READER` or stronger
    - `/actuator/**` requires `OPS_READER` or stronger
    - `/api/fraud-evaluations...` requires `API_CLIENT` or stronger
  - Swagger/OpenAPI exposure defaults to disabled (can be re-enabled by secure-profile env overrides)
  - actuator exposure defaults to `health,info` (can be widened by secure-profile env overrides)
  - credentials come from env-backed `app.security.secure-profile.*` properties
  - identity provider is configurable:
    - `IN_MEMORY` (default) for local/reviewer secure mode
    - `JDBC` for persistent-friendly identity sourcing
  - JDBC identity mode now validates query contracts and falls back to safe defaults aligned to Spring Security `users` / `authorities` schema
  - secure credentials support either:
    - raw password (`password`) for local use, or
    - pre-encoded password (`password-encoded`) for non-local secret workflows
  - secret source strategy is explicit for `IN_MEMORY` mode:
    - `ENV`
    - `PRE_ENCODED`
    - `EXTERNAL_MANAGER` (via `SecureProfileSecretSupplier` seam)
  - credential rotation readiness hook (IN_MEMORY mode):
    - optional overlap window via `rotation-enabled=true`
    - secondary credential fields:
      - `rotation-username`
      - `rotation-password` or `rotation-password-encoded`
    - both primary and rotation credentials authenticate during overlap; remove rotation fields after cutover
  - health details are restricted with `management.endpoint.health.show-details=when_authorized`

This remains a pragmatic take-home baseline, not full enterprise identity integration.

Deferred by design:
- enterprise IAM integrations (OAuth2/JWT/OIDC, external IdP tenancy, centralized policy engines) are intentionally postponed to a future phase so this project remains small, reviewable, and deterministic.

For any non-local run, prefer secure mode:

```bash
SPRING_PROFILES_ACTIVE=secure ./mvnw spring-boot:run
```

### Secure Credential Rotation Workflow (IN_MEMORY)

1. Configure primary secure credential as usual.
2. Set `rotation-enabled=true` and provide a distinct `rotation-username` plus one rotation password field.
3. Deploy and migrate callers to the rotation credential during the overlap window.
4. Promote the rotated credential to primary configuration.
5. Disable rotation (`rotation-enabled=false`) and remove rotation fields.

## Known Simplifications

- rules are code-defined, not database-authored
- no dynamic executable-rule authoring beyond governed metadata transitions/versioning yet
- retrieval remains intentionally bounded and is not intended to become a generic reporting surface
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

Most recent local verification for the current security matrix slice:

- `./mvnw -Dtest=SecureProfileSecurityIntegrationTest,SecureProfileGovernanceAuthorizationIntegrationTest,SecureProfileGovernanceAdminIntegrationTest,SecureProfilePlatformAdminIntegrationTest,DefaultProfileSecurityIntegrationTest test`
  - passed
  - `27` tests
- `docker build -t fraud-rule-engine:local .`
  - passed

## Notes

`HELP.md` is the generated Spring starter help file. This `README` is the primary reviewer-facing project document.
