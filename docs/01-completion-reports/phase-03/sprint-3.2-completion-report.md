# Sprint 3.2 Completion Report

## Sprint Summary
Sprint `3.2` matured profile-based security from baseline authentication into a clearer authorization and exposure model for governance operations. The sprint added role-aware governance mutation protection, introduced a configurable secure identity-provider strategy, added explicit profile-matrix security tests, and formalized profile-driven Swagger/OpenAPI and actuator exposure policy.

## Scope Completed
- `3.2.1` Role-aware governance authorization:
  - secure profile now requires admin role for governance mutation endpoints
  - default profile remains intentionally open for local/reviewer usability
- `3.2.2` Secure credential and identity strategy upgrade:
  - secure identity provider is now configurable (`IN_MEMORY` default, optional `JDBC`)
  - secure credentials now support raw or pre-encoded password source
  - optional JDBC query overrides added for non-default identity schemas
- `3.2.3` Explicit profile behavior tests:
  - added dedicated default-profile open-access integration coverage
  - added secure-profile admin/non-admin mutation authorization coverage
  - strengthened secure-profile read-path assertions
- `3.2.4` Security noise and exposure policy hardening:
  - removed exclusion-based auto-config workaround and moved to explicit profile bean ownership
  - made Swagger/OpenAPI exposure explicitly profile-driven
  - made actuator exposure explicitly profile-driven
  - documented exposure defaults and override strategy in README

## Key Decisions Made
- Preserve reviewer ergonomics in `default` profile while making `secure` profile operationally stricter.
- Protect governance mutations by role, not just authentication presence.
- Introduce an incremental identity strategy (`IN_MEMORY` -> `JDBC`) instead of forcing enterprise IAM scope into this sprint.
- Use explicit profile configuration for endpoint exposure policy rather than relying on implicit defaults.

## Technical Debt Impact
- `TD-004` Partially addressed: secure identity is no longer in-memory-only by contract (`JDBC` path added).
- `TD-005` Partially addressed: secure secret handling supports encoded credentials and clearer non-local secret-source posture.
- `TD-006` Closed: default-profile openness behavior is now explicitly tested.
- `TD-007` Partially addressed: generated-password confusion path reduced via explicit profile bean ownership.
- `TD-008` Closed: Swagger/OpenAPI exposure policy is now explicitly profile-driven.

## File Inventory

| File | Status in Sprint 3.2 | Notes |
| --- | --- | --- |
| `src/main/java/com/capitec/fraudengine/infrastructure/security/SecureProfileSecurityConfiguration.java` | Modified | Role-aware governance mutation authorization + identity-provider strategy wiring |
| `src/main/java/com/capitec/fraudengine/infrastructure/security/SecureProfileSecurityProperties.java` | Modified | Added admin role and identity/secret strategy properties |
| `src/main/java/com/capitec/fraudengine/infrastructure/security/PhaseOneSecurityConfiguration.java` | Modified | Explicit default-profile security bean ownership and actuator matcher alignment |
| `src/main/resources/application.yaml` | Modified | Profile-driven Swagger/OpenAPI and actuator exposure policy + secure identity contract |
| `src/test/java/com/capitec/fraudengine/api/controller/DefaultProfileSecurityIntegrationTest.java` | Created/Modified | Explicit default-profile open-behavior tests |
| `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileSecurityIntegrationTest.java` | Modified | Secure profile read + auth rejection matrix coverage |
| `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileGovernanceAdminIntegrationTest.java` | Created | Secure profile admin-role mutation authorization coverage |
| `README.md` | Modified | Updated security and profile-exposure guidance |
| `docs/RAG/01-project-overview.md` | Modified | Updated current-state security posture summary |
| `docs/RAG/02-decisions-log.md` | Modified | Recorded Sprint 3.2 security decisions |
| `docs/RAG/03-api-scope.md` | Modified | Added profile-exposure/security notes |
| `docs/RAG/05-architecture-current-vs-target.md` | Modified | Updated security architecture current-state notes |
| `docs/03-technical-debt/technical-debt-registry-v1.md` | Modified | Updated TD status and resolution notes for Sprint 3.2 outcomes |
| `docs/01-completion-reports/phase-03/sprint-3.2-completion-report.md` | Created | Sprint close-out report |

## Verification Summary
- `./mvnw -Dtest=DefaultProfileSecurityIntegrationTest,SecureProfileSecurityIntegrationTest,SecureProfileGovernanceAdminIntegrationTest test` passed.
- Test results: `14` tests run, `0` failures, `0` errors, `0` skipped.
- `./mvnw test` passed.
- Full-suite results: `75` tests run, `0` failures, `0` errors, `0` skipped.

## Close-Out
Sprint `3.2` is complete. Security behavior is now more explicit, test-backed, and profile-governed, while preserving the local reviewer-friendly default posture and introducing a clearer path toward stronger non-local identity and secret management.
