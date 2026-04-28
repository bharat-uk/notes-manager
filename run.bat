@echo off
REM Notes Manager wrapper for current workspace folder
cd /d "%~dp0Notes-Manager-main"
if errorlevel 1 (
  echo ERROR: Could not change directory to Notes-Manager-main
  pause
  exit /b 1
)
call run.bat
# .\build_and_run.bat