$url = 'https://services.gradle.org/distributions/gradle-8.13-bin.zip'
$out = 'C:\GradleHome\gradle-8.13-bin.zip'
$dir = 'C:\GradleHome'
New-Item -ItemType Directory -Force -Path $dir | Out-Null
Invoke-WebRequest -Uri $url -OutFile $out -TimeoutSec 300
Expand-Archive -Path $out -DestinationPath $dir -Force
