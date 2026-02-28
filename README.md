# Wesley's Way Out

A Java maze game built with Swing/AWT where you guide Wesley the dog through procedurally generated mazes, collecting keys to unlock the exit before time runs out.

**[▶ Play in Browser](https://Aston13.github.io/Mazes/)** — no download or Java install required (powered by CheerpJ)

## Gameplay

- Arrow keys or **WASD** to move (N/E/S/W)
- Collect all keys to unlock the exit door
- Keys disappear over time — if too many are lost, the level fails
- 30 progressively larger levels with best-time tracking
- Press **Space** to continue, **Esc** to pause/return to menu
- While paused: **R** to restart, **Space/Esc** to resume

## Screenshots

*650×650 window with a top-down maze, animated dog sprite, and key collectibles.*

## Prerequisites

- **Java 21+** — if not installed locally, Gradle will auto-download it via the [Foojay toolchain resolver](https://github.com/gradle/foojay-toolchains)
- No Gradle installation needed (the included Gradle Wrapper handles it)

## Quick Start

```bash
cd MazeGame

# Build & test
./gradlew build        # Linux/macOS
.\gradlew.bat build    # Windows

# Run the game
./gradlew run          # Linux/macOS
.\gradlew.bat run      # Windows
```

## Build Commands

| Command | Description |
|---------|-------------|
| `gradlew build` | Compile, test, and package into a JAR |
| `gradlew run` | Launch the game |
| `gradlew test` | Run JUnit 5 tests only |
| `gradlew jar` | Build the JAR (output: `build/libs/MazeGame-1.0.0.jar`) |
| `gradlew clean` | Delete all build artifacts |

## Running the JAR Directly

```bash
java -jar MazeGame/build/libs/MazeGame-1.0.0.jar
```

## Project Structure

```
MazeGame/
├── build.gradle                  # Gradle build config (Java 21, JUnit 5)
├── settings.gradle               # Project name + Foojay toolchain resolver
├── gradlew / gradlew.bat         # Gradle wrapper scripts
├── src/
│   └── mazegame/
│       ├── Start.java            # Entry point
│       ├── MazeGame.java         # JFrame, game loop, menus
│       ├── Renderer.java         # Rendering, collision, animation
│       ├── Player.java           # Player position & movement state
│       ├── RecursiveBacktracker.java  # Maze generation algorithm
│       ├── Tilemap.java          # Grid data structure
│       ├── Tile.java             # Tile interface
│       ├── TileWall.java         # Impassable wall tile
│       ├── TilePassage.java      # Passable tile (can hold key items)
│       ├── TileExit.java         # Exit tile (lockable/unlockable)
│       ├── AssetManager.java     # Image loading & level data I/O
│       ├── UI.java               # Swing component factory for menus
│       └── Assets/               # Sprites, animation frames, level data
└── test/
    └── mazegame/
        ├── AssetManagerTest.java
        ├── MazeGameStateTest.java
        ├── PlayerTest.java
        ├── RecursiveBacktrackerTest.java
        ├── TileTest.java
        └── TilemapTest.java
```

## How the Maze Works

The maze is generated using a **recursive backtracker** algorithm:

1. Start at a random odd-coordinate cell
2. Randomly choose an unvisited neighbour, carve a passage to it
3. Recurse from the new cell; backtrack when stuck
4. The exit is placed at the furthest reachable point from the start
5. Keys are scattered randomly across passage tiles

Each level increases the grid size by 2 (starting at 11×11), making mazes progressively harder.

## Tech Stack

- **Java 21** (LTS)
- **Swing / AWT** — windowing, rendering, input
- **Gradle 9.3.1** — build system
- **JUnit 5** — testing

## Development

### IDE Setup

The repo includes a `.vscode/` workspace configuration. Open the `MazeGame/` folder (or the repo root) in VS Code with the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) and [Gradle for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle) extensions installed. Recommended extensions are listed in `.vscode/extensions.json`.

An `.editorconfig` file enforces consistent formatting (4-space indent for Java, 2-space for JSON/YAML/HTML, UTF-8, LF line endings, trim trailing whitespace).

### Building & Testing

```bash
cd MazeGame

# Full build (compile + test + JAR)
./gradlew build

# Run tests only
./gradlew test

# Launch the desktop game
./gradlew run
```

### Browser Build (CheerpJ)

The game runs in the browser via [CheerpJ 3.0](https://cheerpj.com/), which requires Java 8 bytecode.

```bash
# Build the Java-8-compatible JAR for CheerpJ
./gradlew browserJar
# Output: build/libs/MazeGame-browser-1.0.0.jar

# Copy to docs/ for GitHub Pages
cp build/libs/MazeGame-browser-*.jar ../docs/MazeGame.jar
```

To test locally, serve the `docs/` folder over HTTP (CheerpJ requires HTTP, not `file://`):

```bash
cd ../docs
python -m http.server 8080
# Open http://localhost:8080 in your browser
```

### Releasing

Releases are automated via GitHub Actions. Push a version tag to trigger the workflow:

```bash
git tag v1.2.0
git push origin v1.2.0
```

The workflow (`.github/workflows/release.yml`) will:
1. Run all tests (`./gradlew test`)
2. Build the desktop JAR and browser JAR
3. Update the GitHub Pages JAR in `docs/`
4. Create a GitHub Release with both JARs attached

### Source Layout

This project uses a non-standard source layout inherited from the original NetBeans project:

| Path | Content |
|------|---------|
| `MazeGame/src/mazegame/` | Java sources |
| `MazeGame/src/mazegame/Assets/` | Sprites, animation frames, level data (loaded via classpath) |
| `MazeGame/test/mazegame/` | JUnit 5 tests |
| `docs/` | GitHub Pages site (CheerpJ browser player) |

This is configured in `build.gradle` via custom `sourceSets` blocks.

## License

All Rights Reserved. See [LICENSE](LICENSE) for details.