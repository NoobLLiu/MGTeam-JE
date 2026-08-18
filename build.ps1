$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$devPluginsRoot = Split-Path -Parent $projectRoot
$workspaceRoot = Split-Path -Parent $devPluginsRoot
$serverRoot = Join-Path $workspaceRoot 'StarCIty'
$devRoot = Join-Path $workspaceRoot 'dev'
$javaHome = Join-Path $serverRoot 'runtime\jdk25\jdk-25.0.3'
$buildRoot = Join-Path $projectRoot 'build'
$pluginOut = Join-Path $buildRoot 'plugin-classes'
$jarPath = Join-Path $buildRoot 'MGTeam-1.0.0.jar'

$paperApi = Join-Path $serverRoot 'libraries\io\papermc\paper\paper-api\1.21.11-R0.1-SNAPSHOT\paper-api-1.21.11-R0.1-SNAPSHOT.jar'
$pluginDirectory = Join-Path $serverRoot 'plugins'
function Get-PluginJar([string]$pattern) {
    $match = Get-ChildItem -LiteralPath $pluginDirectory -File -Filter $pattern |
        Select-Object -First 1
    if (-not $match) {
        throw "Plugin dependency not found: $pattern"
    }
    return $match.FullName
}
$floodgateJar = Get-PluginJar '*Floodgate-Spigot.jar'
$geyserJar = Get-PluginJar '*Geyser-Spigot.jar'
$vaultJar = Get-PluginJar '*Vault.jar'
$placeholderJar = Get-PluginJar '*PlaceholderAPI-2.12.2.jar'
$essentialsJar = Get-PluginJar '*EssentialsX-2.22.0.jar'
$skinCacheJar = Join-Path $devRoot 'local-plugins\gmzc-skin-cache\build\GMZCSkinCache-1.0.0.jar'
$titlesJar = Join-Path $devRoot 'local-plugins\title-system\build\GMZCTitles-1.0.0.jar'

$baseClassPath = "$paperApi;$floodgateJar;$geyserJar;$vaultJar;$skinCacheJar;$titlesJar"
if (Test-Path -LiteralPath $placeholderJar) {
    $baseClassPath = "$baseClassPath;$placeholderJar"
}
if (Test-Path -LiteralPath $essentialsJar) {
    $baseClassPath = "$baseClassPath;$essentialsJar"
}

$libraryJars = Get-ChildItem -LiteralPath (Join-Path $serverRoot 'libraries') -Recurse -Filter '*.jar' -ErrorAction SilentlyContinue | ForEach-Object { $_.FullName }
$compileClassPath = "$baseClassPath;$($libraryJars -join ';')"
foreach ($path in @($pluginOut)) {
    if (Test-Path -LiteralPath $path) {
        Remove-Item -LiteralPath $path -Recurse -Force
    }
    New-Item -ItemType Directory -Path $path | Out-Null
}

$sources = Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src') -Recurse -Filter *.java |
    Where-Object { $_.Name -ne 'FundConsumeManager.java' } |
    ForEach-Object FullName
if ($sources.Count -gt 0) {
    & (Join-Path $javaHome 'bin\javac.exe') -encoding UTF-8 -proc:none -cp $compileClassPath -d $pluginOut $sources
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}

Copy-Item -LiteralPath (Join-Path $projectRoot 'plugin.yml') -Destination $pluginOut
Copy-Item -LiteralPath (Join-Path $projectRoot 'config.yml') -Destination $pluginOut

if (Test-Path -LiteralPath $jarPath) {
    Remove-Item -LiteralPath $jarPath -Force
}
& (Join-Path $javaHome 'bin\jar.exe') --create --file $jarPath -C $pluginOut .
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

Write-Host "Built $jarPath"
