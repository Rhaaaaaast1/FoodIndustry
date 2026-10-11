package dev.rhaaaaaast1.foodindustry.client;

import dev.rhaaaaaast1.foodindustry.common.Config;
import dev.rhaaaaaast1.foodindustry.common.ModTooltips;
import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * 物品提示（tooltip）渲染处理器 —— **通用**：它自己不认识任何具体物品。
 *
 * <p>要做的事只有两件：</p>
 * <ol>
 *   <li>把 {@link ModTooltips} 登记表里属于这件物品的提示行追加进去；</li>
 *   <li>如果这件物品因缺依赖被停用，说明原因。</li>
 * </ol>
 *
 * <p>所以以后加提示**不需要动这个文件**，只改 {@code ModTooltips.registerAll()}。</p>
 *
 * <p>只注册在物理客户端（见 {@link FoodIndustryClient#init}）：
 * {@code Config.SHOW_DISABLED_TOOLTIP} 是 CLIENT 配置，在专用服务器上读取会抛
 * "配置未加载"；而这个事件本身也只发生在客户端。</p>
 */
public final class ItemTooltipHandler {

    /** 挂在游戏事件总线（不是 mod 事件总线）上。 */
    public static void register() {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ItemTooltipHandler::onItemTooltip);
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        // ---- ① 登记表里的提示（士腻架等；数据在 common.ModTooltips） ----
        event.getToolTip().addAll(ModTooltips.linesFor(event.getItemStack().getItem()));

        // ---- ② 被兼容开关停用的内容：说明为什么拿不到/用不了 ----
        // 判定与创造标签过滤共用 ModItems.isUnavailable，两处结论永远一致。
        if (Config.SHOW_DISABLED_TOOLTIP.get() && ModItems.isUnavailable(event.getItemStack())) {
            event.getToolTip().add(Component.translatable(ModItems.unavailableReasonKey())
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }

    private ItemTooltipHandler() {
    }
}
