package dev.rhaaaaaast1.foodindustry.common;

import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 物品提示（tooltip）登记表。
 *
 * <p><b>用法：加新提示只要在这里加一行，物品注册代码和事件处理器都不用动。</b></p>
 *
 * <pre>{@code
 * register(ModItems.SLICKERS.get(), "item.foodindustry.slickers.tooltip", ChatFormatting.GOLD);
 * register(ModItems.RAW_COFFEE_BEANS.get(), "tooltip.foodindustry.coffee.ferment"); // 再来一条就这样
 * }</pre>
 *
 * <p>为什么用"登记表 + 事件处理器"而不是给每件物品写一个 {@code Item} 子类：
 * {@code Item#appendHoverText} 是实例方法，子类只能管自己那一件，加十件带提示的物品
 * 就要多十个类；而"内容被停用"这类提示还要作用于**任意物品**，只有事件方案能做。
 * 于是这里只存数据，渲染统一交给 {@code client.ItemTooltipHandler}。</p>
 *
 * <p>提示文字一律用**语言键**（不是写死的中文），这样中英双语都跟着
 * {@code assets/foodindustry/lang/*.json} 走。</p>
 *
 * <p>登记发生在 {@link CommonSetup}（{@code FMLCommonSetupEvent}），
 * 那时物品早已注册完毕。</p>
 */
public final class ModTooltips {

    /** 物品 → 该物品要追加的提示行（按登记顺序显示）。LinkedHashMap 保证顺序稳定。 */
    private static final Map<Item, List<Component>> LINES = new LinkedHashMap<>();

    /**
     * 给某件物品登记一条提示；同一件物品可以登记多条，按调用顺序显示。
     *
     * @param item       目标物品，直接传 {@code ModItems.XXX.get()}
     * @param key        语言键
     * @param formatting 可选的样式（不传则用默认色）
     */
    public static void register(Item item, String key, ChatFormatting... formatting) {
        // 注意类型必须是 MutableComponent：Component 是只读接口，
        // 没有 withStyle(...) 这些链式方法（写了会报"找不到符号/无法访问 Message"）。
        MutableComponent line = Component.translatable(key);
        if (formatting != null && formatting.length > 0) {
            line = line.withStyle(formatting);   // MutableComponent 上有 withStyle(ChatFormatting...)
        }
        LINES.computeIfAbsent(item, ignored -> new ArrayList<>()).add(line);
    }

    /** 这件物品登记过的全部提示行；没有就返回空表。 */
    public static List<Component> linesFor(Item item) {
        return LINES.getOrDefault(item, List.of());
    }

    /**
     * 登记本模组所有物品提示。由 {@link CommonSetup} 调用。
     *
     * <p>新增提示**只改这里**。</p>
     */
    public static void registerAll() {
        // 士腻架（slickers）：名字是"士力架"的谐音，玩梗物品
        register(ModItems.SLICKERS.get(), "item.foodindustry.slickers.tooltip", ChatFormatting.GOLD);

        // ↓↓↓ 以后要给新物品加提示，在这里加一行即可 ↓↓↓
    }

    private ModTooltips() {
    }
}
