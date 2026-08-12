# 项目长期记忆

## 万年历 Android App

- 项目路径：C:\GitHub\WanNianLi
- 技术栈：Kotlin + Jetpack Compose + AGP 8.11.1 + Gradle 8.13 + compileSdk 36
- 核心算法在 `app/src/main/java/com/wannianli/LunarCalendar.kt`
- UI 在 `app/src/main/java/com/wannianli/MainActivity.kt`
- 农历数据覆盖 1900-2100 年

## 构建注意事项

- 沙箱环境无法直接运行 Gradle（native-platform.dll 加载/文件权限限制）
- 推荐在 Android Studio 中打开项目后构建
- 已配置 `android.overridePathCheck=true` 以兼容中文用户名路径
- 本机 Gradle 8.13 路径：C:\Users\汪刚\.gradle\wrapper\dists\gradle-8.13-bin\5xuhj0ry160q40clulazy9h7d\gradle-8.13
