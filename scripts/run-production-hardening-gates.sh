#!/usr/bin/env bash

set -euo pipefail

echo "Running production-hardening gates..."

./scripts/run-repo-hygiene-checks.sh
./scripts/run-security-ops-regression.sh
./scripts/run-performance-reliability-smoke.sh

./mvnw --batch-mode --no-transfer-progress \
  -Dtest=FraudEvaluationControllerIntegrationTest,RuleGovernanceControllerIntegrationTest \
  test

echo "Production-hardening gates passed."
