#!/usr/bin/env bash

set -euo pipefail

ALLOW_DIRTY_WORKTREE="${1:-}"

echo "Running repository hygiene checks..."

echo "Checking tracked .DS_Store artifacts..."
tracked_ds_store="$(git ls-files | rg '\.DS_Store$' || true)"
if [[ -n "${tracked_ds_store}" ]]; then
  echo "Tracked .DS_Store files detected:"
  echo "${tracked_ds_store}"
  exit 1
fi

echo "Checking untracked .DS_Store artifacts..."
untracked_ds_store="$(find . -type f -name '.DS_Store' -not -path './.git/*' | sed 's#^\./##' || true)"
if [[ -n "${untracked_ds_store}" ]]; then
  echo "Untracked .DS_Store files detected:"
  echo "${untracked_ds_store}"
  exit 1
fi

if [[ "${ALLOW_DIRTY_WORKTREE}" != "--allow-dirty-worktree" ]]; then
  echo "Checking git workspace cleanliness..."
  git_status="$(git status --porcelain --untracked-files=normal)"
  if [[ -n "${git_status}" ]]; then
    echo "Workspace is not clean:"
    echo "${git_status}"
    echo "Use --allow-dirty-worktree only for local exploratory runs."
    exit 1
  fi
else
  echo "Skipping workspace-cleanliness assertion (--allow-dirty-worktree)."
fi

echo "Repository hygiene checks passed."
