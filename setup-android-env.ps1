Write-Host "=================================="
Write-Host "  ANDROID CLI INSTALLER (NO IDE)  "
Write-Host "=================================="

Write-Host "1. Installing Java JDK 17 (Microsoft OpenJDK)..."
winget install --id Microsoft.OpenJDK.17 --accept-package-agreements --accept-source-agreements

Write-Host "2. Downloading Android SDK Command Line Tools..."
$androidHome = "$env:LOCALAPPDATA\Android\Sdk"
$sdkZip = "$env:TEMP\cmdline-tools.zip"
# Fetch latest command line tools from Google
Invoke-WebRequest -Uri "https://dl.google.com/android/repository/commandlinetools-win-11479570_latest.zip" -OutFile $sdkZip

Write-Host "3. Extracting SDK to: $androidHome"
if (!(Test-Path $androidHome)) { New-Item -ItemType Directory -Path $androidHome -Force | Out-Null }
$cmdLineToolsPath = "$androidHome\cmdline-tools\latest"
if (!(Test-Path $cmdLineToolsPath)) { New-Item -ItemType Directory -Path $cmdLineToolsPath -Force | Out-Null }

Expand-Archive -Path $sdkZip -DestinationPath "$env:TEMP\cmdline-tools-extract" -Force
Copy-Item -Path "$env:TEMP\cmdline-tools-extract\cmdline-tools\*" -Destination $cmdLineToolsPath -Recurse -Force
Remove-Item $sdkZip -Force
Remove-Item "$env:TEMP\cmdline-tools-extract" -Recurse -Force

Write-Host "4. Setting Environment Variables..."
[Environment]::SetEnvironmentVariable("ANDROID_HOME", $androidHome, "User")
$userPath = [Environment]::GetEnvironmentVariable("Path", "User")
if ($userPath -notmatch "cmdline-tools\\latest\\bin") {
    $newUserPath = $userPath + ";$cmdLineToolsPath\bin;$androidHome\platform-tools"
    [Environment]::SetEnvironmentVariable("Path", $newUserPath, "User")
}

Write-Host ""
Write-Host "DONE! PLEASE DO THE FOLLOWING TO FINISH:"
Write-Host "1. Restart VS Code Terminal (Click Trash icon and open new terminal)"
Write-Host "2. Run this to accept Android SDK licenses (type y to all prompts):"
Write-Host "   sdkmanager --licenses"
Write-Host "3. Run this to download the compiler tools:"
Write-Host "   sdkmanager `"platforms;android-34`" `"build-tools;34.0.0`" `"platform-tools`""
Write-Host "=================================="
