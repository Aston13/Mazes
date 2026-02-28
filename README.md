# MazeGame

A Java maze game built with Swing/AWT where you navigate a procedurally generated maze, collect keys to unlock the exit, and race against the clock.

**[▶ Play in Browser](https://Aston13.github.io/Mazes/)** — no download or Java install required (powered by CheerpJ)

## Gameplay

- Arrow keys to move (N/E/S/W)
- Collect all keys to unlock the exit door
- Keys disappear over time — if too many are lost, the level fails
- 30 progressively larger levels with best-time tracking
- Press **Space** to continue, **Esc** to return to menu

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
        ├── RecursiveBacktrackerTest.java
        └── TileTest.java
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
- **Gradle 8.12** — build system
- **JUnit 5** — testing

## License

This project does not currently specify a license.