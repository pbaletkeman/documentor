# Project PRD: Prioritized Feature Work

This document collects the prioritized features and requirements produced during the documentation reorganization and project analysis sessions. It is formatted for quick delegation via issues and pull requests.

## Overview

- **Repository:** documentor
- **Owner:** pbaletkeman
- **Primary goal:** Improve configuration handling, diagram file naming, and developer ergonomics; provide clear automation for task delegation (issue → branch → pull request).

## Prioritized Features

1. Schema validation for incoming configuration files

   - **Why:** Ensure user-provided JSON/YAML configs are validated before use.
   - **Acceptance criteria:** Validator CLI/library, unit tests, sample config in `samples/`, docs updated under `docs/CONFIGURATION.md`.

2. Filename sanitizer & diagram naming strategy

   - **Why:** Avoid invalid chars and collisions when generating diagram files (Mermaid/PlantUML). Support configurable strategy (slug, timestamp, increment).
   - **Acceptance criteria:** Utility class + integration test, `samples/config-diagram-naming-example.json`, docs explaining options and migration path.

3. Atomic file writer with collision handling

   - **Why:** Prevent partial writes and race conditions when multiple workers write diagrams or docs.
   - **Acceptance criteria:** Implementation for atomic write + tests; configurable collision policy (overwrite, skip, suffix); documentation and examples.

4. LLM integration test harness and mock provider

   - **Why:** Provide reliable tests for features that depend on LLM outputs (avoid external API calls in unit tests).
   - **Acceptance criteria:** Mock LLM provider, integration tests for `LlmService`, docs showing how to run tests offline.

5. CI matrix and GitHub Actions improvements

   - **Why:** Ensure cross-platform builds and test runs; add workflow to convert labeled issues into branches + PRs (already scaffolded).
   - **Acceptance criteria:** Build matrix for Java 17/21, Windows/Linux, unit/integration modes; automated PR creation workflow validated on remote.

6. Dockerfile and containerized dev image

   - **Why:** Make it easy for contributors to run the tool consistently.
   - **Acceptance criteria:** Multi-stage Dockerfile, README section with `docker build` and `docker run` examples.

7. Logging and metrics integration

   - **Why:** Provide observability for long-running converts and batch runs.
   - **Acceptance criteria:** Add structured logging, basic metrics counters (diagrams generated, errors), and a local debug profile.

8. Release automation and changelog generation

   - **Why:** Streamline releases and changelog generation from PRs.
   - **Acceptance criteria:** GitHub Action to draft releases, link to PRs, and create semver tags when a release label is applied.

9. Schema-driven configuration docs and examples

   - **Why:** Keep `docs/CONFIGURATION.md` in sync with schema and provide examples for each option.
   - **Acceptance criteria:** JSON Schema file, examples in `samples/`, docs regenerated or checked in CI.

10. Backwards-compatibility migration guide

    - **Why:** Help users upgrade from older config formats to the new schema and naming approaches.
    - **Acceptance criteria:** `docs/MIGRATION.md` with examples and automated migration scripts where feasible.

11. End-to-end tests for diagram generation

    - **Why:** Validate full pipeline: read config → generate diagrams → write files.
    - **Acceptance criteria:** E2E test harness and sample artifacts checked into `test-output/`.

12. Improve CLI UX and help text

    - **Why:** Make the command-line interface easier to use and discoverable.
    - **Acceptance criteria:** Updated CLI help, `--config` validation, examples in `docs/GETTING_STARTED.md`.

13. Security review and secrets handling for LLM keys

    - **Why:** Ensure secret management and not leaking keys in logs or artifacts.
    - **Acceptance criteria:** Documentation for secret setup, verify GitHub Actions avoid printing secrets, use repository secrets for CI.

14. Optional: Slack/email notifications for workflow events
    - **Why:** Notify stakeholders when a delegated PR is created or merges.
    - **Acceptance criteria:** Workflow hooks with optional `SLACK_WEBHOOK` or SMTP secrets; opt-in via repository secrets.

## Implementation Notes

- Each prioritized item should have: owner, estimate, acceptance criteria, tests, and docs pointer.
- Use small, focused PRs (one feature per PR) with the provided `PULL_REQUEST_TEMPLATE.md`.
- Prefer standard libraries and lightweight dependencies.

## Next Actions

1. Create issues for top 3 features and apply the `start-work` label to trigger automation.
2. Add repository secrets for optional notifications and test the workflow on a sample issue.
3. Assign owners and produce implementation plans for items 1–3.

## Appendix: Related Files Created Earlier

- `.github/ISSUE_TEMPLATE/task.md` — Task issue template
- `.github/workflows/auto-create-pr-from-task.yml` — Workflow to create branch & PR on label
- `.github/PULL_REQUEST_TEMPLATE.md` — PR template and checklist
- `samples/config-diagram-naming-example.json` — Sample configuration (moved from repo root)
- `docs/CONFIGURATION.md` — Updated references and naming guidance
