#!/usr/bin/env bash

set -euo pipefail

echo "Running performance and reliability smoke checks..."

./mvnw --batch-mode --no-transfer-progress \
  -Dtest=PerformanceReliabilitySmokeIntegrationTest,VelocityFraudRuleTest \
  test

echo "Performance and reliability smoke checks passed."
