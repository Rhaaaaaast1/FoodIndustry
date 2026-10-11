package dev.rhaaaaaast1.foodindustry.common;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.registry.ModBlocks;
import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * 两端共用的初始化。
 *
 * <p>凡是需要操作原版注册表 / 全局单例的动作（发射器行为、村民交易物品等）都必须放进
 * {@code event.enqueueWork(...)}，它们会在正确的加载阶段执行。</p>
 */
public final class CommonSetup {

    public static void init(final FMLCommonSetupEvent event) {
        // 物品提示登记：数据表在 ModTooltips 里，加提示不用动物品注册代码。
        // 放在这里是因为此刻物品已全部注册完毕。
        ModTooltips.registerAll();

        event.enqueueWork(() -> {
            if (Config.LOG_CONTENT_SUMMARY.get()) {
                FoodIndustry.LOGGER.info("[{}] 已注册 {} 个方块 / {} 个物品",
                        FoodIndustry.MODID,
                        ModBlocks.BLOCKS.getEntries().size(),
                        ModItems.ITEMS.getEntries().size());
            }
        });
    }

    private CommonSetup() {
    }
}
