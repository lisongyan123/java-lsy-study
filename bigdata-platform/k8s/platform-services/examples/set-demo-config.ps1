param(
    [Parameter(ParameterSetName = 'Single')]
    [ValidatePattern('^[A-Za-z_][A-Za-z0-9_]*$')]
    [string]$Key = 'FEATURE_X_ENABLED',
    [Parameter(ParameterSetName = 'Single')]
    [string]$Value = 'true',
    [Parameter(Mandatory = $true, ParameterSetName = 'Batch')]
    [string]$ConfigFile,
    [string]$Namespace = 'platform-services',
    [string]$ApiBase = 'http://127.0.0.1:8001',
    [string]$GatewayUrl = 'http://localhost:30080/orders/config'
)

$ErrorActionPreference = 'Stop'

if (-not (Get-Command kubectl -ErrorAction SilentlyContinue)) {
    throw 'kubectl not found in PATH.'
}

try {
    Invoke-RestMethod -Uri "$ApiBase/version" | Out-Null
}
catch {
    throw "Kubernetes API proxy is unavailable at $ApiBase. Start it first with: kubectl proxy --port=8001"
}

$patch = @{ data = @{} }
$expectedPairs = [System.Collections.Generic.List[string]]::new()

if ($PSCmdlet.ParameterSetName -eq 'Batch') {
    if (-not (Test-Path $ConfigFile -PathType Leaf)) {
        throw "Config file was not found: $ConfigFile"
    }

    $batchConfig = Get-Content $ConfigFile -Raw | ConvertFrom-Json
    foreach ($property in $batchConfig.PSObject.Properties) {
        if ($property.Name -notmatch '^[A-Za-z_][A-Za-z0-9_]*$') {
            throw "Invalid config key: $($property.Name). Use letters, digits, and underscores only."
        }

        $patch.data[$property.Name] = [string]$property.Value
        $expectedPairs.Add("$($property.Name)=$($property.Value)")
    }

    if ($patch.data.Count -eq 0) {
        throw 'Config file must contain at least one property.'
    }
}
else {
    $patch.data[$Key] = $Value
    $expectedPairs.Add("$Key=$Value")
}

$patchBody = $patch | ConvertTo-Json -Compress
$configUri = "$ApiBase/api/v1/namespaces/$Namespace/configmaps/platform-config"

Invoke-RestMethod -Uri $configUri -Method Patch -ContentType 'application/merge-patch+json' -Body $patchBody | Out-Null
$config = Invoke-RestMethod -Uri $configUri

Write-Host '== Kubernetes API ConfigMap =='
foreach ($pair in $expectedPairs) {
    $keyName = $pair.Split('=', 2)[0]
    Write-Host "$keyName=$($config.data.$keyName)"
}

kubectl rollout restart deployment/orders-demo -n $Namespace | Out-Host
kubectl rollout status deployment/orders-demo -n $Namespace --timeout=180s | Out-Host

Write-Host '== Gateway API response =='
$deadline = (Get-Date).AddSeconds(30)
$verified = $false
$lastError = $null

do {
    try {
        $response = Invoke-WebRequest -UseBasicParsing $GatewayUrl
        $allExpectedValuesPresent = $true
        foreach ($pair in $expectedPairs) {
            if (-not $response.Content.Contains($pair)) {
                $allExpectedValuesPresent = $false
                break
            }
        }

        if ($response.StatusCode -eq 200 -and $allExpectedValuesPresent) {
            $response.Content | Out-Host
            $verified = $true
            break
        }
    }
    catch {
        $lastError = $_
    }

    Start-Sleep -Milliseconds 500
}
while ((Get-Date) -lt $deadline)

if ($verified) {
    return
}

if ($lastError) {
    throw "Gateway did not become ready within 30 seconds: $($lastError.Exception.Message)"
}

if ($response.StatusCode -ne 200) {
    throw "Gateway returned HTTP $($response.StatusCode)."
}

throw "Gateway did not return all expected updated values: $($expectedPairs -join ', ')"
