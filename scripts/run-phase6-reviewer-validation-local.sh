#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
SECURE_USER="${SECURE_USER:-secure-user}"
SECURE_PASSWORD="${SECURE_PASSWORD:-change-me-secure}"
EXPECTED_GOVERNANCE_READ_STATUS="${EXPECTED_GOVERNANCE_READ_STATUS:-403}"
EXPECTED_GOVERNANCE_MUTATION_STATUS="${EXPECTED_GOVERNANCE_MUTATION_STATUS:-403}"

PREFIX="phase6-local-$(date +%Y%m%d%H%M%S)-${RANDOM}"
TXN_HIGH="${PREFIX}-high"
TXN_LOW="${PREFIX}-low"
ACCOUNT_HIGH="${PREFIX}-account-high"
ACCOUNT_LOW="${PREFIX}-account-low"
CUSTOMER_HIGH="${PREFIX}-customer-high"
CUSTOMER_LOW="${PREFIX}-customer-low"

log() {
  printf '[phase6-local] %s\n' "$1"
}

expect_status() {
  local description="$1"
  local expected="$2"
  shift 2

  local actual
  actual=$(curl -sS -o /tmp/phase6_local_response.json -w "%{http_code}" "$@")

  if [[ "$actual" != "$expected" ]]; then
    log "FAILED: ${description} (expected ${expected}, got ${actual})"
    cat /tmp/phase6_local_response.json
    exit 1
  fi
  log "OK: ${description} -> ${actual}"
}

log "Running local Maven verification subset..."
./mvnw --batch-mode --no-transfer-progress \
  -Dtest=HardenedProfileSecurityConfigurationTest,HardenedProfileSecurityIntegrationTest,SecureProfileSecurityIntegrationTest,SecureProfileGovernanceAuthorizationIntegrationTest,FraudEvaluationControllerIntegrationTest,RuleGovernanceControllerIntegrationTest \
  test

log "Running local curl matrix against ${BASE_URL}..."

expect_status "secure actuator info requires auth and succeeds with valid basic credentials" "200" \
  -u "${SECURE_USER}:${SECURE_PASSWORD}" \
  "${BASE_URL}/actuator/info"

expect_status "secure actuator metrics is intentionally not exposed" "404" \
  -u "${SECURE_USER}:${SECURE_PASSWORD}" \
  "${BASE_URL}/actuator/metrics"

expect_status "api rejects unauthenticated access" "401" \
  "${BASE_URL}/api/fraud-evaluations"

expect_status "api rejects wrong credentials" "401" \
  -u "${SECURE_USER}:wrong-password" \
  "${BASE_URL}/api/fraud-evaluations"

expect_status "create high-risk evaluation" "201" \
  -u "${SECURE_USER}:${SECURE_PASSWORD}" \
  -H "Content-Type: application/json" \
  -d "{
    \"transactionId\": \"${TXN_HIGH}\",
    \"accountId\": \"${ACCOUNT_HIGH}\",
    \"customerId\": \"${CUSTOMER_HIGH}\",
    \"amount\": 26000.00,
    \"currency\": \"ZAR\",
    \"merchantId\": \"merchant-${PREFIX}\",
    \"merchantCategory\": \"RETAIL\",
    \"transactionType\": \"PURCHASE\",
    \"channel\": \"ONLINE\",
    \"eventTimestamp\": \"2026-05-21T10:00:00+02:00\",
    \"location\": {\"countryCode\": \"ZA\", \"city\": \"Cape Town\"},
    \"reference\": \"${PREFIX}-high\"
  }" \
  "${BASE_URL}/api/fraud-evaluations"

expect_status "create low-risk evaluation" "201" \
  -u "${SECURE_USER}:${SECURE_PASSWORD}" \
  -H "Content-Type: application/json" \
  -d "{
    \"transactionId\": \"${TXN_LOW}\",
    \"accountId\": \"${ACCOUNT_LOW}\",
    \"customerId\": \"${CUSTOMER_LOW}\",
    \"amount\": 120.00,
    \"currency\": \"ZAR\",
    \"merchantId\": \"merchant-${PREFIX}\",
    \"merchantCategory\": \"RETAIL\",
    \"transactionType\": \"PURCHASE\",
    \"channel\": \"ONLINE\",
    \"eventTimestamp\": \"2026-05-21T10:02:00+02:00\",
    \"location\": {\"countryCode\": \"ZA\", \"city\": \"Cape Town\"},
    \"reference\": \"${PREFIX}-low\"
  }" \
  "${BASE_URL}/api/fraud-evaluations"

expect_status "retrieve evaluation summaries by transaction id" "200" \
  -u "${SECURE_USER}:${SECURE_PASSWORD}" \
  "${BASE_URL}/api/fraud-evaluations?transactionId=${TXN_HIGH}"

expect_status "rule-hit ANY retrieval" "200" \
  -u "${SECURE_USER}:${SECURE_PASSWORD}" \
  "${BASE_URL}/api/fraud-evaluations?ruleHit=HIGH_AMOUNT&ruleHitMatch=ANY&sort=NEWEST_FIRST&page=0&size=20"

expect_status "rule-hit ALL retrieval" "200" \
  -u "${SECURE_USER}:${SECURE_PASSWORD}" \
  "${BASE_URL}/api/fraud-evaluations?ruleHit=HIGH_AMOUNT&ruleHit=UNUSUAL_TIME&ruleHitMatch=ALL&sort=NEWEST_FIRST&page=0&size=20"

expect_status "governance read authorization matches expected secure role matrix" "${EXPECTED_GOVERNANCE_READ_STATUS}" \
  -u "${SECURE_USER}:${SECURE_PASSWORD}" \
  "${BASE_URL}/api/admin/rules"

expect_status "governance mutation authorization matches expected secure role matrix" "${EXPECTED_GOVERNANCE_MUTATION_STATUS}" \
  -u "${SECURE_USER}:${SECURE_PASSWORD}" \
  -H "Content-Type: application/json" \
  -X PATCH \
  -d '{"lifecycleStatus":"ACTIVE","activationState":"ACTIVE"}' \
  "${BASE_URL}/api/admin/rules/HIGH_AMOUNT/versions/1.0.0/state"

log "Phase 6 local reviewer validation passed."
