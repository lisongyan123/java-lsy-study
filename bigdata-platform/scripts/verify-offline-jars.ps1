[CmdletBinding()]
param(
	[string]$JarRoot
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$manifestPath = Join-Path $projectRoot 'config/jars/manifest.tsv'
$checksumPath = Join-Path $projectRoot 'config/jars/checksums.sha256'
$issues = @()

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
$JarRoot = [IO.Path]::GetFullPath($JarRoot)

if (-not (Test-Path $manifestPath)) {
	throw "JAR manifest not found: $manifestPath"
}

Write-Host "JAR root: $JarRoot"

$entries = Import-Csv -Path $manifestPath -Delimiter "`t"
foreach ($entry in $entries) {
	$relativePath = "$($entry.scope)/$($entry.file)"
	$path = Join-Path $JarRoot $relativePath
	if (-not (Test-Path $path)) {
		if ($entry.required -eq 'yes') {
			$issues += "MISSING $relativePath"
		} else {
			Write-Host "OPTIONAL $relativePath"
		}
	} elseif ((Get-Item $path).Length -eq 0) {
		$issues += "EMPTY   $relativePath"
	} else {
		$header = @(Get-Content -Path $path -Encoding Byte -TotalCount 2)
		if ($header.Count -lt 2 -or $header[0] -ne 0x50 -or $header[1] -ne 0x4b) {
			$issues += "NOTJAR  $relativePath"
		} else {
			Write-Host "OK      $relativePath"
		}
	}
}

$checksumLines = if (Test-Path $checksumPath) {
	Get-Content $checksumPath | Where-Object { $_ -and -not $_.StartsWith('#') }
} else {
	@()
}
foreach ($checksumLine in $checksumLines) {
	if ($checksumLine -notmatch '^([0-9a-fA-F]{64})\s+(.+)$') {
		continue
	}
	$expected = $Matches[1].ToLowerInvariant()
	$relativePath = $Matches[2].Replace('/', [IO.Path]::DirectorySeparatorChar)
	$path = Join-Path $JarRoot $relativePath
	if ((Test-Path $path) -and (Get-Item $path).Length -gt 0) {
		$actual = (Get-FileHash -Algorithm SHA256 -Path $path).Hash.ToLowerInvariant()
		if ($actual -ne $expected) {
			$issues += "HASH    $relativePath"
		}
	}
}

if ($issues.Count -gt 0) {
	$issues | ForEach-Object { Write-Warning $_ }
	throw 'External JAR verification failed.'
}

Write-Host 'All required external JARs are present.'
