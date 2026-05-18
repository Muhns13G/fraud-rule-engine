# Sprint 1.4 Completion Report

## Sprint Summary
Sprint `1.4` delivered the first working end-to-end fraud evaluation vertical slice for the Capitec take-home. The service can now accept categorized transaction events, evaluate fraud rules, aggregate a final decision, persist the evaluation, retrieve stored results, and expose the Phase 1 API endpoints.

## Scope Completed
- Implemented real rule logic for:
  - high amount
  - velocity
  - risky merchant category
  - unusual time
- Implemented aggregation policy for:
  - `ALLOW`
  - `REVIEW`
  - `BLOCK`
  - `decisionScore`
  - `traceSummary`
- Implemented application services for:
  - evaluation orchestration
  - retrieval mapping
- Implemented persistence-backed:
  - recent transaction history lookup
  - evaluation persistence
  - filtered evaluation retrieval
- Implemented REST controller endpoints:
  - `POST /api/fraud-evaluations`
  - `GET /api/fraud-evaluations/{evaluationId}`
  - `GET /api/fraud-evaluations`
- Implemented request validation and centralized API error responses.
- Replaced default Spring Security behavior with intentional Phase 1 permissive local/test security.

## Key Decisions Made
- Keep Phase 1 security permissive for local and reviewer usability.
- Preserve the production-grade architecture boundary between DTOs, domain records, persistence entities, and mappers.
- Use repository-backed recent transaction lookup for velocity history rather than in-memory approximation.
- Keep Phase 1 retrieval filters limited to `decision`, `accountId`, and time range.
- Preserve the dual model of:
  - explicit outward decision
  - internal numeric score

## Deviations From Original Plan
- A small bug fix was folded into `1.4.5`: `FraudEvaluationService` had a stale early return path from pre-persistence wiring, which was removed while the controller slice was added.
- Security was implemented as one permissive configuration class rather than profile-split local/non-local security.
- Retrieval filtering was implemented with repository method combinations rather than a more abstract specification or query-builder approach.
- The `1.4.6` security configuration is implemented and verified in the working tree, but was not yet committed at the moment this completion report was generated.

## Lessons Learned
- The clean boundaries established in Sprint `1.3` paid off once the vertical slice was wired together.
- History-based rules benefit from an explicit rule context plus persisted transaction snapshots.
- Keeping temporary security intentional is better than leaving Spring defaults unexplained.
- Building the end-to-end flow exposed how quickly “small” orchestration details accumulate once persistence, mapping, and API concerns meet.

## Technical Debt Accrued
- The velocity history window is duplicated between rule assumptions and service-side lookup behavior.
- Enum parsing from request strings currently relies on `valueOf` normalization and may need friendlier conversion or validation later.
- Retrieval queries may need consolidation or refinement if filter combinations grow.
- No automated tests exist yet for:
  - rule behavior
  - aggregation policy
  - API flow
  - persistence flow
- `README` and `Dockerfile` are still pending.

## Future Considerations
- A stronger but more time-expensive choice would have been a lightweight API key or basic auth setup.
- Another stronger but more time-expensive choice would have been profile-specific security with local open and non-local locked down.
- Centralize fraud thresholds and history-window configuration so rules and orchestration do not duplicate operational constants.
- Improve request-to-enum conversion ergonomics and error messaging.
- Add OpenAPI verification, tests, `Dockerfile`, and `README` in Sprint `1.5`.

## File Inventory

| File | Status in Sprint 1.4 | Notes |
| --- | --- | --- |
| `src/main/java/com/capitec/fraudengine/domain/rule/impl/HighAmountFraudRule.java` | Modified | Implemented high amount rule behavior |
| `src/main/java/com/capitec/fraudengine/domain/rule/impl/VelocityFraudRule.java` | Modified | Implemented velocity rule behavior |
| `src/main/java/com/capitec/fraudengine/domain/rule/impl/RiskyMerchantCategoryFraudRule.java` | Modified | Implemented risky merchant category rule behavior |
| `src/main/java/com/capitec/fraudengine/domain/rule/impl/UnusualTimeFraudRule.java` | Modified | Implemented unusual time rule behavior |
| `src/main/java/com/capitec/fraudengine/domain/policy/package-info.java` | Created | Policy package marker |
| `src/main/java/com/capitec/fraudengine/domain/policy/FraudDecisionPolicy.java` | Created | Aggregation policy for final decisions |
| `src/main/java/com/capitec/fraudengine/domain/policy/FraudDecisionPolicyResult.java` | Created | Aggregated decision result record |
| `src/main/java/com/capitec/fraudengine/application/mapper/package-info.java` | Created | Application mapper package marker |
| `src/main/java/com/capitec/fraudengine/application/mapper/FraudEvaluationApplicationMapper.java` | Created | DTO/domain mapping for evaluation flows |
| `src/main/java/com/capitec/fraudengine/application/service/package-info.java` | Created | Application service package marker |
| `src/main/java/com/capitec/fraudengine/application/service/FraudEvaluationService.java` | Created, then modified | Orchestrates rule execution, aggregation, history lookup, and persistence |
| `src/main/java/com/capitec/fraudengine/application/service/FraudEvaluationRetrievalService.java` | Created | Retrieves and maps stored evaluations |
| `src/main/java/com/capitec/fraudengine/infrastructure/persistence/repository/FraudEvaluationJpaRepository.java` | Modified | Added retrieval and history query methods |
| `src/main/java/com/capitec/fraudengine/infrastructure/persistence/repository/FraudRuleResultJpaRepository.java` | Modified | Retained rule-result lookup support |
| `src/main/java/com/capitec/fraudengine/infrastructure/persistence/mapper/FraudEvaluationPersistenceMapper.java` | Modified | Continued to support domain/persistence translation in the wired flow |
| `src/main/java/com/capitec/fraudengine/api/controller/package-info.java` | Created | Controller package marker |
| `src/main/java/com/capitec/fraudengine/api/controller/FraudEvaluationController.java` | Created | Phase 1 fraud evaluation REST endpoints |
| `src/main/java/com/capitec/fraudengine/api/error/package-info.java` | Created | API error package marker |
| `src/main/java/com/capitec/fraudengine/api/error/ApiErrorResponse.java` | Created | Consistent API error payload |
| `src/main/java/com/capitec/fraudengine/api/error/FraudEvaluationNotFoundException.java` | Created | Missing-resource exception |
| `src/main/java/com/capitec/fraudengine/api/error/GlobalExceptionHandler.java` | Created | Centralized API exception handling |
| `src/main/java/com/capitec/fraudengine/infrastructure/security/PhaseOneSecurityConfiguration.java` | Created | Intentional permissive Phase 1 security configuration |

## RAG Update Summary
- No core Phase 1 scope assumptions changed during Sprint `1.4`.
- Existing RAG docs remain directionally accurate.
- A future RAG refresh could explicitly note that:
  - the Phase 1 endpoints are now implemented
  - the security posture is intentionally permissive for local and reviewer use

## Verification Summary
- `./mvnw compile` passed at sprint close-out.
- No automated tests were run yet for:
  - rule behavior
  - persistence flow
  - API flow

## Close-Out
Sprint `1.4` is complete. The first working fraud evaluation vertical slice now exists, and Sprint `1.5` remains focused on hardening:
- unit tests
- integration and API tests
- OpenAPI polish
- `Dockerfile`
- `README`
- cleanup and review
