# Secure Credential Rotation Runbook

## Scope
Secure profile (`SPRING_PROFILES_ACTIVE=secure`) credential bootstrap, phase-driven rotation, and rollback for the in-memory identity path.

This runbook is redaction-first:
- never store raw credentials in version control
- share runtime credentials only over private channels
- treat overlap windows as short-lived operational transitions

## Prerequisites
- Runtime profile: `secure`
- Role contract configured (`API_CLIENT`, `OPS_READER`, `GOVERNANCE_ADMIN`, optional `PLATFORM_ADMIN`)
- Secure environment contract from:
  - `docs/operations/env/secure.env.template`

## Bootstrap
1. Copy the template and create your secure runtime env file.
2. Set base secure values:
   - `FRAUD_ENGINE_SECURE_IDENTITY_PROVIDER=IN_MEMORY`
   - `FRAUD_ENGINE_SECURE_SECRET_SOURCE=ENV` (or `PRE_ENCODED` / `EXTERNAL_MANAGER`)
   - primary credential (`FRAUD_ENGINE_SECURE_USER` plus password source)
3. Set `FRAUD_ENGINE_SECURE_ROTATION_PHASE=PREPARE`.
4. Keep rotation fields unset in `PREPARE`.
5. Start app and verify:
   - `curl -u <user>:<pass> http://localhost:8080/actuator/health`
   - `curl -u <ops-or-admin>:<pass> http://localhost:8080/actuator/info`
   - confirm `secureCredentialDiagnostics.rotationPhase=PREPARE`

## Rotation Procedure
Safe sequence: `PREPARE -> OVERLAP -> CUTOVER -> RETIRE`

### Phase 1: PREPARE
- Primary credential active.
- Rotation fields absent.
- Use to confirm baseline before introducing new credentials.

### Phase 2: OVERLAP
1. Set:
   - `FRAUD_ENGINE_SECURE_ROTATION_PHASE=OVERLAP`
   - `FRAUD_ENGINE_SECURE_ROTATION_USER`
   - exactly one of:
     - `FRAUD_ENGINE_SECURE_ROTATION_PASSWORD`
     - `FRAUD_ENGINE_SECURE_ROTATION_PASSWORD_ENCODED`
2. Ensure rotation username differs from primary username.
3. Restart and verify both credentials authenticate.
4. Move clients/integrations from primary credential to rotation credential.

### Phase 3: CUTOVER
1. Keep primary + rotation fields configured.
2. Set `FRAUD_ENGINE_SECURE_ROTATION_PHASE=CUTOVER`.
3. Restart and verify both credentials still authenticate.
4. Promote rotated value to primary fields (`FRAUD_ENGINE_SECURE_USER` + password source).

### Phase 4: RETIRE
1. Remove all rotation fields:
   - `FRAUD_ENGINE_SECURE_ROTATION_USER`
   - `FRAUD_ENGINE_SECURE_ROTATION_PASSWORD`
   - `FRAUD_ENGINE_SECURE_ROTATION_PASSWORD_ENCODED`
2. Set `FRAUD_ENGINE_SECURE_ROTATION_PHASE=RETIRE`.
3. Restart and verify only primary credential authenticates.

## External Secret Source Bootstrap (Optional)
For `EXTERNAL_MANAGER` with env adapter:
- `FRAUD_ENGINE_SECURE_SECRET_SOURCE=EXTERNAL_MANAGER`
- `FRAUD_ENGINE_SECURE_EXTERNAL_MANAGER_ADAPTER=ENV`
- `FRAUD_ENGINE_SECURE_EXTERNAL_SECRET_REF=env:FRAUD_ENGINE_SECURE_SECRET_PAYLOAD`
- `FRAUD_ENGINE_SECURE_SECRET_PAYLOAD` must include:
  - `username`
  - exactly one of `password` or `passwordEncoded`

Do not set local password fields when `EXTERNAL_MANAGER` is active.

## Rollback
If authentication failures start after a rotation step:
1. Revert to last known-good phase and credential set.
2. Restart service.
3. Validate `/actuator/info` diagnostics reflect reverted phase.
4. Validate API auth with known-good credential.
5. Investigate cause before retrying.

Recommended rollback target by failed phase:
- failed `OVERLAP`: rollback to `PREPARE` with primary only
- failed `CUTOVER`: rollback to `OVERLAP` with previous known-good pair
- failed `RETIRE`: restore `CUTOVER` or `OVERLAP` temporarily, then retire again once verified

## Failure Signals and Fast Checks
- Startup fails fast on invalid combinations (missing required fields, forbidden combinations, same usernames).
- `/actuator/info` contains redacted `secureCredentialDiagnostics` for phase/mode visibility.
- 401/403 spikes after change usually indicate:
  - wrong credential in clients
  - role mismatch
  - phase mismatch vs configured fields

## Legacy Mode Note
Legacy fallback still exists:
- if `rotation-phase` is omitted and `rotation-enabled=true`, runtime defaults to `OVERLAP`

This path is for backward compatibility only; prefer explicit phase configuration.
