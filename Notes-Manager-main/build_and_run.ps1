# Notes Manager - Build and Run Script for Windows PowerShell
# This script compiles and runs the Notes Manager application

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Notes Manager - Build Script" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Set paths
$DriverJar = "lib\sqlite-jdbc-3.44.0.0.jar"
$Slf4jApi = "lib\slf4j-api-1.7.36.jar"
$Slf4jSimple = "lib\slf4j-simple-1.7.36.jar"
$BuildDir = "build"
$SrcDir = "src\main\java"

# Ensure lib directory exists
if (-not (Test-Path lib)) {
    New-Item -ItemType Directory -Path lib | Out-Null
}

# Download missing runtime jars
if (-not (Test-Path $DriverJar)) {
    Write-Host "Downloading SQLite JDBC driver..." -ForegroundColor Green
    Invoke-WebRequest -Uri 'https://github.com/xerial/sqlite-jdbc/releases/download/3.44.0.0/sqlite-jdbc-3.44.0.0.jar' -OutFile $DriverJar
}
if (-not (Test-Path $Slf4jApi)) {
    Write-Host "Downloading SLF4J API..." -ForegroundColor Green
    Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar' -OutFile $Slf4jApi
}
if (-not (Test-Path $Slf4jSimple)) {
    Write-Host "Downloading SLF4J Simple binding..." -ForegroundColor Green
    Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar' -OutFile $Slf4jSimple
}

# Validate required jars
if (-not (Test-Path $DriverJar)) {
    Write-Host "`nERROR: SQLite JDBC driver not found at $DriverJar`n" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
if (-not (Test-Path $Slf4jApi)) {
    Write-Host "`nERROR: SLF4J API jar not found at $Slf4jApi`n" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
if (-not (Test-Path $Slf4jSimple)) {
    Write-Host "`nERROR: SLF4J Simple jar not found at $Slf4jSimple`n" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Create build directory
if (-not (Test-Path $BuildDir)) {
    Write-Host "Creating build directory..." -ForegroundColor Green
    New-Item -ItemType Directory -Path $BuildDir | Out-Null
}

# Compile
Write-Host "`nCompiling Java files...`n" -ForegroundColor Green

$JavaFiles = @(
    "$SrcDir\com\notesmanager\NotesApp.java",
    "$SrcDir\com\notesmanager\model\Note.java",
    "$SrcDir\com\notesmanager\database\DatabaseManager.java",
    "$SrcDir\com\notesmanager\gui\MainFrame.java",
    "$SrcDir\com\notesmanager\gui\AddNotePanel.java",
    "$SrcDir\com\notesmanager\gui\EditNotePanel.java",
    "$SrcDir\com\notesmanager\gui\ViewNotesPanel.java"
)

javac -cp "$DriverJar" -d "$BuildDir" @JavaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host "`nERROR: Compilation failed!`n" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "`nCompilation successful!`n" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Starting Notes Manager Application" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Run
java -cp "$BuildDir;$DriverJar" com.notesmanager.NotesApp
