# Copilot Instructions

## Project Overview
Wesley's Way Out — a Java Swing/AWT maze game with procedural maze generation. Single `mazegame` package, built with Gradle.

## Source Layout (standard Maven/Gradle)
- Source: `MazeGame/src/main/java/mazegame/`
- Tests: `MazeGame/src/test/java/mazegame/`
- Assets: `MazeGame/src/main/resources/mazegame/Assets/` (loaded via classpath `getResourceAsStream`)
- Build: Gradle with `build.gradle` in `MazeGame/`

## Key Patterns

### Tile Hierarchy
`Tile` is an **interface** with three implementations:
- `TileWall` — impassable, has NESW neighbour bits for sprite selection
- `TilePassage` — passable, can hold a key item
- `TileExit` — exit door, lockable/unlockable based on key count

All tiles store position (minX, minY) and size. New tile types must implement the `Tile` interface.

### Threading Model
- `MazeGame` extends `JFrame` and implements `Runnable` — the game loop runs on its own `Thread`
- Swing UI setup must happen on the EDT (`SwingUtilities.invokeLater`)
- `javax.swing.Timer` is used for in-game timers (key removal countdown, animation frames)
- Do NOT modify Swing components from the game thread without `invokeLater`

### Asset Loading
- All images are preloaded into a `HashMap<String, BufferedImage>` in `AssetManager`
- Resource paths must use **forward slashes** (`Assets/filename.png`), not backslashes
- Level data is read/written via `BufferedReader`/`BufferedWriter` with try-with-resources

### Maze Generation
- `RecursiveBacktracker` extends `Tilemap` and implements a recursive backtracking algorithm
- Grid size is always odd (enforced). Starting coordinates are random odd numbers.
- Exit is placed at the furthest reachable point from the start

## Code Conventions
- Java 21 target (use modern features: lambdas, try-with-resources, diamond operator, etc.)
- Use `.equals()` for String comparison, never `==`
- Use `Graphics.dispose()`, never `finalize()`
- Prefer `double` primitives over `Double` wrappers where possible
- JUnit 5 for tests
