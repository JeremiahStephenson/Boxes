---
name: evolve-project-format
description: Safely change the exported BitShape JSON format or its import/export behavior while preserving user files. Use for edits to ProjectTransfer, serialized fields, format versions, validation, or compatibility mappings.
---

# Evolve Project Format

Exported project files are user documents. Start by inspecting `repository/ProjectTransfer.kt`, the import and export paths in `BoxesRepository.kt`, and `ProjectTransferTest.kt`.

## Compatibility decision

Before changing code, identify whether the change is:

- Compatible: old files still deserialize with identical meaning and newly written files remain valid for the current version.
- Migratable: old versions require an explicit conversion before mapping to current models.
- Breaking: old readers cannot interpret new output or current code intentionally drops support.

Do not silently make a breaking change. For a migratable or breaking change, increment the format version deliberately, define supported source versions, and keep version-specific conversion separate from current model mapping.

## Implementation requirements

- Keep the stable format marker and file extension unless the product requirement explicitly replaces them.
- Do not serialize enum names casually. `Shape` names are persistent identifiers; use an explicit wire-name mapping if source-code naming must diverge.
- Apply defaults only when absence has an unambiguous backward-compatible meaning.
- Validate before writing imported data. Retain bounds for file size, canvas dimensions, layer count, names, coordinates, duplicate pixels, and known shapes.
- Reject malformed input without leaving partial database state. Keep project, layer, and pixel writes transactional.
- Avoid accepting unknown fields or values in a way that changes the drawing silently.

## Verification

Add fixture or constructed-payload tests covering the oldest supported version, the current version, a current export/import round trip, malformed input, boundary values, and any new compatibility mapping. Confirm that rejected imports create no partial project. Run the repository tests plus the standard project checks relevant to the change.

Document the compatibility outcome in the change summary. If support for an old version is intentionally removed, state that explicitly.
