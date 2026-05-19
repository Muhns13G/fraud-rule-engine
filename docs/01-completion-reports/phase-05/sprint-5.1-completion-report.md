# Sprint 5.1 Completion Report

## Sprint Summary
Sprint `5.1` introduced a hardened token-auth posture for non-local profiles, preserved reviewer-friendly secure hosting guidance, and closed the profile matrix verification/documentation path.

## Scope Completed
- `5.1.1` Non-local identity strategy contract:
  - defined hardened-profile token contract properties in configuration
  - aligned API metadata wording with post-Phase-4 scope
- `5.1.2` Hardened-profile authentication entrypoint:
  - added hardened/production security filter chain using OAuth2 resource-server JWT support
  - fail-fast startup behavior when hardened JWT `jwk-set-uri` is not configured
- `5.1.3` Token claim to role mapping:
  - mapped hardened JWT claims to application roles used by route authorization policy
  - enforced route/method role boundaries for governance mutation, governance reads, actuator routes, and core API routes
- `5.1.4` Profile safety controls:
  - retained explicit default-profile open posture with hosted-runtime guardrail
  - hardened and secure profile behavior now clearly separated in runtime policy and docs
- `5.1.5` Security matrix tests + docs:
  - completed profile matrix integration coverage (`default`, `secure`, `hardened`, `production`)
  - updated README with reviewer-friendly secure-hosting mode and authenticated actuator usage guidance

## Key Decisions Made
- Keep reviewer-hosted deployments on `secure` profile when external IdP/JWT wiring is unavailable.
- Require hardened/production profile JWT infrastructure inputs explicitly, with fail-fast behavior.
- Keep default profile open only for local/reviewer workflows and reject hosted runtime usage under default.

## Technical Debt Impact
- `TD-003` moved from `Open` to `Partially addressed` (hosted-runtime default-profile guardrail now enforced).
- `TD-021` moved from `Open` to `Partially addressed` (hardened JWT/OIDC runtime path implemented).
- `TD-025` closed (OpenAPI metadata now reflects current expanded scope).
- New carry-forward debt added:
  - `TD-027`: hardened JWT decoder currently requires `jwk-set-uri` but does not yet enforce `issuer`/`audience` validation.

## File Inventory

| File | Status in Sprint 5.1 | Notes |
| --- | --- | --- |
| `src/main/java/com/capitec/fraudengine/infrastructure/security/HardenedProfileSecurityConfiguration.java` | Created/Modified | Hardened/production JWT resource-server enforcement and route authorization |
| `src/main/java/com/capitec/fraudengine/infrastructure/security/HardenedProfileSecurityProperties.java` | Created/Modified | Hardened profile JWT/OIDC contract keys |
| `src/main/resources/application.yaml` | Modified | Hardened-profile identity properties and env mapping |
| `src/test/java/com/capitec/fraudengine/infrastructure/security/HardenedProfileSecurityConfigurationTest.java` | Created/Modified | Hardened security configuration unit coverage |
| `src/test/java/com/capitec/fraudengine/api/controller/HardenedProfileSecurityIntegrationTest.java` | Created/Modified | Hardened route authorization behavior |
| `src/test/java/com/capitec/fraudengine/api/controller/ProductionProfileObservabilityIntegrationTest.java` | Modified | Production profile observability behavior with hardened contract inputs |
| `src/test/java/com/capitec/fraudengine/api/controller/DefaultProfileSecurityIntegrationTest.java` | Modified | Default-profile guardrail and open/local posture checks |
| `src/test/java/com/capitec/fraudengine/api/controller/SecureProfileSecurityIntegrationTest.java` | Modified | Secure profile matrix compatibility checks |
| `src/main/java/com/capitec/fraudengine/infrastructure/config/OpenApiConfiguration.java` | Modified | Post-Phase-4 accurate metadata description |
| `README.md` | Modified | Reviewer-friendly secure hosting mode + profile matrix + authenticated actuator examples |

## Verification Summary
- Security profile matrix tests passed:
  - `./mvnw -Dtest=DefaultProfileSecurityIntegrationTest,SecureProfileSecurityIntegrationTest,HardenedProfileSecurityIntegrationTest,ProductionProfileObservabilityIntegrationTest test`
  - Result: `BUILD SUCCESS` with `Tests run: 21, Failures: 0, Errors: 0, Skipped: 0`
- Hosted reviewer path validated with secure profile:
  - Railway startup confirmed with `SPRING_PROFILES_ACTIVE=secure`
  - authenticated `/actuator/health` and `/actuator/info` access verified
  - unauthenticated actuator requests correctly return `401`

## Close-Out
Sprint `5.1` is complete. Hardened JWT profile infrastructure is now implemented and test-backed, reviewer-hosted secure-mode guidance is documented, and debt status has been reconciled with explicit carry-forward transparency.
