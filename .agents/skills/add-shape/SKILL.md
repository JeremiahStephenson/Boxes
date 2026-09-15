---
name: add-shape
description: Add or change a drawable BitShape across rendering, persistence, UI discovery, and compatibility. Use when modifying the Shape enum or implementing shape geometry; do not use for ordinary canvas interactions or styling.
---

# Add Shape

Treat a shape identifier as persisted data. Existing `Shape` enum names may appear in Room rows and exported project files, so do not rename, reorder for semantic effect, or remove them without an explicit migration plan.

## Workflow

1. Inspect `ui/shapes/Shape.kt`, `ShapeGroup.kt`, and the geometry file for the relevant group before editing. Follow the established pairing between each enum entry and its drawing functions.
2. Implement equivalent geometry for both rendering paths:
   - Compose `DrawScope`, used by the editor.
   - Android `Canvas`, used by bitmap, thumbnail, and export rendering.
3. Add the enum entry to the correct `ShapeGroup`. Confirm that existing group-driven UI makes it discoverable; update UI code only if discovery is not automatic.
4. Keep rendering inside the supplied cell bounds and apply `ColorAndShape` consistently with comparable shapes. Avoid allocations in drawing hot paths when a reusable calculation is sufficient.
5. Check database and project-transfer behavior. New enum values must round-trip through `Shape.name` and `Shape.valueOf`; changing an existing identifier requires using the project-format migration workflow.
6. Add focused geometry or rendering tests where observable behavior can be asserted. At minimum, test project export/import round-tripping for the new value and exercise both renderers.
7. Run formatting, relevant tests, and a debug build. Visually inspect the shape at small and large cell sizes when automated assertions cannot establish rendering quality.

Do not combine adding a shape with broad refactoring of the large shape catalog unless the task requests that refactor.
