# Decisions Log

## Confirmed Decisions
- Build tool stays Maven.
- Runtime baseline stays Spring Boot `4.0.6` with Java `25`.
- PostgreSQL is the primary persistence store for local development and tests.
- The top-level API outcome model is `ALLOW`, `REVIEW`, `BLOCK`.
- The first rule set is code-defined and deterministic.
- Phase 1 rules are:
  - high amount
  - velocity
  - risky merchant category
  - unusual time
- `location anomaly` is deferred unless the first vertical slice is already stable.
- Local development will not implement real auth for Phase 1; this choice must be stated clearly in the README.
- Phase 1 retrieval filters are limited to:
  - `decision`
  - `accountId`
  - time range

## Still Flexible
- Exact numeric thresholds for the first rules
- Exact request and response payload schema details
- Whether scoring is numeric, categorical, or both internally
