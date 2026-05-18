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
- rule activation or mutation endpoints (read visibility only currently)
- customer-facing APIs
- broader search filters such as rule-hit lookup
