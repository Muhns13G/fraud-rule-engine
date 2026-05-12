# Blueprints

This directory holds forward-looking project planning for `fraud-rule-engine`.

## How To Use This Folder
- Treat `01-project-blueprint.md` as the canonical high-level system view.
- Treat `02-development-roadmap.md` as the phased delivery plan.
- When implementation changes the architecture, update the blueprint in the same change set.
- Keep a clear boundary between:
  - verified current state from the codebase
  - proposed future state that is not implemented yet

## Current Blueprint Set
- `01-project-blueprint.md`: current repo baseline, target architecture, core domain slices, and cross-cutting concerns.
- `02-development-roadmap.md`: suggested delivery phases from scaffold to production-ready fraud decisioning service.
- `AI-Sessions/`: scratchpad area for imported or conversational planning notes; do not treat it as canonical unless those notes are promoted into the numbered blueprints.

## Neighboring Planning Docs
- `../RAG/`: compact retrieval-friendly context for future coding sessions.
  - `01-project-overview.md`
  - `02-decisions-log.md`
  - `03-api-scope.md`
  - `04-domain-glossary.md`
  - `05-architecture-current-vs-target.md`
- `../implementation-plans/`: sprint-oriented implementation plans using the `Sprint X.Y.Z` convention.
- `../completion-reports/`: end-of-sprint reports capturing work completed, decisions made, deviations, lessons learned, technical debt, future considerations, and affected files.

## Sprint Close-Out Rule
- Every sprint should end with a completion report in `../completion-reports/`.
- Every sprint close-out should also review whether the numbered RAG docs need updates.
