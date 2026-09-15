---
name: room-schema-migration
description: Evolve the Boxes Room database without losing released user data. Use when changing Room entities, columns, indices, foreign keys, converters, or persisted DAO semantics; do not use for query-only changes that leave the schema intact.
---

# Room Schema Migration

Inspect `cache/BoxesDatabase.kt`, every affected entity and DAO query, and database construction in `inject/CacheModule.kt`. Treat existing projects, layers, pixels, and undo history as user data.

## Workflow

1. Determine the actual schema delta and whether it changes existing values, constraints, indices, or relationships.
2. Enable and retain Room schema export when introducing migration support. Store exported schemas in a stable test-visible project directory rather than generated build output.
3. Increase the database version exactly once for the complete schema change.
4. Implement an explicit migration from every released version that the app supports. Preserve identifiers and relationships; populate new non-null columns deterministically.
5. Register migrations in the Room database builder. Do not add destructive fallback to the production database.
6. Update DAO queries and transaction boundaries to match the new schema. Review foreign-key actions and indices rather than relying only on successful compilation.
7. Add Room migration tests using an old schema/database fixture. Insert representative data before migration and assert schema validity, values, ordering, foreign keys, and relevant queries afterward.

When a schema change also changes exported project files or `Shape` identifiers, use the project-format compatibility workflow as part of the same task.

Run migration tests, affected repository tests, lint, and a debug build. State which starting database versions were verified.
