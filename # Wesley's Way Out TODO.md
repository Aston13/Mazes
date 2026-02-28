# Wesley's Way Out — TODO

## Testing

- [x] **Expand JUnit 5 test coverage.** ~~Currently only `TileTest.java` and `RecursiveBacktrackerTest.java` exist.~~ Added `PlayerTest`, `TilemapTest`, `AssetManagerTest`, `MazeGameStateTest` (29 new tests, 40 total). Renderer tests deferred (requires Graphics mocking).
- [x] **Add a CI test step.** `./gradlew test` now runs before building JARs in `.github/workflows/release.yml`.
- [ ] **Add a pre-push hook or local script** that runs `./gradlew test` so developers catch regressions before pushing.

## Project Structure & Build

- [ ] **Migrate to standard Maven/Gradle source layout.** Move sources from `MazeGame/src/mazegame/` → `MazeGame/src/main/java/mazegame/`, tests from `MazeGame/test/` → `MazeGame/src/test/java/`, and resources to `MazeGame/src/main/resources/mazegame/Assets/`. Remove the custom `sourceSets` block from `build.gradle`.
- [x] **Remove legacy NetBeans files.** Deleted `nbproject/`, `build.xml`, `manifest.mf`, and `build/`.
- [ ] **Add Gradle `check` and `spotless` plugins** for automated code formatting and static analysis (e.g. `com.diffplug.spotless` with Google Java Format).
- [x] **Upgrade Gradle wrapper** to 9.3.1 (was 8.12).

## Game Engine / Architecture

- [x] **General code refactor across all classes.** Completed:
  - Removed dead/commented-out code and unused imports
  - Renamed vague variables (`am`→`assetManager`, `halfP`→`halfPlayer`, `rb1`→`mazeGenerator`, `t`→`gameTimer`, `c1`→`color`, `imgStr`→`imageString`, etc.)
  - Extracted magic numbers into named constants across all files
  - Added Javadoc to all public classes and methods
  - Refactored `AssetManager` from 70+ individual fields to loop-based loading (357→213 lines)
  - Made fields `final` where possible, consistent code style throughout

- [ ] **Decouple game loop from JFrame.** `MazeGame` is a god-class (~623 lines) that mixes JFrame lifecycle, game loop, rendering, input, menus, and level management. Extract:
  - `GameLoop` — owns the `while(getGameState())` loop, timing, pause logic
  - `MenuManager` — owns `runMenu()`, `runLevelSelection()`, `runGameOverScreen()`, `runCompletionScreen()`
  - `InputHandler` — owns `setNESWKeys()`, `addKeyBinding()`, global `KeyEventDispatcher`
- [x] **Fix thread safety.** `gameInProgress` marked `volatile` for cross-thread visibility.
- [ ] **Replace `new Thread(this)` pattern.** Each level/retry creates a new `MazeGame` instance + raw `Thread`. Use a single persistent game thread with state resets, or use an `ExecutorService`.
- [ ] **Fix `dispose()` calls in menu transitions.** `runMenu()` calls `dispose()` then creates a new `MazeGame` JFrame — this leaks native resources. Reuse the existing frame and swap content panes instead.
- [x] **Improve frame rate control.** Added `Thread.sleep()` with FPS cap at target 30 FPS using `FRAME_TIME_NS` constant.

## Gameplay & UX

- [ ] **Show countdown timer in the in-game HUD.** The timer exists in `Renderer` but the remaining time is not displayed on-screen during gameplay. Add a visible timer bar or text in `renderHUD()`.
- [ ] **Make the window resizable / scale to screen size.** Currently hardcoded to 650×650. Use `AffineTransform` scaling or render to a `BufferedImage` and scale-blit to support different window sizes.
- [ ] **Modernise the main menu UI.** Replace plain `JButton`/`JLabel` on black background with:
  - An animated background (e.g. a slowly-scrolling maze or particle effect)
  - Styled buttons with hover effects (custom `paintComponent` or a look-and-feel)
  - The game logo as a proper image/sprite instead of a `JLabel` with `Font("Dialog")`
- [ ] **Improve level selection screen.** Currently a raw scrollable list of panels. Add:
  - Visual grid layout (e.g. 5×6 grid of level cards)
  - Lock/star icons for incomplete/completed levels
  - Best time display per level
  - Scroll position memory
- [x] **Add WASD key support** as alternative movement keys alongside arrow keys in `setNESWKeys()`.
- [x] **Add a "Restart Level" button** to the pause menu (Canvas-rendered). Press **R** or click to restart.

## Dog Character / Assets

- [ ] **Redesign the dog sprite to look like Wesley.** Current sprites are in `Assets/AnimationFrames/Dog/`. Create new pixel-art frames based on Wesley's real appearance (golden/cream cocker spaniel — reference: https://www.instagram.com/wesliwoo/). Needs N/E/S/W walk cycle frames.
- [ ] **Update the favicon** (`docs/favicon.ico` and `docs/favicon.png`) to match the new Wesley sprite.

## Developer Experience

- [x] **Improve VS Code workspace settings.** Added `.vscode/settings.json`, `launch.json`, `tasks.json` with build/test/run/browserJar tasks, exclude patterns, and formatter settings.
- [x] **Add a `.vscode/extensions.json`** with Extension Pack for Java, Gradle for Java, EditorConfig, GitLens.
- [ ] **Add a `./gradlew runBrowser` task** that builds the browser JAR, copies it to `docs/`, and opens a local HTTP server (e.g. Python `http.server`) for testing CheerpJ locally without pushing to GitHub Pages.
- [x] **Add `.editorconfig`** at repo root (4-space Java, 2-space JSON/YAML/HTML, UTF-8, LF, trim trailing whitespace, final newline).
- [x] **Document the dev workflow** in `README.md`: build, run, test, deploy (desktop JAR, browser JAR, GitHub Pages, release process).