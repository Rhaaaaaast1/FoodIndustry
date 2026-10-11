package dev.rhaaaaaast1.foodindustry.common.item;

import dev.rhaaaaaast1.foodindustry.common.registry.ModFluids;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 让本模组的**瓶装 / 碗装成品**成为真正的流体容器，这样 Create（注液器、泵、管道、储罐）、
 * Mekanism 等**所有按 NeoForge 标准能力读流体**的机器都能操作它们。
 *
 * <p>规则：</p>
 * <ul>
 *   <li>容量 **250 mB**（对齐 Farmer's Delight / BrewinAndChewin 的瓶子惯例），碗同理；</li>
 *   <li>成品本身是**满的** → {@code fill} 永远返回 0（灌不进）；</li>
 *   <li>{@code drain} 是**整瓶倒**（和桶一样，不支持倒一半）→ 倒完把物品换成**空玻璃瓶 / 空碗**；</li>
 *   <li>只处理**单个**物品堆叠（{@code count == 1}）—— 与 NeoForge 自带的
 *       {@code FluidBucketWrapper} 行为一致：调用方（机器）本来就应该传单个物品。</li>
 * </ul>
 *
 * <p><b>为什么不接管原版的空玻璃瓶 / 空碗</b>：给 {@code minecraft:glass_bottle} 注册流体能力会
 * ① 可能和别的模组的注册冲突，② 让原版玻璃瓶在全局变成流体容器、污染药水/酿造交互。
 * "空瓶灌满"这个方向交给 **Create 的 filling 配方**（数据包层）处理更安全。</p>
 */
public class FluidContainerItemHandler implements IFluidHandlerItem {

    /** 一瓶 / 一碗的量。 */
    public static final int CAPACITY = 250;

    /** 懒初始化：容器物品 → （流体, 倒空后变成什么）。 */
    @Nullable
    private static Map<Item, Entry> entries;

    private ItemStack container;

    public FluidContainerItemHandler(ItemStack container) {
        this.container = container;
    }

    @Override
    public ItemStack getContainer() {
        // ⚠️ 必须返回**当前**（可能已经被倒空替换过的）堆叠，机器靠它把物品换掉
        return this.container;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        Entry entry = entryFor(this.container.getItem());
        return entry == null ? FluidStack.EMPTY : new FluidStack(entry.fluid().get(), CAPACITY);
    }

    @Override
    public int getTankCapacity(int tank) {
        return CAPACITY;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        // 成品已经是满的
        return false;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        // 我们的成品瓶/碗永远装满了 → 灌不进（"空瓶灌满"走 Create 的 filling 配方）
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        Entry entry = entryFor(this.container.getItem());
        if (entry == null || resource.isEmpty() || !resource.is(entry.fluid().get())) {
            return FluidStack.EMPTY;
        }
        return drain(CAPACITY, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        // 与 FluidBucketWrapper 一致：堆叠数量不为 1 时不动手（机器本来就该传单个物品）
        if (this.container.getCount() != 1) {
            return FluidStack.EMPTY;
        }
        Entry entry = entryFor(this.container.getItem());
        if (entry == null || maxDrain < CAPACITY) {
            // 整瓶倒，不支持倒一半
            return FluidStack.EMPTY;
        }
        FluidStack drained = new FluidStack(entry.fluid().get(), CAPACITY);
        if (action.execute()) {
            this.container = new ItemStack(entry.emptyContainer());
        }
        return drained;
    }

    @Nullable
    private static Entry entryFor(Item item) {
        return entries().get(item);
    }

    /** 从流体注册表推导映射，避免两处维护。 */
    private static Map<Item, Entry> entries() {
        if (entries == null) {
            Map<Item, Entry> map = new HashMap<>();
            for (ModFluids.MilkLike milkLike : ModFluids.allMilkLike()) {
                if (milkLike.bottle() != null) {
                    map.put(milkLike.bottle().get(), new Entry(milkLike.source(), Items.GLASS_BOTTLE));
                }
                if (milkLike.bowl() != null) {
                    map.put(milkLike.bowl().get(), new Entry(milkLike.source(), Items.BOWL));
                }
            }
            entries = map;
        }
        return entries;
    }

    /** 一个容器物品：里面装的流体 + 倒空后变成什么。 */
    private record Entry(DeferredHolder<net.minecraft.world.level.material.Fluid, FlowingFluid> fluid,
                         Item emptyContainer) {
    }
}
