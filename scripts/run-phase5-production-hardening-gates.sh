#!/usr/bin/env bash

set -euo pipefail

echo "Running Phase 5 production-hardening gates..."

./scripts/run-repo-hygiene-checks.sh
./scripts/run-phase4-security-ops-regression.sh
./scripts/run-performance-reliability-smoke.sh

./mvnw --batch-mode --no-transfer-progress \
  -Dtest=FraudEvaluationControllerIntegrationTest,RuleGovernanceControllerIntegrationTest \
  test

echo "Phase 5 production-hardening gates passed."
