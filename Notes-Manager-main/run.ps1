# Notes Manager - One-click PowerShell build and run wrapper

$DriverJar = "lib\sqlite-jdbc-3.44.0.0.jar"
$Slf4jApi = "lib\slf4j-api-1.7.36.jar"
$Slf4jSimple = "lib\slf4j-simple-1.7.36.jar"
$BuildDir = "build"
$SrcDir = "src\main\java"
$Classpath = "$BuildDir;$DriverJar;$Slf4jApi;$Slf4jSimple"

if (-not (Test-Path lib)) {
    Write-Host "Creating lib directory..." -ForegroundColor Green
    New-Item -ItemType Directory -Path lib | Out-Null
}

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

if (-not (Test-Path $DriverJar)) {
    Write-Host "ERROR: SQLite JDBC driver not found at $DriverJar" -ForegroundColor Red
    return
}

if (-not (Test-Path $Slf4jApi)) {
    Write-Host "ERROR: SLF4J API jar not found at $Slf4jApi" -ForegroundColor Red
    return
}

if (-not (Test-Path $Slf4jSimple)) {
    Write-Host "ERROR: SLF4J Simple jar not found at $Slf4jSimple" -ForegroundColor Red
    return
}

if (-not (Test-Path $BuildDir)) {
    Write-Host "Creating build directory..." -ForegroundColor Green
    New-Item -ItemType Directory -Path $BuildDir | Out-Null
}

Write-Host "`nCompiling Notes Manager..." -ForegroundColor Cyan
javac -cp "$DriverJar;$Slf4jApi;$Slf4jSimple" -d "$BuildDir" \
    "$SrcDir\com\notesmanager\NotesApp.java" \
    "$SrcDir\com\notesmanager\model\Note.java" \
    "$SrcDir\com\notesmanager\database\DatabaseManager.java" \
    "$SrcDir\com\notesmanager\gui\MainFrame.java" \
    "$SrcDir\com\notesmanager\gui\AddNotePanel.java" \
    "$SrcDir\com\notesmanager\gui\EditNotePanel.java" \
    "$SrcDir\com\notesmanager\gui\ViewNotesPanel.java"

if ($LASTEXITCODE -ne 0) {
    Write-Host "`nERROR: Compilation failed." -ForegroundColor Red
    return
}

Write-Host "`nCompilation successful." -ForegroundColor Green
Write-Host "Launching Notes Manager...`n" -ForegroundColor Cyan
java -cp "$Classpath" com.notesmanager.NotesApp
