## Agents

This project uses small automation agents and CLI helpers to streamline common tasks such as building, testing, and releasing artifacts.

# Agents

This file documents the helper workflows used during development and release of Documentor.

# Purpose

Provide a concise reference for contributors and maintainers about agent-like workflows and CLI steps.

# Release workflow (agents)

These are documented as reproducible CLI steps (the "agent" is a human or script that runs them):

Create a release tag locally:

```bash
git tag -a vX.Y.Z -m "Release vX.Y.Z"
git push origin vX.Y.Z
```

Build and produce artifacts (runs Checkstyle first):

```bash
gradlew.bat clean checkstyleMain checkstyleTest
gradleMaint build
```

Upload artifact(s) to GitHub release and set notes:

```bash
gh release upload vX.Y.Z build\\libs\\documentor.jar --clobber
gh release edit vX.Y.Z --notes-file detailed_release_notes_vX.Y.Z.md --publish
```

# Local CLI helpers

Always run the clean Checkstyle build before finalizing releases to avoid style regressions:

```bash
gradlew.bat clean checkstyleMain checkstyleTest
```

# Release notes and changelog

- `release_notes_vX.Y.Z.md` — short release summary for the GitHub release body.
- `detailed_release_notes_vX.Y.Z.md` — extended notes for internal review before publishing.
- `CHANGELOG.md` — canonical changelog in the repo root.

# Publishing checklist

1. Commit documentation changes (`CHANGELOG.md`, `docs/DOCUMENTATION_SUMMARY.md`, release note files).
2. Run `gradlew.bat clean checkstyleMain checkstyleTest && gradlew.bat build` and confirm `build/libs/documentor.jar` exists.
3. Create and push annotated tag, create or edit GitHub release, upload artifact(s), then publish.

# Contact

See `README.md` for maintainer contact information.

- Provide a brief reference for contributors and maintainers about release and helper workflows.

## Agents and workflows

- Release agent: helper steps that create tags, build artifacts, and upload them to GitHub Releases using the `gh` CLI and the Gradle wrapper (`gradlew.bat`).

Usage summary:

- Create a release tag locally: `git tag -a vX.Y.Z -m "Release vX.Y.Z"`
- Push tag to origin: `git push origin vX.Y.Z`
- Build artifact: `gradlew.bat clean build`
- Upload artifact to release: `gh release upload vX.Y.Z build\\libs\\documentor.jar --clobber`
- Edit release notes or set draft/publish: `gh release edit vX.Y.Z --notes-file detailed_release_notes_vX.Y.Z.md --publish`

## Local CLI helpers

- Always run the clean Checkstyle build before finalizing releases:

```bash
gradlew.bat clean checkstyleMain checkstyleTest
```

This helps prevent style regressions and ensures the project follows defined rules.

## Release notes and changelog

- `release_notes_vX.Y.Z.md` — concise release summary used for GitHub release notes.
- `detailed_release_notes_vX.Y.Z.md` — extended notes for reviewers and drafts.
- `CHANGELOG.md` — canonical changelog kept in the repo root.

## Reviewing and publishing releases

1. Ensure the working tree is clean and documentation changes are committed.
2. Verify build and style: `gradlew.bat clean checkstyleMain checkstyleTest && gradlew.bat build`.
3. Create and push tag, create/edit the release, upload artifacts, then publish the release when ready.

## Contact

For questions about the release flow or helper scripts, see `README.md` or contact the maintainers listed there.
