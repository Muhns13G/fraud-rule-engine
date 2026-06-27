# Domain Glossary

## Transaction Event
A categorized financial transaction submitted for fraud evaluation. This is the primary input to the service.

## Fraud Evaluation
The aggregate result of processing one transaction event through the active fraud rules. It includes the final decision and the rule hit trail.

## Fraud Decision
The top-level business outcome returned by the service:
- `ALLOW`: no material suspicious signals
- `REVIEW`: suspicious but not conclusively fraudulent
- `BLOCK`: clearly unacceptable risk condition

## Fraud Rule
A deterministic rule that evaluates one aspect of a transaction event and contributes a result plus supporting reason.

## Rule Evaluation Result
The outcome of one rule execution. It should capture whether the rule hit, its reason, and any score or severity contribution.

## Decision Trace
A machine-readable explanation of how the final fraud decision was reached from the individual rule results.

## Velocity
A signal based on too many recent transactions in a short time window for the same account or customer.

## Risky Merchant Category
A heuristic signal based on merchant categories that are more fraud-prone or unusual for the evaluation context.

## Unusual Time
A heuristic signal based on a transaction occurring during a suspicious local time window.
