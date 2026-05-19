# Sprint 5.1 Implementation Plan

## Scope
Production-hardening identity posture for non-local environments while preserving reviewer usability.

## Sprint Goal
Introduce enterprise-ready authentication posture for hardened profiles and reduce profile-misuse risk.

## Debt Merged
- `TD-003`
- `TD-021`
- `TD-025`

## Task List

### Sprint 5.1.1
Define non-local identity strategy contract.
- lock token-based strategy for hardened profiles (JWT/OAuth2/OIDC integration path)
- document compatibility with current secure profile behavior
- align OpenAPI/service metadata wording with post-Phase-4 scope (remove stale "Phase 1 API" framing)

Implementation contract for `5.1.1`:
- hardened auth mechanism: `JWT_OIDC` (non-local hardened profile target)
- profile posture:
  - `default`: open (reviewer-local only)
  - `secure`: HTTP Basic (current hardened local mode)
  - `hardened`: token-based auth contract (JWT/OIDC), enforcement starts in `5.1.2`
- token contract keys:
  - `issuer-uri`
  - `jwk-set-uri`
  - `audience`
  - `principal-claim`
  - `roles-claim`
  - `clock-skew-seconds`
- compatibility note:
  - existing secure-profile route matrix remains source of truth until hardened filter-chain enforcement is introduced.

### Sprint 5.1.2
Implement hardened-profile authentication entrypoint.
- add token-validation path for hardened profile(s)
- keep `default` profile reviewer-open with explicit guardrails

### Sprint 5.1.3
Map token claims to application roles.
- enforce role mapping for:
  - `API_CLIENT`
  - `OPS_READER`
  - `GOVERNANCE_ADMIN`
  - `PLATFORM_ADMIN`

### Sprint 5.1.4
Add profile safety controls.
- fail fast when non-local profile uses open defaults
- ensure docs/actuator policy remains profile-consistent

### Sprint 5.1.5
Add security matrix tests and docs.
- test profile matrix: `default` vs `secure` vs hardened non-local
- document migration and local testing path

## Public/API Changes
- Authentication mechanism for hardened profile changes from basic-only posture to token-aware posture.
- No business endpoint shape changes expected.

## Tests
- Hardened profile authn/authz integration tests.
- Claim-to-role mapping tests for governance routes.
- Negative tests for profile misconfiguration.

## Expected Output
- Non-local security posture is enterprise-aligned.
- Reviewer-local ergonomics remain intact and explicit.
