---
name: release-readiness
description: Audit and prepare a Boxes Android release candidate. Use for explicit release-readiness, Play publishing, versioning, signed bundle, R8, privacy, or store-delivery tasks; do not activate for ordinary debug builds.
---

# Release Readiness

Treat this as a verification workflow, not authorization to publish, upload, rotate credentials, or modify Play Console state. Obtain explicit user direction before any external release action.

## Release candidate checks

- Confirm the intended `versionCode`, `versionName`, application ID, target SDK, and release notes scope.
- Confirm production signing is supplied outside the repository. The checked-in signing values are dummy credentials for local release-build testing only and must not be presented as production signing.
- Run formatting, unit tests, lint, and the release build. Inspect R8 or resource-shrinking warnings relevant to runtime behavior.
- Build the artifact from a known source revision and report its path, variant, version, and whether it is signed with dummy or production credentials. Do not commit generated APKs, bundles, or signing files.
- Smoke-test launch, navigation, project creation/editing, save/reload, import/export, sharing, thumbnails, and in-app update behavior as applicable to the change set.
- Verify database migrations from supported released versions and exported-project compatibility when either format changed.
- Review the merged manifest for exported components, FileProvider configuration, backup/data-extraction behavior, permissions, and SDK-added declarations.
- Compare analytics, network, storage, identifiers, and third-party SDK behavior with `PRIVACY_POLICY.md` and Play Data safety disclosures. Flag mismatches; do not invent declarations.
- Check adaptive layouts, accessibility basics, launcher assets, and representative supported API levels when UI or resources changed.

## Handoff

Report completed checks, failures, skipped checks with reasons, unresolved release blockers, artifact identity, signing type, and any manual Play Console steps. A successful local build alone is not a release-readiness pass.
