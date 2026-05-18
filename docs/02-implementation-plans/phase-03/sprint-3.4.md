# Sprint 3.4 Implementation Plan

## Scope
This sprint closes Phase 3 by adding repeatable delivery gates, test-runtime hygiene, and final governance regression/documentation alignment.

## Sprint Goal
Make Phase 3 outcomes verifiable and reproducible through CI and end-to-end regression confidence.

## Task List

### Sprint 3.4.1
Add baseline CI pipeline.
- implement CI workflow for compile, test, and package across PR/main flows
- include Docker-backed integration-test strategy or equivalent service-container approach
- debt merged:
  - `TD-016`

### Sprint 3.4.2
Resolve Mockito/JDK 25 dynamic-agent warning path.
- align test runtime configuration with current Mockito/JDK guidance
- debt merged:
  - `TD-009`

### Sprint 3.4.3
Add final governance regression suite.
- verify version registration, lifecycle transitions, authorization rules, and retrieval behavior after mutation flows

### Sprint 3.4.4
Complete Phase 3 documentation close-out.
- update blueprint, RAG, README, and technical debt statuses for completed Phase 3 outcomes
- produce sprint completion report and phase summary artifact

## Public/API Changes
- no major new API surface is expected in this sprint
- focus remains reliability, delivery-gating, and close-out consistency

## Tests
- CI green for compile + tests + package
- full governance regression suite passing locally and in CI
- final debt status reconciliation based on Phase 3 outcomes

## Expected Output
- repeatable CI gate exists for core quality checks
- test runtime noise is reduced and better aligned with Java 25
- Phase 3 closes with regression confidence and documentation consistency

## Notes
- keep CI scope pragmatic for current repository shape; avoid premature platform complexity
- closure quality should include documentation and debt-status alignment, not code only
