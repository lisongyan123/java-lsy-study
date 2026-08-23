[CmdletBinding()]
param(
    [switch]$PullImages,
    [switch]$SkipMetadata,
    [string]$JarRoot
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

function Invoke-Compose {
    param([Parameter(Mandatory)][string[]]$Arguments)

    & docker compose @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "docker compose failed: $($Arguments -join ' ')"
    }
}

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw 'Docker CLI was not found in PATH.'
}

& docker info *> $null
if ($LASTEXITCODE -ne 0) {
    throw 'Docker Engine is not running.'
}

if (-not $JarRoot) {
    $JarRoot = $env:JAR_ROOT
}
if (-not $JarRoot) {
    $envPath = Join-Path $projectRoot '.env'
    if (Test-Path $envPath) {
        $jarRootLine = Get-Content $envPath | Where-Object { $_ -match '^\s*JAR_ROOT=' } | Select-Object -First 1
        if ($jarRootLine) {
            $JarRoot = ($jarRootLine -split '=', 2)[1].Trim().Trim('"').Trim("'")
        }
    }
}
if (-not $JarRoot) {
    throw 'JAR_ROOT is not set. Configure it in .env or pass -JarRoot.'
}
if (-not [IO.Path]::IsPathRooted($JarRoot)) {
    $JarRoot = Join-Path $projectRoot $JarRoot
}
$env:JAR_ROOT = [IO.Path]::GetFullPath($JarRoot)

& (Join-Path $PSScriptRoot 'verify-offline-jars.ps1') -JarRoot $env:JAR_ROOT

$profileArguments = @()
if (-not $SkipMetadata) {
    $profileArguments = @('--profile', 'metadata')
}

Invoke-Compose -Arguments ($profileArguments + @('config', '--quiet'))

if ($PullImages) {
    Invoke-Compose -Arguments ($profileArguments + @('pull'))
}

Invoke-Compose -Arguments ($profileArguments + @('up', '-d', '--remove-orphans', '--wait'))
Invoke-Compose -Arguments ($profileArguments + @('ps', '-a'))

Write-Host "Big Data Platform started successfully with JAR_ROOT=$($env:JAR_ROOT)."
