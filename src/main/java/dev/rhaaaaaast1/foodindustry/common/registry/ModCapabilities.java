package dev.rhaaaaaast1.foodindustry.common.registry;

import dev.rhaaaaaast1.foodindustry.common.item.FluidContainerItemHandler;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

/**
 * 能力（capability）注册。
 *
 * <h2>为什么桶要单独注册</h2>
 * <p>NeoForge 给物品自动挂"流体容器"能力时用的是<b>精确类比较</b>而不是 {@code instanceof}：</p>
 *
 * <pre>{@code
 * // net.neoforged.neoforge.capabilities.CapabilityHooks
 * for (Item item : BuiltInRegistries.ITEM) {
 *     if (item.getClass() == BucketItem.class)          // ← getClass() ==
 *         event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), item);
 * }
 * }</pre>
 *
 * <p>所以<b>任何继承 {@code BucketItem} 的自定义桶都拿不到这个能力</b>，必须自己再注册一次 ——
 * 否则它在储罐 / 管道 / 注液器里根本不会被当成流体容器：既接不出液体，也倒不进去。</p>
 *
 * <h2>瓶装 / 碗装</h2>
 * <p>我们的瓶和碗是自定义物品类，NeoForge 也不会自动挂能力，所以同样在这里用
 * {@link FluidContainerItemHandler} 注册（容量 250 mB，倒空后变成空玻璃瓶 / 空碗）。
 * 这样 Create、Mekanism 等所有走标准能力的机器都能读它们。</p>
 *
 * <p><b>刻意不接管</b>原版 {@code minecraft:glass_bottle} / {@code minecraft:bowl}：
 * 那会和其他模组的注册冲突，也会让原版物品在全局变成流体容器。
 * "拿空瓶灌满"这个方向交给 Create 的 filling 配方（数据包层）。</p>
 */
public final class ModCapabilities {

    /** 挂到 mod 事件总线（能力注册属于 mod 事件，不是游戏事件）。 */
    public static void register(RegisterCapabilitiesEvent event) {
        // ⚠️ 发酵木桶的方块能力（物品面/流体面的严格单向）已随木桶一起暂时移除。
        //    恢复时要加回：
        //      event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
        //              ModBlockEntities.FERMENTATION_BARREL.get(),
        //              (barrel, side) -> barrel.itemHandler(side));
        //      event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
        //              ModBlockEntities.FERMENTATION_BARREL.get(),
        //              (barrel, side) -> barrel.fluidHandler(side));

        for (ModFluids.MilkLike milkLike : ModFluids.allMilkLike()) {
            // 桶：沿用 NeoForge 自带的包装器
            if (milkLike.bucket() != null) {
                event.registerItem(Capabilities.FluidHandler.ITEM,
                        (stack, context) -> new FluidBucketWrapper(stack),
                        milkLike.bucket().get());
            }
            // 瓶 / 碗：我们的自定义容器包装器
            if (milkLike.bottle() != null) {
                event.registerItem(Capabilities.FluidHandler.ITEM,
                        (stack, context) -> new FluidContainerItemHandler(stack),
                        milkLike.bottle().get());
            }
            if (milkLike.bowl() != null) {
                event.registerItem(Capabilities.FluidHandler.ITEM,
                        (stack, context) -> new FluidContainerItemHandler(stack),
                        milkLike.bowl().get());
            }
        }
    }

    private ModCapabilities() {
    }
}
