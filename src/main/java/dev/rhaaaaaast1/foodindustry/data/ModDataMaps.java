package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

/**
 * 原版/NeoForge 数据表（data map）生成。
 *
 * <p>1.21 起堆肥几率**不再是 {@code ComposterBlock.COMPOSTABLES} 那个静态 map**
 * （那个字段已被 {@code @Deprecated}，注释写着改用 NeoForge 的 compostable data map），
 * 而是数据文件 {@code data/neoforge/data_maps/item/compostables.json}，
 * 运行期由 {@code ComposterBlock.getValue(ItemStack)} 里的
 * {@code item.getItemHolder().getData(NeoForgeDataMaps.COMPOSTABLES)} 读取。</p>
 *
 * <p>所以"给物品加堆肥几率"= 生成本模组的 data map 文件
 * （{@code data/foodindustry/data_maps/item/compostables.json}），
 * <b>不是</b>注册方块或配方。</p>
 */
public class ModDataMaps extends DataMapProvider {

    // ==================================================================
    //  几率取值依据（原版与农夫乐事完全一致）
    // ==================================================================
    /** 种子：原版 {@code wheat_seeds}/{@code beetroot_seeds}/西瓜南瓜种子全是 0.3；
     *  农夫乐事的 {@code cabbage_seeds}/{@code tomato_seeds}/{@code rice} 等也全是 0.3。 */
    private static final float CHANCE_SEEDS = 0.30F;
    /** 作物产物：对齐原版 {@code wheat}/{@code carrot}/{@code potato}/{@code beetroot} 的 0.65，
     *  以及农夫乐事的 {@code cabbage}/{@code onion}/{@code tomato}。 */
    private static final float CHANCE_CROPS = 0.65F;
    /** 野生作物方块：农夫乐事给 {@code wild_cabbages}/{@code wild_onions}/{@code wild_tomatoes}/
     *  {@code wild_carrots}/{@code wild_potatoes}/{@code wild_beetroots} 都是 0.65
     *  （见 {@code farmersdelight/data/DataMaps.java:70-76}）。 */
    private static final float CHANCE_WILD_CROPS = 0.65F;

    public ModDataMaps(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    /**
     * 取物品的注册表句柄。
     *
     * <p>不能直接把 {@code DeferredItem<X>} 里的 {@code X} 传进去：{@code DeferredItem<ItemNameBlockItem>}
     * 实现的是 {@code Holder<ItemNameBlockItem>}，而 Java 的泛型不变，
     * 它<b>不是</b> {@code Holder<Item>}，编译会报
     * "no suitable method found for add(ItemNameBlockItem, Compostable, boolean)"。
     * 所以先用 {@code .get()} 拿到 {@code Item} 本身，再取它在原版物品注册表里的句柄。</p>
     */
    private static Holder<Item> item(DeferredItem<? extends Item> deferred) {
        return deferred.get().builtInRegistryHolder();
    }

    @Override
    protected void gather() {
        // ---------------- 种子（0.3）----------------
        // can_villager_compost = true 的判据来自原版：只有**能种的**那两样种子
        // （wheat_seeds / beetroot_seeds）才带这个标记，别的物品一律没有。
        // 它控制的是 WorkAtComposter 里"农民往堆肥桶里塞物品"的行为
        // （WorkAtComposter.java:50-51 读 compostable.canVillagerCompost()），
        // 对玩家自己堆肥毫无影响。我们的种子都能种（都是 ItemNameBlockItem），
        // 所以照原版给它们打上这个标记。
        var compostables = builder(NeoForgeDataMaps.COMPOSTABLES)
                .add(item(ModItems.CHILI_SEEDS), new Compostable(CHANCE_SEEDS, true), false)
                .add(item(ModItems.EGGPLANT_SEEDS), new Compostable(CHANCE_SEEDS, true), false)
                .add(item(ModItems.STRAWBERRY_SEEDS), new Compostable(CHANCE_SEEDS, true), false)
                .add(item(ModItems.CUCUMBER_SEEDS), new Compostable(CHANCE_SEEDS, true), false)
                .add(item(ModItems.WHITE_GRAPE_SEEDS), new Compostable(CHANCE_SEEDS, true), false)
                .add(item(ModItems.RED_GRAPE_SEEDS), new Compostable(CHANCE_SEEDS, true), false)
                .add(item(ModItems.BLUEBERRY_SEEDS), new Compostable(CHANCE_SEEDS, true), false)
                .add(item(ModItems.TEA_SEEDS), new Compostable(CHANCE_SEEDS, true), false)
                .add(item(ModItems.SUNFLOWER_SEEDS), new Compostable(CHANCE_SEEDS, true), false)
                .add(item(ModItems.CORN_KERNELS), new Compostable(CHANCE_SEEDS, true), false)
                .add(item(ModItems.PINEAPPLE_CROWN), new Compostable(CHANCE_SEEDS, true), false)
                .add(item(ModItems.PINEAPPLE_SAPLING), new Compostable(CHANCE_SEEDS, true), false)
                // 产物即种子
                .add(item(ModItems.PEANUTS), new Compostable(CHANCE_SEEDS, true), false)
                .add(item(ModItems.SOYBEANS), new Compostable(CHANCE_SEEDS, true), false)
                .add(item(ModItems.RAW_COFFEE_BEANS), new Compostable(CHANCE_SEEDS, true), false);

        // ---------------- 作物产物（0.65）----------------
        // 比照原版小麦/胡萝卜/马铃薯和农夫乐事的卷心菜/洋葱/番茄。
        // 注意咖啡果只能切、不能种，所以不带 can_villager_compost。
        compostables.add(item(ModItems.CORN), new Compostable(CHANCE_CROPS), false)
                .add(item(ModItems.CHILI_PEPPER), new Compostable(CHANCE_CROPS), false)
                .add(item(ModItems.EGGPLANT), new Compostable(CHANCE_CROPS), false)
                .add(item(ModItems.STRAWBERRIES), new Compostable(CHANCE_CROPS), false)
                .add(item(ModItems.PINEAPPLE), new Compostable(CHANCE_CROPS), false)
                .add(item(ModItems.CUCUMBER), new Compostable(CHANCE_CROPS), false)
                .add(item(ModItems.WHITE_GRAPES), new Compostable(CHANCE_CROPS), false)
                .add(item(ModItems.RED_GRAPES), new Compostable(CHANCE_CROPS), false)
                .add(item(ModItems.BLUEBERRIES), new Compostable(CHANCE_CROPS), false)
                .add(item(ModItems.TEA_LEAVES), new Compostable(CHANCE_CROPS), false)
                .add(item(ModItems.COFFEE_CHERRIES), new Compostable(CHANCE_CROPS), false);

        // ---------------- 野生作物方块（0.65）----------------
        // 这些是**方块物品**（野生作物可以直接采下来挪地方），农夫乐事对它的
        // wild_cabbages / wild_onions / wild_tomatoes / wild_carrots / wild_potatoes /
        // wild_beetroots 六样给的都是 0.65（FD 的 data/DataMaps.java:70-76），
        // 所以照搬同一个数。野生作物不能种，所以不带 can_villager_compost。
        compostables.add(item(ModItems.WILD_CORN), new Compostable(CHANCE_WILD_CROPS), false)
                .add(item(ModItems.WILD_PEANUT), new Compostable(CHANCE_WILD_CROPS), false)
                .add(item(ModItems.WILD_CHILI), new Compostable(CHANCE_WILD_CROPS), false)
                .add(item(ModItems.WILD_STRAWBERRY), new Compostable(CHANCE_WILD_CROPS), false)
                .add(item(ModItems.WILD_PINEAPPLE), new Compostable(CHANCE_WILD_CROPS), false)
                .add(item(ModItems.WILD_SOYBEAN), new Compostable(CHANCE_WILD_CROPS), false)
                .add(item(ModItems.WILD_CUCUMBER), new Compostable(CHANCE_WILD_CROPS), false)
                .add(item(ModItems.WILD_WHITE_GRAPE), new Compostable(CHANCE_WILD_CROPS), false)
                .add(item(ModItems.WILD_RED_GRAPE), new Compostable(CHANCE_WILD_CROPS), false)
                .add(item(ModItems.WILD_TEA), new Compostable(CHANCE_WILD_CROPS), false)
                .add(item(ModItems.WILD_BLUEBERRY), new Compostable(CHANCE_WILD_CROPS), false)
                .add(item(ModItems.WILD_COFFEE), new Compostable(CHANCE_WILD_CROPS), false);
    }
}
