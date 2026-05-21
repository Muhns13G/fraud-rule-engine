#!/usr/bin/env bash

set -euo pipefail

echo "Running Phase 6 reviewer-safe validation pack..."
./scripts/run-phase6-reviewer-validation-local.sh

if [[ -n "${SECURE_USER:-}" && -n "${SECURE_PASSWORD:-}" ]]; then
  ./scripts/run-phase6-reviewer-validation-hosted.sh
else
  echo "SECURE_USER / SECURE_PASSWORD not set; skipping hosted validation pass."
fi

echo "Phase 6 reviewer-safe validation pack completed."
