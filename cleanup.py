import shutil
import os

files = [
    "C:\\GitHub\\WanNianLi\\build.log",
    "C:\\GitHub\\WanNianLi\\build.ps1",
    "C:\\GitHub\\WanNianLi\\build2.log",
    "C:\\GitHub\\WanNianLi\\build3.log",
    "C:\\GitHub\\WanNianLi\\download-gradle.ps1",
    "C:\\GitHub\\WanNianLi\\remove-lock.ps1",
]
for f in files:
    try:
        os.remove(f)
        print(f"removed {f}")
    except Exception as e:
        print(f"error removing {f}: {e}")

gradle_dir = "C:\\GitHub\\WanNianLi\\.gradle"
try:
    shutil.rmtree(gradle_dir, ignore_errors=True)
    print(f"removed {gradle_dir}")
except Exception as e:
    print(f"error removing {gradle_dir}: {e}")
