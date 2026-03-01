@echo off
REM ============================================================
REM  Wesley's Way Out — Quick Launch
REM  Double-click this file to build and run the game.
REM ============================================================
cd /d "%~dp0MazeGame"
call gradlew.bat run
pause
