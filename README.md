# FoodIndustry

以 [Farmer's Delight](https://github.com/vectorwing/FarmersDelight) 为前置的农业与烹饪扩展模组。

| 项目 | 值 |
|---|---|
| Minecraft | 1.21.1 |
| 加载器 | NeoForge 21.1.256 |
| 硬前置 | Farmer's Delight 1.21.1-1.3.4 及以上 |
| 软兼容 | JEI 19.57+（配方界面）、Jade 15.10+（方块信息） |
| 构建 | ModDevGradle 2.0.148 + Gradle 8.8 + Java 21 |
| 许可证 | MIT（见 `LICENSE`） |

## 目录结构

```
src/main/java/dev/rhaaaaaast1/foodindustry/
├── FoodIndustry.java              主入口：注册配置、分流客户端、挂载所有 DeferredRegister
├── common/
│   ├── Config.java                ModConfigSpec：COMMON / CLIENT 两份配置
│   ├── CommonSetup.java           两端共用初始化（enqueueWork）
│   └── registry/                  注册表：ModBlocks / ModItems / ModEffects / ModCreativeTabs
├── client/
│   └── FoodIndustryClient.java    客户端专属初始化（只允许在物理客户端加载）
├── data/                          数据生成（datagen）
│   ├── FoodIndustryDataGenerators 入口，挂到 mod 事件总线
│   ├── ModBlockStateProvider      blockstate / 方块模型
│   ├── ModItemModelProvider       物品模型
│   ├── ModRecipeProvider          配方
│   ├── ModBlockLootTables         方块战利品表
│   ├── ModBlockTagsProvider       方块标签
│   └── ModItemTagsProvider        物品标签
└── mixin/                         Mixin 包（当前为空，不需要改原版）
```

资源：`src/main/resources`（手写资源）+ `src/generated/resources`（datagen 产物，已加入源集）。

## 开发命令

```bash
./gradlew compileJava    # 只编译，最快的语法检查
./gradlew build          # 构建
./gradlew runClient      # 启动带模组的客户端
./gradlew runData        # 生成数据到 src/generated/resources
./gradlew runGameServer  # 启动带模组的服务端（runServer）
./gradlew runGameTestServer   # 跑游戏内测试
./gradlew syncDevMods    # 手动同步 run/mods（正常跑 run* 任务会自动执行）
```

## 添加内容的约定

- **新方块**：`ModBlocks.BLOCKS.registerBlock(...)` + 在 `ModItems` 里注册对应的 BlockItem，
  然后在 `ModBlockStateProvider` / `ModBlockLootTables` / `ModBlockTagsProvider` 里补生成。
- **新物品**：用 `ModItems.registerWithTab("名字", Item::new)` 注册（自动进入本模组的创造标签），
  或 `registerHidden(...)`（不进标签页），再在 `ModItemModelProvider` 里补模型。
- **进创造标签**：只要用了 `registerWithTab` 就会自动出现在标签里，不需要改标签代码。
- **新标签**：标签常量请**自己声明**，不要直接引用前置模组的 `TagKey` 常量（Farmer's Delight
  官方也要求 add-on 复制这些 key）。

## 依赖处理（容易踩坑，务必看这段）

前置与软兼容依赖都**不会**被打进发布产物（玩家自行安装）。规则是：

| 配置 | 用途 |
|---|---|
| `compileOnly` | 只为了编译期能引用对方的类。不进 POM、不进发布 jar。 |
| `devMods` | 开发运行时需要的模组 jar，由 `syncDevMods` 任务复制到 `run/mods/`。 |

**为什么运行期必须走 `run/mods`：** ModDevGradle 的 dev 运行里，FML 只把 `run/mods`
里的 jar 当作 mod，放在 classpath 上的 jar 只会被当成普通库 —— 表现为
`Currently, farmersdelight is not installed`。
而如果同一个模组**同时**出现在 classpath 和 `run/mods`，又会因为模块重复而崩：

```
java.lang.module.ResolutionException: Module farmersdelight contains package ...,
module farmers.delight exports package ... to farmersdelight
```

所以：**依赖只写 `compileOnly`，运行期统一交给 `run/mods`**（`syncDevMods` 会在每次
`run*` 前自动同步，并先清掉自己管理的旧 jar）。

- 前置 Farmer's Delight 走 Modrinth Maven（`maven.modrinth:farmers-delight:...`）。
  离线时也可把 jar 放进 `libs/` 并启用 `build.gradle` 里注释掉的备选两行。
- 运行时缺少前置会在 `neoforge.mods.toml` 的 `[[dependencies]]` 处直接报错，这是预期行为。

## 本机环境注意（非项目通用）

- 需要 **JDK 21**；`build.gradle` 已声明 toolchain 21，`gradle.properties` 指定了
  `org.gradle.java.installations.paths=E:/jdk/jdk21`。
- 本机走本地代理上网，而 Java/Gradle 默认不读系统代理，因此代理配置写在
  **用户级** `~/.gradle/gradle.properties`（`systemProp.https.proxyHost` 等），不在本工程内。

## 致谢与出处

- 前置：Farmer's Delight（MIT）。
- 设计阶段参考了多个 Farmer's Delight 附属模组的**做法**（CollectorsReap、Delightful、
  BrewinAndChewin、MyNethersDelight、End's Delight 等）。**代码全部自行实现，不复制他人源码；
  贴图/模型/语言等美术资源全部自产**。
