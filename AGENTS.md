# Boxes Android Project

## Project scope

- `:app` is the only active Gradle module. Production source is in `app/src/main`, unit tests are in `app/src/test`, and instrumented tests are in `app/src/androidTest`.
- `androidApp/` and `composeApp/` are inactive migration experiments. Do not modify or revive them unless the task explicitly includes them.
- Do not edit generated content under `build/`, `.gradle/`, or an individual module's `build/` directory.
- Do not modify `app/release/` unless the task explicitly concerns local release artifacts.

## Technology and conventions

- The app uses Kotlin, Jetpack Compose, Material 3, Navigation 3, Koin, Room, coroutines and Flow, and JUnit 5.
- Add or update dependency versions through `gradle/libs.versions.toml`; do not add inline versions to module build files.
- Follow the existing package structure rooted at `com.jerry.bit.shapes`.
- Let ktlint enforce mechanical Kotlin formatting. Do not add competing style rules.

## Architecture

- Composables render state and emit user actions. Keep persistence, file I/O, bitmap processing, and long-running work out of composables.
- ViewModels own screen state and coordinate application behavior. Prefer immutable values in state exposed to UI.
- Keep database and project import/export operations in the repository and DAO layers.
- Collect Flow from composables with lifecycle-aware APIs such as `collectAsStateWithLifecycle`.
- Launch screen-owned asynchronous work from lifecycle-aware scopes.
- Follow the existing serializable `NavKey` pattern and register new Navigation 3 destinations in `NavigationModule`.
- Add new Koin dependencies to the appropriate module under `inject/`.
- Do not introduce a new architectural layer or Gradle module for a small isolated change.

## Persistence and compatibility

- Treat Room data and exported BitShape project files as persistent user data.
- A Room schema change requires a database version increase, an explicit migration, and migration coverage. Do not use destructive migration for released user data.
- `Shape` enum names are stored in the database and project files. Do not rename or remove an existing value without an explicit compatibility migration.
- Changes to `ProjectTransfer` must preserve supported files or explicitly increment the format version and provide a compatibility path.
- Preserve import validation for dimensions, coordinates, duplicate pixels, layer counts, names, and shape identifiers.
- Keep related multi-table writes inside a Room transaction.

## UI and performance

- Hoist reusable component state where practical and avoid exposing new mutable ViewModel-owned state directly to composables.
- Give interactive icons meaningful accessibility descriptions. Decorative images may use a null description.
- Preserve edge-to-edge behavior and system-bar and IME insets when changing top-level layouts.
- Do not add avoidable allocation, sorting, database access, or bitmap work to composition or drawing hot paths.
- Avoid adding unrelated responsibilities to the already large `BoxesMain.kt`, `BoxesViewModel.kt`, and `Shape.kt` files.

## Testing and verification

- Add or update focused tests for behavior changed by the task. Bug fixes should include a regression test when the behavior can be tested reliably.
- Use coroutine test APIs and the existing main-dispatcher test support for ViewModel tests.
- Project import/export changes require round-trip and invalid-input tests. Database schema changes require migration tests. Shape changes require coverage of both Compose and Android Canvas rendering paths.
- Run the narrowest relevant checks while developing. Before completing a substantial change, run as applicable:
  - `./gradlew ktlintCheck`
  - `./gradlew testDebugUnitTest`
  - `./gradlew lintDebug`
  - `./gradlew assembleDebug`
- For release-specific changes, verify the release build explicitly. Report checks that could not be run and why.

## Signing and sensitive configuration

- The checked-in release signing values are dummy credentials used only for testing release builds locally.
- Never replace them with production credentials or commit production signing material. Production publishing must use separately managed signing credentials.
- Do not commit new secrets, private keys, access tokens, or machine-specific paths.

## Change discipline

- Preserve unrelated user changes in a dirty worktree and keep changes scoped to the requested behavior.
- Prefer extending existing project patterns unless those patterns directly cause the problem being addressed.
- Update documentation when behavior, file formats, build requirements, or supported workflows change.
