# Time Battle / 时间管理局 — 项目协作指南

> 本文件汇总项目目标、技术栈、设计规范与已知的 Android 工程陷阱，供 AI 助手与开发者协作时遵循。
> 来源：`doc/code-best.html`（设计方案） + `doc/AI_ANDROID_PROJECT_PITFALLS.md`（工程踩坑记录）。
>
> **状态**：Phase 01 核心页面已实现，新增设置页、联盟占位页与 ViewModel 单元测试，可通过 `./gradlew :app:assembleDebug` 编译且 `./gradlew :app:testDebugUnitTest` 全部通过。

---

## 1. 项目概述

- **产品名**：时间管理局（Time Battle）
- **灵感**：电影 *In Time*（时间即生命，每一秒都在消耗）。
- **形态**：Android 原生应用，单模块 `:app`。
- **核心循环**：登录获得初始生命时长 → 实时倒计时消耗 → 通过系统任务 / 玩家悬赏 / 交易 / 联盟获取时间 → 时间归零进入终局。
- **开发阶段**：当前处于 Phase 01（核心机制：登录、计时、基础 UI、系统任务）。

---

## 2. 技术栈（固定，不可随意升级）

为匹配本地环境、避免反复升降级，使用以下稳定组合：

| 组件 | 版本 | 备注 |
|------|------|------|
| AGP | `8.9.0` | 兼容 JDK 17；仍支持 kapt |
| Gradle | `8.11.1` | 与 AGP 8.9 匹配 |
| Kotlin | `2.1.20` | 可用 `kotlin.compose` 插件 |
| JDK | `17` | Android Studio 内置常见版本 |
| compileSdk | `36` | 已配置 `android.suppressUnsupportedCompileSdk=36` |
| minSdk | `24` | 兼容 Android 7.0 |
| targetSdk | `36` | 与 compileSdk 一致 |

### 依赖版本（已按 AGP 8.9 兼容调整）

| 依赖 | 版本 |
|------|------|
| `androidx.core:core-ktx` | `1.15.0` |
| `androidx.lifecycle:lifecycle-runtime-ktx` | `2.8.7` |
| `androidx.activity:activity-compose` | `1.9.3` |
| Compose BOM | `2024.12.01` |
| `androidx.test.ext:junit` | `1.2.1` |
| `androidx.test.espresso:espresso-core` | `3.6.1` |
| `org.robolectric:robolectric` | `4.12.2` | 本地 JVM 单元测试用 |
| `org.jetbrains.kotlinx:kotlinx-coroutines-test` | `1.9.0` | 协程测试 |
| `androidx.test:core` | `1.6.1` | `ApplicationProvider` 用 |

### 关键插件配置

`app/build.gradle.kts` 必须同时应用以下三个插件，缺一不可：

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.android")   // 负责实际编译 .kt
}
```

说明：
- `com.android.application`：Android 构建
- `org.jetbrains.kotlin.plugin.compose`：Compose 编译器
- `org.jetbrains.kotlin.android`：编译 Kotlin 源文件（缺它会在运行时 `ClassNotFoundException`）

---

## 3. 数据存储方案

为简化环境、避免注解处理与 kapt/KSP 配置，**放弃 Room**。

- **本地持久化**：`SharedPreferences` / `DataStore` + `Gson`
- **数据结构**：简单 data class，序列化后存 JSON 字符串
- **适用场景**：用户设置、任务列表、签到记录、玩家悬赏等轻量数据

---

## 4. Gradle / Maven 镜像配置（必须）

### 4.1 Wrapper 镜像

`gradle/wrapper/gradle-wrapper.properties`：

```properties
distributionUrl=https\://mirrors.aliyun.com/gradle/distributions/v8.11.1/gradle-8.11.1-bin.zip
```

### 4.2 插件仓库镜像

`settings.gradle.kts`：

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    }
}
```

### 4.3 依赖仓库镜像

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
    }
}
```

### 4.4 Gradle 属性

`gradle.properties`：

```properties
org.gradle.java.home=C:\\Users\\Moky\\.jdks\\jbr-17.0.14
android.useAndroidX=true
android.suppressUnsupportedCompileSdk=36
```

### 4.5 Android Studio 设置

关闭 `Download sources`：

```
Settings → Build, Execution, Deployment → Build Tools → Gradle → Advanced Settings
→ 取消勾选 "Download sources"
```

---

## 5. 验证流程

不要只点 Sync。每次修改 Gradle / 依赖 / 核心代码后，按以下顺序验证：

1. `Sync Project with Gradle Files`（或 `./gradlew :app:dependencies --configuration implementation`）
2. `./gradlew :app:assembleDebug`（必须编译通过）
3. `./gradlew :app:testDebugUnitTest`（必须全部通过）
4. 最后才 Run / 安装到设备

---

## 6. 设计规范

### 6.1 色彩

| 名称 | 色值 | 用途 |
|------|------|------|
| 深渊黑 | `#090909` | 页面背景 |
| 碳灰 | `#0F0F0F` | 卡片 / 表面 |
| 深灰 | `#161616` | 次级表面 |
| 生命红 | `#C0392B` | 主强调色、倒计时、按钮激活 |
| 暗红 | `#7B241C` | 边框、弱化强调 |
| 暖白 | `#ECE7E0` | 主文字 |
| 灰白 | `#7A7570` | 次级文字 |

### 6.2 字体

- **标题 / 品牌**：衬线体（Android 上可用 `Noto Serif SC` 或系统衬线 fallback）
- **正文 / UI**：无衬线体（`Inter` 或系统默认 sans-serif）
- **倒计时 / 数据**：等宽体（`JetBrains Mono` 或 `Roboto Mono`）

### 6.3 图标

- 全部使用 **SVG 线条图标**
- 线宽：`1.25dp` / `1.5dp`
- 圆角端点（`stroke-linecap="round"`）
- 无填充色块

### 6.4 组件原子

- Buttons：Primary（红底白字）、Outline（红边框红字）、Ghost（灰边框灰字）
- Inputs：透明底 + 细边框 + 聚焦红边框
- Chips / Tags：圆角胶囊，激活态红底白字
- Task Card：标签 + 标题 + 描述 + 奖励时间 + 操作按钮
- Toast：左侧强调线 + 图标 + 文案

---

## 7. 核心页面

当前阶段已实现：

1. **S-01 登录** — 品牌 Logo、授权登录、注册入口
2. **S-02 主页** — 顶部状态栏、生命倒计时、快捷操作、进行中的任务、底部导航
3. **S-03 任务中心** — 返回栏、筛选标签、任务卡片列表、底部导航
4. **S-04 终局** — 时间归零提示、存活统计、重新开始按钮
5. **交易页** — 时间挂单购买 / 出售
6. **排行榜页** — 本地 Mock 玩家排名
7. **通知页** — 应用内通知列表
8. **我的页** — 统计、排行榜入口、设置入口
9. **设置页** — 震动开关、清除缓存、退出登录、重新开始
10. **联盟占位页** — Phase 02 预览占位

> 初始生命时长已调整为 **7 天**（`7 * 24 * 3600` 秒），以匹配核心循环紧迫感。

---

## 8. 运行时权限

`AndroidManifest.xml` 按需声明：

```xml
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

## 9. 编码规范

- 使用 **Kotlin** + **Jetpack Compose**
- Compose 版本以 `libs.versions.toml` 中 BOM 为准
- 修改 `data class` 字段后，全局搜索该类名，同步更新 Preview / 列表 key / 测试数据
- 含中文字符串时，确保文件为 UTF-8 编码，避免引号乱码
- 尽量保持最小改动（Minimal change），先跑通再扩展

---

## 10. 常见陷阱速查

- AGP 9.x 需要 JDK 21 且不再支持 kapt → **本项目固定 8.9.0**
- 只应用 `kotlin.compose` 不应用 `kotlin.android` → 运行时 `ClassNotFoundException`
- 配置镜像后仍从 `services.gradle.org` 下载 → 检查 Android Studio `Download sources`
- Sync 通过 ≠ Build 通过 ≠ Run 能跑 → 必须执行 `./gradlew :app:assembleDebug`

---

## 11. 推荐命令

```bash
# 查看 JDK 版本
java -version

# 编译
./gradlew :app:assembleDebug

# 单元测试
./gradlew :app:testDebugUnitTest

# 清理
./gradlew clean

# 查看依赖
./gradlew :app:dependencies --configuration implementation
```

---

*最后更新：2026-06-26（同步本次设置页、联盟占位页、单元测试与 7 天初始生命时长改动）*
