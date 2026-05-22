# Sprint 6.2 Completion Report

## Sprint Summary
Sprint `6.2` focused on reviewer ergonomics by improving request-parse feedback, adding compatibility normalization aliases, and shipping a ready-to-import Postman verification pack for both local and hosted reviewer paths.

## Scope Completed
- `6.2.1` Parse-feedback hardening:
  - added request-body parse handling for malformed payloads
  - ensured invalid/no-offset timestamp payloads return structured `400` responses with actionable guidance
- `6.2.2` Compatibility alias normalization:
  - added API-layer alias normalization for common inbound values:
    - `POS` -> `CARD_PRESENT`
    - `ECOM` -> `ONLINE`
    - `CARD_PAYMENT` -> `PAYMENT`
    - `CASH_WITHDRAWAL` -> `WITHDRAWAL`
    - `MONEYTRANSFER` -> `MONEY_TRANSFER`
  - retained canonical domain enum values and persistence model
- `6.2.3` Regression coverage:
  - added integration tests for alias acceptance
  - added integration tests for invalid timestamp parse behavior
  - aligned production-profile observability test setup with hardened property requirements
- `6.2.4` Reviewer docs + Postman pack:
  - added Postman collection with actuator and fraud-evaluation positive/negative checks
  - added separate local and hosted reviewer Postman environments
  - updated README with reviewer quick start, Postman quick start, enum/alias matrix, and timestamp/query-encoding guidance

## Verification Summary
- Targeted integration coverage:
  - `src/test/java/com/capitec/fraudengine/api/controller/FraudEvaluationControllerIntegrationTest.java`
  - alias acceptance and timestamp parse regression paths
- Compile sanity:
  - `./mvnw -q -DskipTests compile`
- Postman artifact validation:
  - JSON syntax validated for collection and environment files

## Debt Impact
- No new debt items introduced.
- `TD-021` and `TD-027` remain unchanged (`Partially addressed`) because Sprint 6.2 did not perform live external IdP rollout.

## File Inventory
| File | Status | Notes |
| --- | --- | --- |
| `src/main/java/com/capitec/fraudengine/api/error/GlobalExceptionHandler.java` | Modified | `HttpMessageNotReadableException` handling and improved parse feedback |
| `src/main/java/com/capitec/fraudengine/application/mapper/FraudEvaluationApplicationMapper.java` | Modified | Compatibility alias normalization for inbound enum-like values |
| `src/test/java/com/capitec/fraudengine/api/controller/FraudEvaluationControllerIntegrationTest.java` | Modified | Alias + invalid timestamp integration coverage |
| `src/test/java/com/capitec/fraudengine/api/controller/ProductionProfileObservabilityIntegrationTest.java` | Modified | Hardened property contract alignment |
| `docs/operations/postman/fraud-rule-engine-reviewer.postman_collection.json` | Added | Reviewer verification pack with positive/negative checks |
| `docs/operations/postman/fraud-rule-engine-reviewer.postman_environment.json` | Added | Hosted reviewer environment |
| `docs/operations/postman/fraud-rule-engine-local.postman_environment.json` | Added | Local environment |
| `README.md` | Modified | Reviewer quickstart, Postman instructions, enum/alias/timestamp guidance |

## Close-Out
Sprint `6.2` is complete. Reviewer onboarding is now faster and less error-prone, with clearer parse-error behavior, payload compatibility normalization, and turnkey Postman flows for hosted and local validation.
