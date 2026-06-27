# Sprint 2.4 Completion Report

## Sprint Summary
Sprint `2.4` replaced the single permissive security posture with a profile-aware baseline that keeps local validation usability intact while introducing an explicit secured mode. The sprint locked the security strategy, implemented secure-profile HTTP Basic authentication, aligned Swagger/OpenAPI and actuator behavior with that strategy, added dedicated security integration tests, and updated public-facing and RAG documentation.

## Scope Completed
- `2.4.1` Strategy lock-in and config contract:
  - chose `HTTP Basic` as the next-step auth mechanism
  - chose `default` open + `secure` authenticated profile split
  - locked secured-surface intent (API, Swagger/OpenAPI, actuator)
  - added env-backed secure-profile credential properties in `application.yaml`
- `2.4.2` Profile-aware security behavior:
  - `PhaseOneSecurityConfiguration` now applies only outside `secure` profile
  - added `SecureProfileSecurityConfiguration` for `secure` profile
  - added secure-profile properties binder and in-memory user wiring
- `2.4.3` Swagger/OpenAPI and actuator alignment:
  - secure profile now protects `/api/**`, Swagger/OpenAPI endpoints, and `/actuator/**`
  - secure profile health detail visibility changed to `when_authorized`
- `2.4.4` Security-focused tests:
  - added unauthenticated and authenticated secure-profile integration coverage
  - verified protected-route rejection (`401`) and valid-auth success paths
- `2.4.5` Documentation and deployment notes:
  - added secure-profile run instructions and authenticated request example in `README`
  - updated RAG architecture and overview docs to reflect the new baseline

## Key Decisions Made
- Keep the secured mode simple and architecture discussion-explainable with HTTP Basic instead of introducing OAuth2/JWT complexity.
- Preserve local ergonomics by keeping `default` profile open and making hardening opt-in via `SPRING_PROFILES_ACTIVE=secure`.
- Use env-backed credentials to avoid hardcoded production secrets while keeping local setup straightforward.
- Treat secure-profile endpoint protection and health-detail visibility as explicit policies rather than incidental framework defaults.

## Technical Debt Accrued
- Secure profile still uses in-memory credentials; no external identity provider or persistent user store.
- Credentials are env-configured but not integrated with enterprise secret management or rotation workflows.
- Security tests currently validate secure-profile behavior; default-profile openness assertions remain implicit in existing integration tests.

## Future Considerations
- Introduce profile-specific authorization rules beyond single-role in-memory authentication.
- Add dedicated tests for default-profile openness and secure-profile endpoint matrix completeness (including Swagger docs JSON paths).
- Evaluate migration path from Basic Auth to token-based auth only when project scope requires it.
- Revisit actuator exposure depth in secure mode as operations requirements evolve.

## File Inventory

| File | Status in Sprint 2.4 | Notes |
| --- | --- | --- |
| `src/main/java/com/oitws/fraudengine/infrastructure/security/PhaseOneSecurityConfiguration.java` | Modified | Scoped permissive behavior to non-secure profile |
| `src/main/java/com/oitws/fraudengine/infrastructure/security/SecureProfileSecurityConfiguration.java` | Created | Added secure-profile HTTP Basic filter chain and in-memory user setup |
| `src/main/java/com/oitws/fraudengine/infrastructure/security/SecureProfileSecurityProperties.java` | Created | Added secure-profile credential property binding |
| `src/main/resources/application.yaml` | Modified | Added secure-profile credential contract and health-detail override |
| `src/test/java/com/oitws/fraudengine/api/controller/SecureProfileSecurityIntegrationTest.java` | Created | Added secure-profile authentication/authorization integration tests |
| `docs/implementation-plans/phase-02/sprint-2.4.md` | Modified | Locked strategy decisions and task intent |
| `README.md` | Modified | Added secure-profile run instructions and updated security posture |
| `docs/RAG/01-project-overview.md` | Modified | Added profile-aware security baseline to current posture |
| `docs/RAG/02-decisions-log.md` | Modified | Recorded Sprint 2.4.1 security decisions |
| `docs/RAG/05-architecture-current-vs-target.md` | Modified | Updated current/target security architecture notes |

## Verification Summary
- `./mvnw test` passed after Sprint `2.4` changes.
- Test results: `44` tests run, `0` failures, `0` errors, `0` skipped.
- Secure-profile integration tests confirm:
  - unauthenticated access to protected routes returns `401`
  - authenticated requests with valid credentials succeed as expected
  - invalid credentials are rejected

## Close-Out
Sprint `2.4` is complete. The project now has a clear and test-backed security baseline that balances local validation usability with explicit secured-mode behavior, and the updated documentation reflects how to run and reason about both modes.
