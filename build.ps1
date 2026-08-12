$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
$env:ANDROID_HOME="C:\Users\汪刚\AppData\Local\Android\Sdk"
$env:GRADLE_USER_HOME="C:\GradleHome"
Set-Location "C:\GitHub\WanNianLi"
& "C:\Users\汪刚\.gradle\wrapper\dists\gradle-8.13-bin\5xuhj0ry160q40clulazy9h7d\gradle-8.13\bin\gradle.bat" assembleDebug --no-daemon 2>&1 | Tee-Object -FilePath "C:\GitHub\WanNianLi\build.log" -Append
