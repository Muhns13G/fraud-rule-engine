# API Scope

## Phase 1 Endpoints

### `POST /api/fraud-evaluations`
Accept one categorized transaction event, evaluate it against the active rule set, persist the result, and return the final decision with traceable reasons.

### `GET /api/fraud-evaluations/{evaluationId}`
Return one persisted fraud evaluation including the original normalized transaction fields, final decision, and per-rule results.

### `GET /api/fraud-evaluations`
Return filtered fraud evaluations for review workflows.

## Phase 1 Query Filters
- `decision`
- `accountId`
- `from`
- `to`

## Not In Phase 1 By Default
- bulk ingestion
- asynchronous processing
- admin rule-management endpoints
- customer-facing APIs
- advanced search filters such as `customerId`, `transactionId`, `merchantCategory`, or rule-hit lookup
