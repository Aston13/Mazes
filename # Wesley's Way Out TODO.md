# Wesley's Way Out — TODO

## Blocked

- [ ] **Browser audio support** — `javax.sound.sampled` is not supported by CheerpJ, so all synthesised sounds and music are silently skipped in the browser. A warning is shown on the HTML page and in the Settings panel.
  - *Blocked:* Needs CheerpJ JavaScript interop (`cjCall`/`cjResolveCall`) to bridge to Web Audio API, or a parallel JS-side audio engine. Non-trivial and requires browser-only testing.

## Bugs

- [ ] **Confetti not visible on level completion** — `spawnConfetti()` fires but `game.setGameState(false, "Next Level")` triggers immediately after, swapping the panel before confetti has time to render. Needs a short delay (e.g. 1–2 s) between confetti spawn and the screen transition, or confetti should carry over onto the result overlay.
- [ ] **App icon is default Java icon** — When launched from `.bat`/`.sh` scripts, the window title bar and taskbar show the generic Java coffee-cup icon. Should call `frame.setIconImage()` with the Wesley sprite (e.g. `wesleyEast0`) in `MazeGame.setUpFrame()`.

## UI / Polish

- [ ] **Spinning key sprites on menu button icons** — The animated key sprites used in-game could be repurposed as decorative icons on menu buttons (e.g. a small spinning key beside "Continue") for visual cohesion between gameplay and menus.
- [ ] **Pillar region detection** — Large wall clusters are expected behaviour of the recursive backtracker (even-even grid positions are always walls). Could detect and regenerate excessively "pillar-heavy" mazes, but may not be worth the complexity if it's rare.
- [ ] feat: Add a toggleable diagnostics for exposing helpful info

## Completed

- [x] **i18n / localisation system** — `Messages.java` resource-bundle wrapper with `get(key)` and `fmt(key, args...)`. English (default) and Norwegian Bokmål (`Messages_nb.properties`) included. All UI strings across 8 source files wired through `Messages`. Language toggle in Settings panel cycles locale and persists the preference in `GameSettings`.
- [x] **Mobile browser touch controls** — `TouchDpad.java` renders a translucent directional pad (bottom-right) and pause icon (top-right) when running in CheerpJ. Touch/mouse press/release/drag events mapped to player movement via `InputHandler.bindTouchControls()`. Works with CheerpJ's touch→mouse event bridge.
- [x] **In-game pause button consistency** — Removed custom `drawPauseButton()` and migrated pause overlay to `UiTheme.paintStdButton()` with hover highlighting and spinning diamond accents, matching all other menu buttons.
- [x] **Bone counter layout** — Right-aligned in level-selection header with drawn bone icon. No longer clipped on small windows.
- [x] **Bone emoji renders as square box** — Replaced all SMP emoji (🦴, 🔒, 🔇, 🔊) with Java2D drawn equivalents (`UiTheme.generateBoneIcon()`, `UiTheme.drawLockIcon()`, plain text). BMP characters (✓, ▶, ♪, ⚠, ·) retained.
- [x] **Collectible bones + Sasso skin unlock** — One golden bone per level, procedurally rendered with Java2D (bob animation). Pickup triggers crunch sound, golden flash, "Bone Found!" text, and a dog quip. HUD shows bone status. Level selection cards show golden check for collected bones. Completion screen shows total (X/30). Sasso skin locked behind 10 bones with dark overlay + lock icon in Settings.
- [x] **Test expansion** — TileTest 6→22 tests (colour states, bounds, passage image IDs, all tile types). RecursiveBacktrackerTest 4→15 tests (key formula, BFS connectivity, border walls, wall IDs). GameSettingsTest +3 skin-unlock tests.
- [x] **Dog bark on speech bubbles** — `DOG_TALK` sound synthesised and plays whenever an overhead quip triggers.
- [x] **Browser black screen fix** — Overly broad CSS rule was hiding the CheerpJ display `<div>`. Scoped the rule to fix rendering.
- [x] **Splash white flash fix** — Set dark background on pane before splash transition to eliminate the brief white frame.
- [x] **Level-start quips** — Random dog quip triggers at the start of each level. "Dad" references updated to "Aston".
- [x] **Splash screen** — Mask image with "Somebody stop me!" quote and Aston13 credits. Fades in/out over 4 s, auto-advances to main menu. Click or key to skip.
- [x] **Floating particles on all static screens** — Subtle warm-accent particles drift upward on result overlays, settings, and level-selection screens.
- [x] **Button hover spinning diamonds** — Rotating diamond accents on either side of hovered buttons (main menu + result screens).
- [x] **Run scripts** — `run-game.bat` (Windows) and `run-game.sh` (macOS/Linux) for launching without Gradle commands.
- [x] **VS Code debug config** — Run Game and Debug Game launch configurations in `.vscode/launch.json`.
- [x] **RuneScape-style speech bubbles** — Yellow text + black drop shadow above player. Idle quips every 12–25 s; contextual quips on key pickup, locked door, all-keys. Dog-themed from Wesley's perspective.
- [x] **Door confetti burst** — 60 particles spray from player on exit. Gravity, spin, quadratic fade over 1.8 s.
- [x] **Bug: Quit music** — Fixed: `WindowListener` + explicit `stopMusic()` before `frame.dispose()`.
- [x] **Bug: Game speed affected by keys** — Fixed: `GameLoop.tick()` now decrements properly instead of resetting to 0.
- [x] **Bug: Key timer crash** — Fixed: timer short-circuits when `keyCount >= keysRequired` or `keysOnMap` is empty.
- [x] **Result screen polish** — Title glow, shadow, letter-spacing, gradient accent line with diamond, optional subtitle.
- [x] **Main menu animation** — Floating warm-accent particles so the screen isn't static.
