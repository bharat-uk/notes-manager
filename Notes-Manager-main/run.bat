@echo off
REM Notes Manager - One-click build and run wrapper

setlocal
set DRIVER_JAR=lib\sqlite-jdbc-3.44.0.0.jar
set SLF4J_API=lib\slf4j-api-1.7.36.jar
set SLF4J_SIMPLE=lib\slf4j-simple-1.7.36.jar
set BUILD_DIR=build
set SRC_DIR=src\main\java
set CLASSPATH=%BUILD_DIR%;%DRIVER_JAR%;%SLF4J_API%;%SLF4J_SIMPLE%

if not exist lib (
    echo Creating lib directory...
    mkdir lib
)

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

if not exist "%BUILD_DIR%" (
    echo Creating build directory...
    mkdir "%BUILD_DIR%"
)

echo.
echo Compiling Notes Manager...
javac -cp "%DRIVER_JAR%;%SLF4J_API%;%SLF4J_SIMPLE%" -d "%BUILD_DIR%" "%SRC_DIR%\com\notesmanager\NotesApp.java" "%SRC_DIR%\com\notesmanager\model\Note.java" "%SRC_DIR%\com\notesmanager\database\DatabaseManager.java" "%SRC_DIR%\com\notesmanager\gui\MainFrame.java" "%SRC_DIR%\com\notesmanager\gui\AddNotePanel.java" "%SRC_DIR%\com\notesmanager\gui\EditNotePanel.java" "%SRC_DIR%\com\notesmanager\gui\ViewNotesPanel.java"
if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed.
    pause
    exit /b 1
)

echo.
echo Compilation successful.
echo Launching Notes Manager...
java -cp "%CLASSPATH%" com.notesmanager.NotesApp
endlocal
