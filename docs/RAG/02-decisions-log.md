# Decisions Log

## Confirmed Decisions
- Build tool stays Maven.
- Runtime baseline stays Spring Boot `4.0.6` with Java `25`.
- PostgreSQL is the primary persistence store for local development and tests.
- The top-level API outcome model is `ALLOW`, `REVIEW`, `BLOCK`.
- Phase 1 uses a simple internal numeric score in addition to the outward business decision.
- The first rule set is code-defined and deterministic.
- Phase 1 rules are:
  - high amount
  - velocity
  - risky merchant category
  - unusual time
- Starter thresholds are:
  - amount review at `>= 10000.00 ZAR`
  - amount block at `>= 25000.00 ZAR`
  - velocity at `>= 3` transactions in `5 minutes` for the same `accountId`
  - unusual time between `00:00` and `04:00`
  - risky merchant categories start with `GAMBLING`, `CRYPTO`, `MONEY_TRANSFER`
- `location anomaly` is deferred unless the first vertical slice is already stable.
- Local development will not implement real auth for Phase 1; this choice must be stated clearly in the README.
- The current review retrieval surface supports:
  - `decision`
  - `accountId`
  - `customerId`
  - `transactionId`
  - time range
  - explicit summary sorting with `NEWEST_FIRST` and `OLDEST_FIRST`
- Sprint 2.4.1 locks the security strategy to:
  - `HTTP Basic` as the next-step authentication mechanism
  - profile split: `default` open and `secure` authenticated
  - secured-mode protection target: API + Swagger/OpenAPI + exposed actuator endpoints
  - env-backed secure-profile credentials with local defaults
- OAuth2/JWT and external identity provider integration remain intentionally deferred.

## Still Flexible
- Exact enum values and naming for transaction type, channel, and merchant category
- Exact response projection used by the list endpoint versus single-item retrieval
