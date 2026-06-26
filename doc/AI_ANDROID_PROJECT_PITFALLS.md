# 新开 Android 项目防踩坑指南（给 AI 用）

> 本文件记录本项目从零到跑通过程中踩过的坑，供后续 AI 助手或开发者参考。
> 适用场景：Android 原生 + Kotlin + Jetpack Compose 的新项目初始化。

---

## 1. 版本栈必须一次性选对

### 坑
生成项目时工具默认推荐最新版（如 AGP 9.2.1 + Gradle 9.4.1 + JDK 21），但本地环境、依赖库、网络条件可能不支持，导致反复升降级。

### 推荐组合（本项目当前稳定栈）

| 组件 | 版本 | 备注 |
|------|------|------|
| AGP | `8.9.0` | 兼容 JDK 17，仍支持 kapt |
| Gradle | `8.11.1` | 与 AGP 8.9 匹配 |
| Kotlin | `2.1.20` | 可用 `kotlin.compose` 插件 |
| JDK | `17` | Android Studio 内置常见版本 |
| compileSdk | `36` | AGP 8.9 会有 warning，但可编译 |

### 检查清单
- [ ] 先确认本地默认 JDK 版本：`java -version`
- [ ] AGP 版本与 Gradle 版本匹配：参考 [Gradle-Android 兼容表](https://developer.android.com/studio/releases/gradle-plugin#updating-gradle)
- [ ] AGP 9.x 需要 JDK 21，且**不再支持 kapt**

---

## 2. 必须应用 Kotlin Android 插件

### 坑
只写 `alias(libs.plugins.kotlin.compose)` 看起来能 Sync，但 Kotlin 源文件**不会被编译进 APK**，运行时报：

```
java.lang.ClassNotFoundException: com.xxx.MainActivity
```

### 正确写法

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.android")
}
```

### 解释
- `kotlin.compose`：配置 Compose 编译器
- `org.jetbrains.kotlin.android`：实际编译 `.kt` 文件
- 两者缺一不可

---

## 3. kapt vs KSP vs 不用注解处理

### 坑
AGP 9.x 开始**移除 kapt 支持**。如果项目用了 Room、Dagger/Hilt 等依赖 kapt 的库，要么：
1. 降级 AGP 到 8.x
2. 把 kapt 迁移到 KSP
3. 彻底不用需要注解处理的库

### 本项目选择
为简化环境，**放弃 Room**，改用 `SharedPreferences + Gson`。

### 决策建议

| 场景 | 推荐方案 |
|------|---------|
| 快速 MVP、数据量小 | SharedPreferences / DataStore + Gson |
| 正式项目、复杂查询 | Room + KSP（AGP 8.x/9.x 都可用） |
| 旧项目升级 AGP 9.x | 必须 kapt → KSP 迁移 |

---

## 4. Gradle 镜像配置

### 坑
`services.gradle.org` 在国内访问慢或失败，Gradle wrapper 下载卡住。

### 配置阿里云镜像

`gradle/wrapper/gradle-wrapper.properties`：

```properties
distributionUrl=https\://mirrors.aliyun.com/gradle/distributions/v8.11.1/gradle-8.11.1-bin.zip
```

`settings.gradle.kts` 里也要把 plugin 仓库改成国内镜像：

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

---

## 5. Android Studio 的 "Download sources" 必须关掉

### 坑
即使 `gradle-wrapper.properties` 配置了阿里云，`gradle-xxx-src.zip` 源码包仍会从 `services.gradle.org` 下载，因为这个走 IDE 自己的设置，不走项目配置。

### 关闭路径

```
Settings → Build, Execution, Deployment → Build Tools → Gradle → Advanced Settings
→ 取消勾选 "Download sources"
```

### 已下载的残留缓存处理
如果已经部分下载导致损坏，删除对应目录：

```bash
rm -rf ~/.gradle/wrapper/dists/gradle-*-src
```

---

## 6. Sync 通过 ≠ Build 通过 ≠ Run 能跑

### 坑
很多人（包括 AI）Sync 一通过就觉得没问题了，其实三个阶段检查的东西不同：

| 阶段 | 检查内容 | 能发现的问题 |
|------|---------|-------------|
| Sync | Gradle 脚本、依赖解析、插件应用 | 版本不匹配、依赖拉不到 |
| Build | Kotlin/Java 编译、资源合并、Dex | 代码语法错误、API 不兼容 |
| Run | 安装到设备、运行时权限、生命周期 | 权限缺失、空指针、ClassNotFound |

### 建议流程
1. Sync 通过后，**立刻 `./gradlew :app:assembleDebug`**
2. Build 通过后，**再点 Run**
3. 不要只在 IDE 里反复 Sync

---

## 7. 权限不要忘了加

### 坑
代码里调用了系统功能，但 `AndroidManifest.xml` 没声明权限，运行时直接 SecurityException 闪退。

### 本项目案例
点击红心时调用震动：

```kotlin
vibrator.vibrate(...)
```

必须加权限：

```xml
<uses-permission android:name="android.permission.VIBRATE" />
```

### 常见易漏权限
- `VIBRATE` — 震动
- `INTERNET` — 网络请求
- `POST_NOTIFICATIONS` — Android 13+ 通知
- `READ_MEDIA_*` / `READ_EXTERNAL_STORAGE` — 读取文件

---

## 8. Compose 版本与 API 兼容性

### 坑
不同 Compose BOM 版本里，某些 API 的 import 或参数会变化。

### 本项目案例
- `InfiniteTransition.animateFloat()` 是扩展函数，必须显式 import：
  ```kotlin
  import androidx.compose.animation.core.animateFloat
  ```
- `LazyColumn` 的 `items(key = ...)` 引用的字段必须真实存在

### 建议
升级 Compose BOM 后，先全量 Build 一遍，把 `Unresolved reference` 逐个修掉。

---

## 9. 数据类改动要全局检查

### 坑
修改了 `data class` 的字段（如去掉 `id`），但 Preview、列表 key、测试数据里还在用旧字段。

### 本项目案例
`CheckInRecord` 从带 `id` 改成只有 `date/timestamp/microLight/customText`，但 `HistoryScreen.kt` 里：
- `key = { it.id }` 没改
- Preview 里 `id = 1` 没删

### 建议
修改核心数据类后，全局搜索该数据类名，检查所有构造调用处。

---

## 10. 中文字符串写入时的编码问题

### 坑
通过工具批量生成代码时，中文引号可能被写成乱码或重复引号，如：

```kotlin
// 错误
 text = ""断签不是失败...""
```

### 建议
生成含中文的代码后，用 IDE 打开检查一遍字符串字面量，确保是标准 UTF-8 编码。

---

## 11. 本地 JDK 路径与命令行构建

### 坑
命令行 `./gradlew` 找不到 JDK，报错 `JAVA_HOME is not set`。

### 临时解决
```bash
export JAVA_HOME=/c/Users/你的用户名/.jdks/jbr-17.0.14
export PATH=$JAVA_HOME/bin:$PATH
./gradlew :app:assembleDebug
```

### 持久化解决
在 `gradle.properties` 里加：

```properties
org.gradle.java.home=C:\\Users\\你的用户名\\.jdks\\jbr-17.0.14
```

---

## 12. 不要信任"最新版一定最好"

### 原则
- 优先匹配用户本地已有环境
- 优先选择社区成熟、文档完整的版本组合
- 新项目不要同时踩多个新特性的坑（如 AGP 9.x + Kotlin 2.x + Room kapt）

---

## 快速检查表（每次开新项目时过一遍）

- [ ] AGP / Gradle / JDK 版本匹配
- [ ] 已应用 `org.jetbrains.kotlin.android`
- [ ] 已应用 `kotlin.compose`（如果用 Compose）
- [ ] 确认存储方案：SharedPreferences / DataStore / Room + KSP
- [ ] Gradle wrapper 使用国内镜像
- [ ] Android Studio 关闭 Download sources
- [ ] `settings.gradle.kts` 配置了国内 Maven 镜像
- [ ] 所有运行时权限已在 `AndroidManifest.xml` 声明
- [ ] 修改数据类后全局检查调用处
- [ ] Sync 后执行一次 `./gradlew :app:assembleDebug`
