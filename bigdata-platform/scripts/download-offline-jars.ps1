[CmdletBinding()]
param(
	[switch]$Force,
	[string]$JarRoot
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$manifestPath = Join-Path $projectRoot 'config/jars/manifest.tsv'

if (-not $JarRoot) {
	$JarRoot = $env:JAR_ROOT
}
if (-not $JarRoot) {
	throw 'JAR_ROOT is not set. Pass -JarRoot with an external target directory.'
}
if (-not [IO.Path]::IsPathRooted($JarRoot)) {
	$JarRoot = Join-Path $projectRoot $JarRoot
}
$JarRoot = [IO.Path]::GetFullPath($JarRoot)

if (-not (Test-Path $manifestPath)) {
	throw "Manifest not found: $manifestPath"
}

$entries = Import-Csv -Path $manifestPath -Delimiter "`t"
foreach ($entry in $entries) {
	$targetDirectory = Join-Path $JarRoot $entry.scope
	$targetPath = Join-Path $targetDirectory $entry.file
	New-Item -ItemType Directory -Path $targetDirectory -Force | Out-Null

	if ((Test-Path $targetPath) -and -not $Force) {
		Write-Host "SKIP $($entry.scope)/$($entry.file)"
		continue
	}

	Write-Host "GET  $($entry.maven_url)"
	Invoke-WebRequest -Uri $entry.maven_url -OutFile $targetPath
}

& (Join-Path $PSScriptRoot 'verify-offline-jars.ps1') -JarRoot $JarRoot
