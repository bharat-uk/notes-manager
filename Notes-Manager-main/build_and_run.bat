@echo off
REM Notes Manager - Build and Run Script for Windows
REM This script compiles and runs the Notes Manager application

setlocal enabledelayedexpansion

echo.
echo ========================================
echo   Notes Manager - Build Script
echo ========================================
echo.

REM Set paths
set DRIVER_JAR=lib\sqlite-jdbc-3.44.0.0.jar
set SLF4J_API=lib\slf4j-api-1.7.36.jar
set SLF4J_SIMPLE=lib\slf4j-simple-1.7.36.jar
set BUILD_DIR=build
set SRC_DIR=src\main\java
set CLASSPATH=%BUILD_DIR%;%DRIVER_JAR%;%SLF4J_API%;%SLF4J_SIMPLE%

REM Ensure lib directory exists
if not exist lib (
    echo Creating lib directory...
    mkdir lib
)

REM Download missing runtime jars
if not exist "%DRIVER_JAR%" (
    echo Downloading SQLite JDBC driver...
    powershell -Command "Invoke-WebRequest -Uri 'https://github.com/xerial/sqlite-jdbc/releases/download/3.44.0.0/sqlite-jdbc-3.44.0.0.jar' -OutFile '%DRIVER_JAR%'" 2>nul
)
if not exist "%SLF4J_API%" (
    echo Downloading SLF4J API...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar' -OutFile '%SLF4J_API%'" 2>nul
)
if not exist "%SLF4J_SIMPLE%" (
    echo Downloading SLF4J Simple binding...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar' -OutFile '%SLF4J_SIMPLE%'" 2>nul
)

REM Check if required jars exist
if not exist "%DRIVER_JAR%" (
    echo.
    echo ERROR: SQLite JDBC driver not found at %DRIVER_JAR%
    pause
    exit /b 1
)
if not exist "%SLF4J_API%" (
    echo.
    echo ERROR: SLF4J API jar not found at %SLF4J_API%
    pause
    exit /b 1
)
if not exist "%SLF4J_SIMPLE%" (
    echo.
    echo ERROR: SLF4J Simple jar not found at %SLF4J_SIMPLE%
    pause
    exit /b 1
)

REM Create build directory
if not exist "%BUILD_DIR%" (
    echo Creating build directory...
    mkdir "%BUILD_DIR%"
)

REM Compile
echo.
echo Compiling Java files...
echo.

javac -cp "%DRIVER_JAR%;%SLF4J_API%;%SLF4J_SIMPLE%" -d "%BUILD_DIR%" ^
    "%SRC_DIR%\com\notesmanager\NotesApp.java" ^
    "%SRC_DIR%\com\notesmanager\model\Note.java" ^
    "%SRC_DIR%\com\notesmanager\database\DatabaseManager.java" ^
    "%SRC_DIR%\com\notesmanager\gui\MainFrame.java" ^
    "%SRC_DIR%\com\notesmanager\gui\AddNotePanel.java" ^
    "%SRC_DIR%\com\notesmanager\gui\EditNotePanel.java" ^
    "%SRC_DIR%\com\notesmanager\gui\ViewNotesPanel.java"

if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo.
echo ========================================
echo   Starting Notes Manager Application
echo ========================================
echo.

REM Run
java -cp "%CLASSPATH%" com.notesmanager.NotesApp

endlocal
