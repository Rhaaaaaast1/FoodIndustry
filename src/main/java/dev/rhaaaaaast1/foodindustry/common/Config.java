package dev.rhaaaaaast1.foodindustry.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 模组配置。
 *
 * <p>用两个独立的 {@link ModConfigSpec.Builder} 分别产出 COMMON 与 CLIENT 规格，
 * 在主类里通过 {@code ModContainer#registerConfig} 注册。</p>
 */
public final class Config {

    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    // ------------------------------------------------------------------ COMMON

    /**
     * 当检测到其它模组已经提供了同名内容（作物/物品等）时，是否自动停用本模组对应的那一份。
     *
     * <p>注册本身无法被条件化（NeoForge 不支持条件注册），因此这个开关的作用是：
     * 停掉对应的配方、战利品与世界生成，并在创造模式标签里隐藏它。</p>
     */
    public static final ModConfigSpec.BooleanValue AUTO_DISABLE_CONFLICTING_CONTENT = COMMON_BUILDER
            .comment("检测到其它模组已提供同名内容时，自动停用本模组的对应内容（配方/掉落/世界生成/标签页）")
            .translation("foodindustry.configuration.autoDisableConflictingContent")
            .define("compat.autoDisableConflictingContent", true);

    /** 是否在启动日志里打印本模组注册了多少方块/物品，便于排查注册问题。 */
    public static final ModConfigSpec.BooleanValue LOG_CONTENT_SUMMARY = COMMON_BUILDER
            .comment("启动时在日志里打印方块/物品注册数量")
            .translation("foodindustry.configuration.logContentSummary")
            .define("debug.logContentSummary", false);

    // ------------------------------------------------------------------ 粪便

    /**
     * 动物每隔多少 **tick** 产一次粪便。默认 6000 tick = **5 分钟**。
     *
     * <p>换算：20 tick = 1 秒，所以 1200 = 1 分钟、6000 = 5 分钟、12000 = 10 分钟。</p>
     *
     * <p>哪些动物会产由实体类型标签 {@code foodindustry:manure_producers} 决定
     * （默认是牛/猪/羊/鸡等常见家畜），可用数据包增删，不必改配置。</p>
     */
    public static final ModConfigSpec.IntValue MANURE_INTERVAL_TICKS = COMMON_BUILDER
            .comment("动物每隔多少 tick 产一次粪便（20 tick = 1 秒；6000 = 5 分钟）")
            .translation("foodindustry.configuration.manureIntervalTicks")
            .defineInRange("animals.manureIntervalTicks", 6000, 200, 240000);

    /** 被粪便砸中后反胃多少秒。默认 10 秒。 */
    public static final ModConfigSpec.IntValue MANURE_NAUSEA_SECONDS = COMMON_BUILDER
            .comment("被粪便砸中后反胃的秒数")
            .translation("foodindustry.configuration.manureNauseaSeconds")
            .defineInRange("animals.manureNauseaSeconds", 10, 1, 300);

    public static final ModConfigSpec COMMON_SPEC = COMMON_BUILDER.build();

    // ------------------------------------------------------------------ CLIENT

    /** 被兼容开关停用的物品，是否在提示里说明原因。 */
    public static final ModConfigSpec.BooleanValue SHOW_DISABLED_TOOLTIP = CLIENT_BUILDER
            .comment("被兼容开关停用的内容，是否在物品提示里说明原因")
            .translation("foodindustry.configuration.showDisabledTooltip")
            .define("tooltip.showDisabledTooltip", true);

    public static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

    private Config() {
    }
}
