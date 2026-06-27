# Sprint 4.1 Completion Report

## Sprint Summary
Sprint `4.1` established the Phase 4 access-control baseline by formalizing a secure-profile role model, enforcing route-level role segmentation, adding default-profile guardrails, and documenting the final matrix.

## Scope Completed
- `4.1.1` Role contract and planning baseline:
  - added secure-profile role properties for API, ops-read, governance-admin, and optional platform-admin scopes
  - generated Phase 4 sprint plans (`4.1` to `4.4`)
- `4.1.2` Authorization enforcement model:
  - updated secure-profile security configuration to support multiple role groupings per route surface
- `4.1.3` Default-profile guardrails:
  - added startup warning/guardrail signaling that `default` remains intentionally open for local workflows
- `4.1.4` Access-matrix verification:
  - expanded integration coverage for secure/default profile behavior across core API, governance, and actuator surfaces
- `4.1.5` Documentation close-out:
  - documented final role-access matrix and explicit deferred IAM boundary

## Key Decisions Made
- Keep `default` profile intentionally open for validation ergonomics.
- Use `secure` profile as the hardened route-policy baseline.
- Segment authorization by surface and role:
  - `API_CLIENT` for core evaluation API
  - `OPS_READER` for governance-read and actuator diagnostics
  - `GOVERNANCE_ADMIN` for governance mutation
  - `PLATFORM_ADMIN` as optional superset role

## Technical Debt Impact
- Directly advanced debt items associated with security posture maturity and profile-aware authorization boundaries.
- Remaining enterprise IAM/JWT/OAuth2 integration was explicitly deferred as out of Sprint 4.1 scope.

## File Inventory

| File | Status in Sprint 4.1 | Notes |
| --- | --- | --- |
| `src/main/java/com/oitws/fraudengine/infrastructure/security/SecureProfileSecurityConfiguration.java` | Modified | Role-segmented secure-profile authorization |
| `src/main/java/com/oitws/fraudengine/infrastructure/security/SecureProfileSecurityProperties.java` | Modified | Role property contract |
| `src/main/java/com/oitws/fraudengine/infrastructure/security/DefaultProfileSecurityGuardrails.java` | Created/Modified | Default-profile openness guardrail |
| `src/main/resources/application.yaml` | Modified | Role/property alignment |
| `src/test/java/com/oitws/fraudengine/api/controller/SecureProfileSecurityIntegrationTest.java` | Modified | Secure-profile matrix assertions |
| `src/test/java/com/oitws/fraudengine/api/controller/SecureProfileGovernanceAuthorizationIntegrationTest.java` | Modified | Governance authz assertions |
| `src/test/java/com/oitws/fraudengine/api/controller/SecureProfileGovernanceAdminIntegrationTest.java` | Modified | Admin-path assertions |
| `src/test/java/com/oitws/fraudengine/api/controller/SecureProfilePlatformAdminIntegrationTest.java` | Modified | Platform-admin assertions |
| `docs/02-implementation-plans/phase-04/sprint-4.1.md` | Modified | Sprint plan closure alignment |
| `docs/00-blueprints/02-development-roadmap.md` | Modified | Phase posture updates |
| `docs/RAG/01-project-overview.md` | Modified | Current-state security posture updates |
| `docs/RAG/02-decisions-log.md` | Modified | Sprint decisions captured |
| `docs/RAG/03-api-scope.md` | Modified | Authorization matrix notes |
| `docs/RAG/05-architecture-current-vs-target.md` | Modified | Security architecture updates |
| `README.md` | Modified | Public-facing secure/default behavior notes |

## Verification Summary
- Secure/default profile behavior was validated through expanded integration matrix tests introduced in this sprint.
- Route-level authorization boundaries for governance and platform-admin flows were explicitly asserted in dedicated security integration tests.

## Close-Out
Sprint `4.1` is complete. The project now has an explicit, documented, and test-backed secure-profile access-control baseline for Phase 4.
