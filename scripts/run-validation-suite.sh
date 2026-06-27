#!/usr/bin/env bash

set -euo pipefail

echo "Running validation suite..."
./scripts/run-local-validation.sh

if [[ -n "${SECURE_USER:-}" && -n "${SECURE_PASSWORD:-}" ]]; then
  ./scripts/run-hosted-validation.sh
else
  echo "SECURE_USER / SECURE_PASSWORD not set; skipping hosted validation pass."
fi

echo "Validation suite completed."
