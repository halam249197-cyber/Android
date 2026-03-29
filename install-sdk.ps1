$dl = "https://dl.google.com/android/repository/commandlinetools-win-11479570_latest.zip"
$zip = "$env:TEMP\c.zip"
Write-Host "1. Tải Android SDK..."
Invoke-WebRequest -Uri $dl -OutFile $zip
$sdk = "$env:LOCALAPPDATA\Android\Sdk"
$tmp = "$env:TEMP\c_ex"
Write-Host "2. Giải nén vào $sdk..."
Expand-Archive -Path $zip -DestinationPath $tmp -Force
if (!(Test-Path "$sdk\cmdline-tools\latest")) { New-Item -ItemType Directory -Force -Path "$sdk\cmdline-tools\latest" | Out-Null }
Copy-Item -Path "$tmp\cmdline-tools\*" -Destination "$sdk\cmdline-tools\latest\" -Recurse -Force
Write-Host "3. Cấu hình PATH..."
[Environment]::SetEnvironmentVariable("ANDROID_HOME", $sdk, "User")
$newPath = [Environment]::GetEnvironmentVariable("Path", "User")
if ($newPath -notmatch "cmdline-tools\\latest\\bin") {
    $newPath = $newPath + ";$sdk\cmdline-tools\latest\bin;$sdk\platform-tools"
    [Environment]::SetEnvironmentVariable("Path", $newPath, "User")
}
Write-Host "HOÀN TẤT."
