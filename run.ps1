# Notes Manager wrapper for current workspace folder
$ProjectDir = Join-Path $PSScriptRoot 'Notes-Manager-main'
if (-not (Test-Path $ProjectDir)) {
    Write-Host "ERROR: Project folder 'Notes-Manager-main' not found in $PSScriptRoot" -ForegroundColor Red
    return
}
Set-Location $ProjectDir
.\run.ps1
