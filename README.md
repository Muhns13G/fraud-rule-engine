# Fraud Rule Engine

Spring Boot service for evaluating categorized transaction events against a deterministic fraud rule set, persisting the resulting decision trail, and exposing retrieval and governance APIs for review and operations.

The goal of the current slice is not to model a full fraud platform, but to deliver a production-grade, explainable vertical slice that is easy to run, test, and discuss.

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

## Implemented Scope

API surface:

- `POST /api/fraud-evaluations`
- `GET /api/fraud-evaluations/{evaluationId}`
- `GET /api/fraud-evaluations`
- `GET /api/admin/rules`
- `GET /api/admin/rules/{ruleCode}/versions/{version}`
- `PATCH /api/admin/rules/{ruleCode}/versions/{version}/state`
- `POST /api/admin/rules/{ruleCode}/versions`

Current fraud rules:

- high amount
- velocity
- risky merchant category
- unusual time

Decision model:

- `ALLOW`
- `REVIEW`
- `BLOCK`

Current retrieval filters:

- `decision`
- `accountId`
- `customerId`
- `transactionId`
- `merchantCategory`
- `channel`
- `ruleHit` (repeatable)
- `ruleHitMatch` (`ANY` default, `ALL`)
- `from`
- `to`
- `sort`

Rule governance support:

- persisted metadata per `ruleCode + version`
- active-rule listing
- version inspection
- constrained state transition
- controlled version registration

## Architecture Summary

The service follows a layered structure:

- `api`
  - controllers, DTOs, and exception handling
- `application`
  - orchestration for evaluation, retrieval, and governance use cases
- `domain`
  - transaction model, fraud rules, and decision policy
- `infrastructure.persistence`
  - JPA entities, repositories, and persistence mappers
- `infrastructure.security`
  - profile-aware security configuration and guardrails
- `infrastructure.config`
  - OpenAPI, Flyway, and application bootstrap configuration

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

Spring Boot will use `compose.yaml` to start PostgreSQL automatically.

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

Port behavior:

- local default is `8080`
- hosted environments can override the runtime port via `PORT` (`server.port=${PORT:8080}`)

### Option 2: Run in secure profile

Use generic local credentials for an authenticated run:

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

### Option 3: Start PostgreSQL yourself, then run the app

```bash
docker compose up -d postgres
./mvnw spring-boot:run
```

The local Compose service is pinned to `postgres:18.3`.

For PostgreSQL `18+`, the Compose volume is mounted at `/var/lib/postgresql` rather than `/var/lib/postgresql/data` to match the image's major-version-aware data layout.

### Environment Configuration Templates

Reference templates are available under:

- `docs/operations/env/local-reviewer.env.template`
- `docs/operations/env/secure.env.template`
- `docs/operations/env/production.env.template`
- `docs/operations/runbooks/secure-credential-rotation-runbook.md`

These templates define the baseline property contracts for local, secure, and production-style runs.

### Hosted Deployment Prerequisites

For hosted runtime, use the `production` profile by default:

```bash
SPRING_PROFILES_ACTIVE=production
```

Required datasource environment variables:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}
SPRING_DATASOURCE_USERNAME=${PGUSER}
SPRING_DATASOURCE_PASSWORD=${PGPASSWORD}
```

Notes:

- ensure your app service is linked to a PostgreSQL service so `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, and `PGPASSWORD` are injected
- keep internal app port at `8080`; hosted platforms should route via `PORT`
- health check endpoint for deployment verification: `/actuator/health`

`production` remains available for environments that provide hardened JWT/OIDC configuration (`issuer-uri`, `jwk-set-uri`, and audience).

### Profile Matrix and Verification Path

Security posture by profile:

- `default`: unauthenticated local ergonomics with guardrails to prevent hosted misuse
- `secure`: HTTP Basic authentication with explicit role boundaries
- `hardened` / `production`: JWT/OIDC resource-server posture with claim-to-role mapping

Useful local verification path:

1. Validate local-open behavior (`default`):

```bash
./mvnw -Dtest=DefaultProfileSecurityIntegrationTest test
```

2. Validate secure behavior (`secure`):

```bash
./mvnw -Dtest=SecureProfileSecurityIntegrationTest test
```

3. Validate hardened non-local behavior (`hardened`):

```bash
./mvnw -Dtest=HardenedProfileSecurityIntegrationTest test
```

4. Validate production observability posture (`production` + JWT contract):

```bash
./mvnw -Dtest=ProductionProfileObservabilityIntegrationTest test
```

Notes:

- hardened/production tests use mocked JWT authentication in integration tests; no live external IdP is required for local verification
- live hosted `production` requires valid `issuer-uri`, `jwk-set-uri`, and audience values from your IdP
- hardened/production startup fails fast if `issuer-uri`, `jwk-set-uri`, or `audience` is missing

## Running Tests

All tests require Docker because integration tests and context startup use Testcontainers.

Run the full suite:

```bash
./mvnw test
```

Compile without tests:

```bash
./mvnw compile
```

Build the jar:

```bash
./mvnw package
```

Current automated coverage includes:

- unit tests for all fraud rules
- decision-policy unit tests
- repository integration tests
- API integration tests
- application context startup test
- profile-aware security matrix tests
- governance mutation and retrieval regression coverage

CI baseline:

- `.github/workflows/ci.yml` runs compile, test, and package checks
- repository hygiene checks are wired into CI
- security and production-hardening regression scripts are included under `scripts/`
- `./scripts/run-security-ops-regression.sh`
- `./scripts/run-production-hardening-gates.sh`
- `./scripts/run-performance-reliability-smoke.sh`
- `./scripts/run-local-validation.sh`

Current performance smoke thresholds:

- evaluation latency p95 <= `1500ms`
- retrieval latency p95 <= `800ms`
- velocity temporal correctness rejects future-dated events in window counts

## Verification Snapshot

Most recent local verification for the current security matrix slice:

- `./mvnw -Dtest=SecureProfileSecurityIntegrationTest,SecureProfileGovernanceAuthorizationIntegrationTest,SecureProfileGovernanceAdminIntegrationTest,SecureProfilePlatformAdminIntegrationTest,DefaultProfileSecurityIntegrationTest test`
- `docker build -t fraud-rule-engine:local .`

## Docker

Build the application image:

```bash
docker build -t fraud-rule-engine:local .
```

Run the database service:

```bash
docker compose up -d postgres
```

The `Dockerfile` is multi-stage and uses pinned Eclipse Temurin Java 25 images for both build and runtime.

## Postman Quick Start

Import these files from `docs/operations/postman`:

- `fraud-rule-engine-reviewer.postman_collection.json`
- `fraud-rule-engine-local.postman_environment.json`

Recommended run order:

1. Select the local environment.
2. Run `Actuator` requests (`Health`, `Info`, `Metrics`).
3. Run `Fraud Evaluations` requests (`Create`, `Get by Id`, `List`).

Timestamp reminders:

- `eventTimestamp` must be ISO-8601 with timezone offset (`Z` or `+02:00`)
- for query filters (`from`, `to`), URL-encode `+` as `%2B`

## API Summary

### Create evaluation

`POST /api/fraud-evaluations`

Merchant category values:

- `GROCERY`
- `RETAIL`
- `TRAVEL`
- `GAMBLING`
- `CRYPTO`
- `MONEY_TRANSFER`
- `OTHER`

Transaction type values:

- `PURCHASE`
- `WITHDRAWAL`
- `TRANSFER`
- `DEPOSIT`
- `PAYMENT`
- `OTHER`

Channel values:

- `CARD_PRESENT`
- `ONLINE`
- `ATM`
- `TRANSFER`
- `MOBILE_APP`
- `OTHER`

Compatibility aliases:

- `POS` -> `CARD_PRESENT`
- `ECOM` -> `ONLINE`
- `CARD_PAYMENT` -> `PAYMENT`
- `CASH_WITHDRAWAL` -> `WITHDRAWAL`
- `MONEYTRANSFER` -> `MONEY_TRANSFER`

Timestamp format:

- `eventTimestamp` must be ISO-8601 with timezone offset, for example `2026-05-12T10:00:00+02:00` or `2026-05-12T08:00:00Z`
- for query filters (`from`, `to`) URL-encode `+` as `%2B`

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

### List evaluation summaries

`GET /api/fraud-evaluations`

Supported filters:

- `decision`
- `accountId`
- `customerId`
- `transactionId`
- `merchantCategory`
- `channel`
- `ruleHit` (repeatable)
- `ruleHitMatch` (`ANY` default, `ALL`)
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

### Governance endpoints

- `GET /api/admin/rules`
- `GET /api/admin/rules/{ruleCode}/versions/{version}`
- `PATCH /api/admin/rules/{ruleCode}/versions/{version}/state`
- `POST /api/admin/rules/{ruleCode}/versions`

Examples:

```bash
curl http://localhost:8080/api/admin/rules
curl http://localhost:8080/api/admin/rules?activeOnly=false
curl http://localhost:8080/api/admin/rules/HIGH_AMOUNT/versions/1.0.0
```

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

Current observability posture includes:

- structured `key=value` service logs
- lightweight request correlation via `X-Request-Id`
- Micrometer evaluation metrics
- security denial counters
- retrieval, governance, and API-error counters
- Actuator exposure limited by active profile

Current signals include:

- structured logs around:
  - fraud evaluation start
  - recent-history lookup
  - fraud evaluation completion
  - retrieval queries
  - request-validation and request-parsing failures
- Micrometer metrics for the evaluation path:
  - `fraud.evaluation.completed.total`
  - `fraud.evaluation.decision.count`
  - `fraud.evaluation.rule.triggered.count`
  - `fraud.evaluation.duration`
- security counters:
  - `fraud.security.authn.denied.total`
  - `fraud.security.authz.denied.total`

Operational runbook baseline:

- `default` profile:
  - actuator defaults: `health,info,metrics`
  - Swagger/OpenAPI enabled
  - no authentication enforced
- `secure` profile:
  - actuator defaults: `health,info` (authenticated)
  - Swagger/OpenAPI disabled by default
  - auth required for API and observability surfaces
- `production` profile:
  - actuator defaults: `health` only
  - health details disabled (`show-details=never`)
  - Swagger/OpenAPI disabled

Incident triage signals:

1. start with the request ID (`X-Request-Id`) from the client/response
2. trace logs by request ID
   - evaluation and retrieval flow logs
   - security denial diagnostics:
     - `security_authn_denied` for `401` paths
     - `security_authz_denied` for `403` paths
3. check counters in `/actuator/metrics` where exposed
4. confirm profile exposure contract before deeper debugging

## Security Posture

Security is profile-aware:

- `default`
  - open local-development posture
  - Swagger/OpenAPI enabled by default
  - actuator defaults: `health,info,metrics`
- `secure`
  - HTTP Basic authentication
  - role-segmented API, governance, and actuator access
- `hardened` / `production`
  - JWT/OIDC resource-server posture with claim-to-role mapping

This is a pragmatic baseline focused on deterministic local development, integration testing, and explainable service behavior.

More detail by profile:

- `default`
  - intentionally open for local development
  - `/api/**`, Swagger/OpenAPI, and exposed actuator endpoints are reachable without auth
  - startup guardrail warning is emitted to make local-only intent explicit
  - should not be used for non-local or internet-exposed deployment
- `secure`
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
  - route authorization model:
    - `PATCH/POST /api/admin/rules/**` requires `GOVERNANCE_ADMIN` or `PLATFORM_ADMIN`
    - `GET /api/admin/rules/**` requires `OPS_READER` or stronger
    - `/actuator/**` requires `OPS_READER` or stronger
    - `/api/fraud-evaluations...` requires `API_CLIENT` or stronger
  - credentials come from env-backed `app.security.secure-profile.*` properties
  - identity provider is configurable:
    - `IN_MEMORY` (default)
    - `JDBC`
  - JDBC identity mode aligns to Spring Security `users` / `authorities` style contracts
  - secure credentials support either:
    - raw password (`password`) for local use
    - pre-encoded password (`password-encoded`) for non-local secret workflows
  - secret source strategy is explicit for `IN_MEMORY` mode:
    - `ENV`
    - `PRE_ENCODED`
    - `EXTERNAL_MANAGER`
  - `/actuator/info` exposes redacted `secureCredentialDiagnostics` for operations without leaking raw secrets
  - external-secret adapter is available through env configuration, including:
    - `FRAUD_ENGINE_SECURE_EXTERNAL_MANAGER_ADAPTER`
    - `FRAUD_ENGINE_SECURE_EXTERNAL_SECRET_REF`
- `hardened` / `production`
  - JWT/OIDC resource-server posture
  - fail-fast startup when issuer, key set, or audience configuration is missing

### Secure Credential Rotation Workflow

1. `PREPARE`
   - primary credential only
   - rotation fields absent
2. `OVERLAP`
   - primary and rotation credentials both active
   - requires `rotation-username` and one rotation secret source
3. `CUTOVER`
   - validates overlap-ready credential state before promotion
4. `RETIRE`
   - primary credential only after cleanup
   - rotation fields absent

Safe sequence: `PREPARE -> OVERLAP -> CUTOVER -> RETIRE`

For operational bootstrap and rollback paths, use:

- `docs/operations/runbooks/secure-credential-rotation-runbook.md`

### Governance Workflow Lifecycle Contract

Governance lifecycle promotion and deprecation semantics are documented in:

- `docs/operations/runbooks/governance-workflow-lifecycle-contract.md`

## Known Simplifications

- rules are code-defined, not database-authored
- no dynamic executable-rule authoring yet
- retrieval remains intentionally bounded
- enterprise IAM integration is still out of scope
- SpringDoc and Actuator exposure are profile-driven

## Future Improvements

- centralize fraud thresholds and history-window configuration
- evolve secure mode beyond in-memory Basic Auth where needed
- expand observability depth
- extend retrieval and audit support
- expand governance automation and policy controls

## Notes

`HELP.md` is the generated Spring starter help file. This `README` is the primary project document.
