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

## Security/Observability Policy Notes (Sprint 4.3)
- Profile policy baseline is now explicit:
  - `default`: actuator `health,info,metrics`, Swagger/OpenAPI enabled
  - `secure`: actuator `health,info` (authenticated), Swagger/OpenAPI disabled by default
  - `production`: actuator `health` only, health details hidden, Swagger/OpenAPI disabled
- Request-correlation handling is hardened:
  - incoming `X-Request-Id` values must match UUID format and length constraints
  - invalid/missing values are replaced with generated safe correlation IDs

## Hardened Identity Policy Notes (Sprint 5.1)
- `hardened` and `production` profiles now use JWT token authentication for protected routes.
- Hardened JWT configuration contract keys:
  - `issuer-uri`
  - `jwk-set-uri`
  - `audience`
  - `principal-claim`
  - `roles-claim`
  - `clock-skew-seconds`
- Startup fails fast for hardened/production when JWT `jwk-set-uri` is missing.
- Reviewer-hosted mode remains `secure` profile when external IdP wiring is unavailable; actuator endpoints still require authentication.

## Secure Secret and Rotation Notes (Sprint 5.2)
- No business API endpoint contract changed in Sprint 5.2.
- Secure operational contract now includes:
  - explicit secret-source modes (`ENV`, `PRE_ENCODED`, `EXTERNAL_MANAGER`)
  - explicit credential rotation phases (`PREPARE`, `OVERLAP`, `CUTOVER`, `RETIRE`)
  - legacy fallback mode represented as `LEGACY_OVERLAP` in diagnostics when `rotation-enabled=true` and no explicit phase is set
  - redacted secure credential posture diagnostics in `/actuator/info` for authorized secure roles

## Governance Workflow Contract Notes (Sprint 5.3.1)
- No new endpoint was added in `5.3.1`; this step defines contract semantics only.
- Governance lifecycle contract is now explicitly documented in:
  - `docs/operations/runbooks/governance-workflow-lifecycle-contract.md`
- Contract-level semantic actions (`PROMOTE`, `DEPRECATE`, `REACTIVATE`, `RETIRE`) are mapped to currently allowed lifecycle transitions and existing lifecycle/activation invariants.

## Phase 4 Regression Notes (Sprint 4.4)
- No new public API surface was added in Sprint `4.4`.
- Operational confidence is now gated by a dedicated Phase 4 security/ops regression suite:
  - access-control matrix checks
  - secure identity-provider mode checks
  - observability/actuator policy behavior checks

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
