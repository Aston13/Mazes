#!/usr/bin/env bash
# ============================================================
#  Wesley's Way Out — Quick Launch
#  Run this script to build and launch the game.
# ============================================================
cd "$(dirname "$0")/MazeGame" || exit 1
./gradlew run
