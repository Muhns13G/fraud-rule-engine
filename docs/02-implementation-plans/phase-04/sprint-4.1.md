# Sprint 4.1 Implementation Plan

## Scope
Start Phase 4 by hardening authentication and authorization boundaries for operations-facing surfaces while preserving explicit local usability modes.

## Sprint Goal
Move from profile-based baseline security to policy-explicit operational access controls for admin/governance and ops endpoints.

## Task List

### Sprint 4.1.1
Define access-policy matrix for all exposed surfaces.
- lock access rules for:
  - fraud-evaluation API
  - governance read/mutation API
  - actuator endpoints
  - Swagger/OpenAPI routes
- codify least-privilege role model (`API_CLIENT`, `OPS_READER`, `GOVERNANCE_ADMIN`, optional `PLATFORM_ADMIN`)

#### Locked Access-Policy Matrix (4.1.1 Contract)

| Surface | `default` profile | `secure` profile (target contract) | Notes |
| --- | --- | --- | --- |
| `GET/POST /api/fraud-evaluations...` | Open | `API_CLIENT` or stronger | Core evaluation/review API surface |
| `GET /api/admin/rules...` | Open | `OPS_READER` or stronger | Governance visibility/read operations |
| `PATCH/POST /api/admin/rules...` | Open | `GOVERNANCE_ADMIN` or stronger | Governance mutation operations |
| `/actuator/health`, `/actuator/info`, `/actuator/metrics` | Open (current hosted mode) | `OPS_READER` or stronger | Ops diagnostics surface |
| `/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**` | Open (current hosted mode) | Default disabled; when enabled require auth | Documentation surface remains profile-driven |

#### Locked Role Model (4.1.1 Contract)
- `API_CLIENT`:
  - baseline authenticated API caller for fraud-evaluation operations
- `OPS_READER`:
  - read-only operations role for governance visibility and actuator inspection
- `GOVERNANCE_ADMIN`:
  - mutation role for governed rule lifecycle/version changes
- `PLATFORM_ADMIN` (optional):
  - future super-role for broader platform-level operations

#### Contract Boundary for 4.1.1
- This task defines and encodes policy contract only.
- Enforcement refactor is intentionally deferred to `4.1.2`.

### Sprint 4.1.2
Implement fine-grained secure-profile authorization rules.
- apply role-based matchers by route group and method
- enforce stricter separation between governance mutation and read-only operations
- keep `default` profile open by design, but ensure secure profile is production-defensible
- debt merged:
  - `TD-003`
  - `TD-012`
  - `TD-014`

### Sprint 4.1.3
Harden default-profile safeguards without breaking validation ergonomics.
- add explicit guardrails and warnings for non-local usage in default mode
- formalize startup logs and README guidance for intended profile usage
- debt merged:
  - `TD-007`

### Sprint 4.1.4
Expand security integration matrix tests.
- verify full role matrix for secure profile (authorized/forbidden paths)
- verify default-profile behavior remains intentional and documented

### Sprint 4.1.5
Document final access matrix and security boundary decisions.
- update RAG, blueprint roadmap alignment notes, and README security sections
- include explicit “why deferred” notes for enterprise IAM integrations

## Public/API Changes
- no endpoint shape changes expected
- authorization behavior in `secure` profile becomes more granular by role and surface

## Tests
- profile matrix integration tests for default vs secure
- role matrix assertions across governance, evaluation, and ops routes

## Expected Output
- clear, enforceable access-policy model
- stronger operational security posture with auditable role boundaries

## Notes
- do not introduce JWT/OAuth2 in this sprint unless scope is formally changed
- prefer explicit policy clarity over broad framework complexity
