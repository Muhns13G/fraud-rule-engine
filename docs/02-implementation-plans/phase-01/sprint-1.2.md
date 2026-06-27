# Sprint 1.2 Implementation Plan

## Scope
Phase 1, Sprint 2 locks the exact Phase 1 contract and starter heuristics so coding can begin without hidden assumptions.

## Sprint Goal
Turn the settled blueprint into a code-ready specification for DTOs, thresholds, and persistence entry points.

## Task List

### Sprint 1.2.1
Move schema migration into the active Phase 1 slice.
- Add Flyway to the implementation scope now, not later.
- Treat the first migration as part of the first vertical slice.

### Sprint 1.2.2
Lock the first request DTO contract.
- Required fields:
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
- Optional fields:
  - `location.countryCode`
  - `location.city`
  - `reference`

### Sprint 1.2.3
Lock the first response DTO contract.
- Response must include:
  - `evaluationId`
  - `transactionId`
  - `decision`
  - `decisionScore`
  - `evaluatedAt`
  - `traceSummary`
  - `ruleResults`
- Each rule result must include:
  - `ruleCode`
  - `ruleName`
  - `triggered`
  - `severity`
  - `scoreContribution`
  - `reason`

### Sprint 1.2.4
Lock the first rule thresholds.
- Amount review threshold: `>= 10000.00 ZAR`
- Amount block threshold: `>= 25000.00 ZAR`
- Velocity threshold: `>= 3` transactions for the same `accountId` within `5 minutes`
- Risky merchant categories:
  - `GAMBLING`
  - `CRYPTO`
  - `MONEY_TRANSFER`
- Unusual time window:
  - `00:00` to `04:00`

### Sprint 1.2.5
Lock the initial aggregation model.
- Keep outward decision model:
  - `ALLOW`
  - `REVIEW`
  - `BLOCK`
- Use simple internal scoring:
  - review-level signal: `40`
  - block-level signal: `100`
- Decision policy:
  - `ALLOW` when no rules trigger
  - `REVIEW` when score is `1` to `99`
  - `BLOCK` when a blocking rule triggers or total score is `>= 100`

## Expected Output
- Phase 1 planning is now specific enough to implement DTOs, rules, entities, and tests directly.
- No further open-ended planning should be required before coding starts.
