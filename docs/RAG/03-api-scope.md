# API Scope

## Phase 1 Endpoints

### `POST /api/fraud-evaluations`
Accept one categorized transaction event, evaluate it against the active rule set, persist the result, and return the final decision with traceable reasons.

Expected request fields:
- `transactionId`
- `accountId`
- `customerId`
- `amount`
- `currency`
- `merchantId`
- `merchantCategory`
- `transactionType`
- `channel`
- `eventTimestamp`
- optional `location`
- optional `reference`

Expected response fields:
- `evaluationId`
- `transactionId`
- `decision`
- `decisionScore`
- `evaluatedAt`
- `traceSummary`
- `ruleResults`

### `GET /api/fraud-evaluations/{evaluationId}`
Return one persisted fraud evaluation including the original normalized transaction fields, final decision, and per-rule results.

### `GET /api/fraud-evaluations`
Return paged and filtered fraud evaluations for review workflows.

## Phase 2.5 Admin Rule Governance Endpoints

### `GET /api/admin/rules`
Return governed rule metadata for admin visibility. Defaults to active rules with optional `activeOnly=false` for full visibility.

### `GET /api/admin/rules/{ruleCode}/versions/{version}`
Return one governed rule metadata entry by identity (`ruleCode + version`).

## Phase 3.1 Admin Rule Governance Mutation Endpoints

### `PATCH /api/admin/rules/{ruleCode}/versions/{version}/state`
Apply constrained lifecycle/activation state transitions for governed rule metadata.

### `POST /api/admin/rules/{ruleCode}/versions`
Register a new governed metadata version for an existing rule code while preserving code-defined runtime execution boundaries.

## Phase 1 Query Filters
- `decision`
- `accountId`
- `customerId`
- `transactionId`
- `merchantCategory`
- `channel`
- `from`
- `to`
- `sort`
- `page`
- `size`

Supported sort values:
- `NEWEST_FIRST`
- `OLDEST_FIRST`

## Not In Phase 1 By Default
- bulk ingestion
- asynchronous processing
- rule activation or mutation endpoints outside governed admin controls
- customer-facing APIs
- broader search filters such as rule-hit lookup

## Rule Evaluation Surface Notes (Sprint 3.3)
- The rule set now includes `LOCATION_ANOMALY` in addition to:
  - `HIGH_AMOUNT`
  - `VELOCITY`
  - `RISKY_MERCHANT_CATEGORY`
  - `UNUSUAL_TIME`
- `ruleResults` remains the explainability contract surface for per-rule evidence.

## Security Exposure Notes (Sprint 3.2)
- `default` profile keeps API, Swagger/OpenAPI, and configured actuator endpoints open for local review workflows.
- `secure` profile requires authentication for API and actuator routes, and requires admin role for governance mutation endpoints.
- Swagger/OpenAPI and actuator exposure are now explicitly profile-driven through configuration.

## Secure Identity Contract Notes (Sprint 4.2)
- No public endpoint contract changed in Sprint 4.2.
- `secure` profile identity and secret posture is now stricter and explicitly validated:
  - in-memory secret-source modes: `ENV`, `PRE_ENCODED`, `EXTERNAL_MANAGER` seam
  - JDBC identity query contract validation with safe defaults
  - optional credential-rotation overlap contract for controlled cutover

## Authorization Matrix Notes (Sprint 4.1)
- `secure` profile route authorization is role-segmented:
  - core fraud-evaluation API (`/api/fraud-evaluations...`): `API_CLIENT` or stronger
  - governance reads (`GET /api/admin/rules...`): `OPS_READER` or stronger
  - governance mutations (`PATCH/POST /api/admin/rules...`): `GOVERNANCE_ADMIN` or `PLATFORM_ADMIN`
  - actuator routes (`/actuator/**`): `OPS_READER` or stronger
  - docs routes (`/swagger-ui/**`, `/v3/api-docs/**` when enabled): `OPS_READER` or stronger
- `default` profile remains intentionally open with startup guardrail messaging.

## Governance Regression Notes (Sprint 3.4)
- Governance mutation and retrieval behavior is now regression-tested as a closure gate.
- Secure-profile authorization boundaries are explicitly asserted for:
  - admin user access to governance mutation routes
  - non-admin user rejection for governance mutation routes
