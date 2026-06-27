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

## Supporting Reference Docs
- `../RAG/`: compact architecture, API, and decision references that mirror the current codebase.
- `../operations/`: environment templates, Postman assets, and runbooks for local and hosted validation.
