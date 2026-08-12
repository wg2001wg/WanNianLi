$dir = 'C:\GradleHome\wrapper\dists\gradle-8.13-bin\5xuhj0ry160q40clulazy9h7d'
Remove-Item -Path "$dir\gradle-8.13-bin.zip.lck" -Force -ErrorAction SilentlyContinue
Remove-Item -Path "$dir\gradle-8.13-bin.zip.part" -Force -ErrorAction SilentlyContinue
Get-ChildItem $dir
