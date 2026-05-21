# Sprint 6.1 Implementation Plan

## Scope
Reviewer-safe production validation and hardened token-contract completion.

## Sprint Goal
Strengthen hardened/production JWT trust boundaries while preserving secure-profile reviewer usability and adding a repeatable local+hosted validation matrix.

## Debt Policy
- `TD-021` remains `Partially addressed` unless live external IdP rollout is implemented and verified.
- `TD-027` remains `Partially addressed` unless live issuer/audience validation is verified against a real IdP environment.

## Task List

### Sprint 6.1.1
Harden JWT contract validation in `hardened/production`.
- require `issuer-uri`, `jwk-set-uri`, and `audience` at startup
- enforce issuer and audience validators in JWT decoder
- keep enforcement isolated to `hardened | production`

### Sprint 6.1.2
Add negative hardening regression coverage.
- verify startup fail-fast when `issuer-uri` or `audience` is missing
- verify validator rejects invalid issuer and audience claims
- verify role-claim absence denies protected API access

### Sprint 6.1.3
Add reviewer-focused validation harness scripts.
- local validation script (`curl + Maven`) covering auth, evaluation, retrieval, governance authorization, and actuator behavior
- hosted validation script (`curl`) against reviewer URL with deterministic test prefixes and full functional writes

### Sprint 6.1.4
Documentation and governance synchronization.
- update README with Phase 6 validation scripts and hardened contract notes
- update RAG and debt registry with Phase 6 outcomes and evidence
- generate Sprint 6.1 completion report

## Public/API Changes
- No new business endpoints.
- Hardened token acceptance becomes stricter for issuer/audience mismatch.

## Tests
- Targeted Maven suites for hardened/secure security and API matrix.
- Local script-run validation for reviewer flows.
- Hosted script-run validation for production-like reviewer environment.

## Expected Output
- Reviewer path stays stable on `secure`.
- Hardened/production token contract correctness is stronger and explicitly test-backed.
- Validation evidence is reproducible via scripts and completion artifacts.
