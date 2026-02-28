# Wesley's Way Out — TODO

## Testing

- [ ] **Expand JUnit 5 test coverage.** Currently only `TileTest.java` and `RecursiveBacktrackerTest.java` exist in `MazeGame/test/mazegame/`. Add tests for:
  - `Player` — movement setters/getters, bounds, size
  - `AssetManager` — `loadLevelData()` (classpath resource), `saveLevelData()`, reset behaviour
  - `Tilemap` — grid construction, tile retrieval, odd-size enforcement
  - `Renderer` — collision detection (`checkCollision`), maze centering, HUD rendering (mock Graphics)
  - `MazeGame` — game state transitions (`setGameState`/`getGameState`), `setCurrentLevel`, `increaseLevel`
- [ ] **Add a CI test step.** Update `.github/workflows/release.yml` to run `./gradlew test` before building JARs, so tests gate every release.
- [ ] **Add a pre-push hook or local script** that runs `./gradlew test` so developers catch regressions before pushing.

## Project Structure & Build

- [ ] **Migrate to standard Maven/Gradle source layout.** Move sources from `MazeGame/src/mazegame/` → `MazeGame/src/main/java/mazegame/`, tests from `MazeGame/test/` → `MazeGame/src/test/java/`, and resources to `MazeGame/src/main/resources/mazegame/Assets/`. Remove the custom `sourceSets` block from `build.gradle`.
- [ ] **Remove legacy NetBeans files.** Delete `MazeGame/nbproject/`, `MazeGame/build.xml`, `MazeGame/manifest.mf`, and `MazeGame/build/` (old Ant output). These are unused since the Gradle migration.
- [ ] **Add Gradle `check` and `spotless` plugins** for automated code formatting and static analysis (e.g. `com.diffplug.spotless` with Google Java Format).
- [ ] **Upgrade Gradle wrapper** if a newer stable version is available (currently 8.12). Run `./gradlew wrapper --gradle-version=latest`.

## Game Engine / Architecture

- [ ] **Decouple game loop from JFrame.** `MazeGame` is a god-class (~623 lines) that mixes JFrame lifecycle, game loop, rendering, input, menus, and level management. Extract:
  - `GameLoop` — owns the `while(getGameState())` loop, timing, pause logic
  - `MenuManager` — owns `runMenu()`, `runLevelSelection()`, `runGameOverScreen()`, `runCompletionScreen()`
  - `InputHandler` — owns `setNESWKeys()`, `addKeyBinding()`, global `KeyEventDispatcher`
- [ ] **Fix thread safety.** `gameInProgress` is read/written from both EDT and game thread but is not `volatile` (unlike `paused`). Mark it `volatile` or use `AtomicBoolean`.
- [ ] **Replace `new Thread(this)` pattern.** Each level/retry creates a new `MazeGame` instance + raw `Thread`. Use a single persistent game thread with state resets, or use an `ExecutorService`.
- [ ] **Fix `dispose()` calls in menu transitions.** `runMenu()` calls `dispose()` then creates a new `MazeGame` JFrame — this leaks native resources. Reuse the existing frame and swap content panes instead.
- [ ] **Improve frame rate control.** The game loop lacks `Thread.sleep()` and busy-spins at 100% CPU. Add a sleep to cap at target FPS (30).

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
- [ ] **Add WASD key support** as alternative movement keys alongside arrow keys in `setNESWKeys()`.
- [ ] **Add a "Restart Level" button** to the pause menu so players can retry without going to the main menu.

## Dog Character / Assets

- [ ] **Redesign the dog sprite to look like Wesley.** Current sprites are in `Assets/AnimationFrames/Dog/`. Create new pixel-art frames based on Wesley's real appearance (golden/cream cocker spaniel — reference: https://www.instagram.com/wesliwoo/). Needs N/E/S/W walk cycle frames.
- [ ] **Update the favicon** (`docs/favicon.ico` and `docs/favicon.png`) to match the new Wesley sprite.

## Developer Experience

- [ ] **Add a `./gradlew runBrowser` task** that builds the browser JAR, copies it to `docs/`, and opens a local HTTP server (e.g. Python `http.server`) for testing CheerpJ locally without pushing to GitHub Pages.
- [ ] **Add EditorConfig or Spotless config** to enforce consistent formatting (indentation, line endings, trailing whitespace) across all contributors.
- [ ] **Document the dev workflow** in `README.md` or a `CONTRIBUTING.md`: how to build, run, test, and deploy (desktop JAR, browser JAR, GitHub Pages).