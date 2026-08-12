# 万年日历（WanNianLi）

一个基于 Android Studio + Kotlin + Jetpack Compose 实现的万年历手机 App。

## 功能

- 公历月份网格展示，支持年份/月份切换
- 点击日期查看详细信息
- 显示农历日期、天干地支、生肖、节气、节假日
- 显示宜忌、彭祖百忌、胎神占方、五行纳音、二十八宿
- 显示星座、佛历年、伊斯兰历、冲煞、六曜、十二建神

## 技术栈

- Android SDK 36
- Kotlin 2.0.21
- Jetpack Compose
- Gradle 8.13
- AGP 8.11.1

## 项目结构

```
C:\GitHub\WanNianLi
├── app/src/main/java/com/wannianli/
│   ├── MainActivity.kt        # 主界面（Compose UI）
│   ├── LunarCalendar.kt       # 农历、干支、节气等核心算法
│   └── ui/theme/              # Compose 主题
├── app/src/main/res/          # 资源文件
├── build.gradle
├── app/build.gradle
├── settings.gradle
└── gradle.properties
```

## 构建说明

1. 确保已安装 Android Studio 及 Android SDK。
2. 在 Android Studio 中打开本项目（`C:\GitHub\WanNianLi`）。
3. 同步 Gradle，然后选择 `Build > Build Bundle(s) / APK(s) > Build APK(s)`。
4. 生成的 APK 位于 `app/build/outputs/apk/debug/app-debug.apk`。

## 运行环境

- minSdk：24
- targetSdk / compileSdk：36

## 注意事项

- 本机 Gradle 构建可能受沙箱网络/权限限制，推荐在 Android Studio 中直接构建。
- 农历数据覆盖 1900-2100 年。
