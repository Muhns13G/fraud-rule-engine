# Sprint 3.2 Implementation Plan

## Scope
This sprint matures profile-based security for governance operations while preserving local reviewer ergonomics.

## Sprint Goal
Harden authorization and security policy clarity for governance workflows without disrupting default local usability.

## Task List

### Sprint 3.2.1
Introduce role-aware authorization for admin governance operations.
- keep `default` profile intentionally open for local/reviewer use
- require admin role in `secure` profile for governance mutations
- debt merged:
  - `TD-003`
  - `TD-004`

### Sprint 3.2.2
Upgrade secure-profile credential and secret strategy.
- move beyond in-memory-only assumptions toward externalized/persistent-friendly identity strategy
- define secret-source expectations for non-local environments
- debt merged:
  - `TD-005`

### Sprint 3.2.3
Add explicit security behavior tests for profile matrices.
- prove default-open behavior deliberately
- prove secure-profile role protection for governance reads/mutations
- debt merged:
  - `TD-006`

### Sprint 3.2.4
Resolve security/log noise and profile exposure policy gaps.
- address generated-password warning confusion path
- make Swagger/OpenAPI and actuator exposure explicitly profile-aware
- debt merged:
  - `TD-007`
  - `TD-008`

## Public/API Changes
- secure-profile authorization behavior for admin governance endpoints is enforced
- profile-specific documentation and actuator exposure policy is formalized

## Tests
- profile matrix integration tests (`default` vs `secure`)
- endpoint authorization tests for governance read/mutation paths
- exposure tests for Swagger/OpenAPI and actuator behavior by profile

## Expected Output
- governance operations have clearer role-based protection in secure mode
- profile-driven behavior is explicitly tested and documented
- security posture is more production-defensible while preserving local reviewer flow

## Notes
- keep local reviewer friendliness in `default` profile intentional and documented
- avoid over-expanding to enterprise IAM integration in this sprint unless required by scope changes
