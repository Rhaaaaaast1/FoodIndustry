package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.common.registry.ModChestLootTables;
import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.BiConsumer;

/**
 * 注入到原版结构箱子里的战利品表内容。
 *
 * <p>这里<b>只负责"箱子里能开出什么"</b>；"把这些表挂到哪个原版箱子上"由
 * {@link ModGlobalLootModifiers} 生成的 {@code neoforge:add_table} 全局修改器负责。
 * 拆成两半是农夫乐事的做法（{@code FDChestLoot} + {@code LootModifiers}），
 * 好处是一张表可以被多个箱子复用，箱子清单改动也不用动内容。</p>
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li><b>只放作物与种子</b>。不放辣酱、酱油、醋、食用油这类瓶罐成品
 *       —— 那些是要玩家自己动手做的，箱子直接开出来会跳过整个加工链。</li>
 *   <li><b>不重复原版已有的东西</b>（小麦、面包、胡萝卜……），所以这里看到的
 *       全是 foodindustry 自己的作物产物与种子。</li>
 *   <li>每池 {@code 1..3} 次抽取 + 空条目压权重，避免把箱子塞爆。</li>
 *   <li>作物按生物群系/建筑风格挑：沙漠给辣椒和茄子，雪原给茶，针叶林给浆果。</li>
 * </ul>
 */
public final class ModChestLoot implements LootTableSubProvider {

    /** 空条目的权重：数字越大越容易"这次什么都没抽到"。 */
    private static final int WEIGHT_NOTHING = 3;

    public ModChestLoot(HolderLookup.Provider registries) {
        // 本模组的箱子内容全是固定物品，不需要查注册表。
        // 保留这个参数是因为 LootTableSubProvider 的约定如此，将来要塞附魔书之类的会用到。
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {

        // ==============================================================
        //  村庄民房 × 5 种生物群系
        // ==============================================================
        // 都是"普通人家"，所以结构一样：一池作物与种子。
        // 群系风味靠多给一两样当季作物体现。
        consumer.accept(ModChestLootTables.VILLAGE_PLAINS_HOUSE, LootTable.lootTable()
                .withPool(cropPool(1.0F, 3.0F)
                        .add(item(ModItems.PEANUTS, 2, 1, 3))
                        .add(item(ModItems.SOYBEANS, 2, 1, 3))
                        .add(item(ModItems.CHILI_SEEDS, 1, 1, 3))
                        .add(item(ModItems.EGGPLANT_SEEDS, 1, 1, 3))
                        .add(item(ModItems.CUCUMBER_SEEDS, 1, 1, 3))
                        .add(item(ModItems.STRAWBERRY_SEEDS, 1, 1, 3))
                        .add(item(ModItems.BLUEBERRY_SEEDS, 1, 1, 3))
                        .add(item(ModItems.CORN, 2, 1, 2))
                        .add(item(ModItems.CUCUMBER, 2, 1, 2))));

        consumer.accept(ModChestLootTables.VILLAGE_DESERT_HOUSE, LootTable.lootTable()
                .withPool(cropPool(1.0F, 3.0F)
                        .add(item(ModItems.CHILI_SEEDS, 3, 1, 3))
                        .add(item(ModItems.EGGPLANT_SEEDS, 3, 1, 3))
                        .add(item(ModItems.CHILI_PEPPER, 3, 1, 3))
                        .add(item(ModItems.EGGPLANT, 2, 1, 2))
                        .add(item(ModItems.PEANUTS, 1, 1, 3))));

        consumer.accept(ModChestLootTables.VILLAGE_SAVANNA_HOUSE, LootTable.lootTable()
                .withPool(cropPool(1.0F, 3.0F)
                        .add(item(ModItems.CHILI_SEEDS, 3, 1, 3))
                        .add(item(ModItems.CHILI_PEPPER, 2, 1, 3))
                        .add(item(ModItems.PEANUTS, 2, 1, 3))
                        .add(item(ModItems.SOYBEANS, 2, 1, 3))
                        .add(item(ModItems.CORN, 2, 1, 2))
                        .add(item(ModItems.EGGPLANT_SEEDS, 1, 1, 3))));

        consumer.accept(ModChestLootTables.VILLAGE_SNOWY_HOUSE, LootTable.lootTable()
                .withPool(cropPool(1.0F, 3.0F)
                        .add(item(ModItems.TEA_SEEDS, 3, 1, 3))
                        .add(item(ModItems.TEA_LEAVES, 3, 1, 3))
                        .add(item(ModItems.SUNFLOWER_SEEDS, 2, 1, 3))
                        .add(item(ModItems.SOYBEANS, 2, 1, 3))
                        .add(item(ModItems.PEANUTS, 1, 1, 3))
                        .add(item(ModItems.GREEN_TEA_LEAVES, 1, 1, 3))));

        consumer.accept(ModChestLootTables.VILLAGE_TAIGA_HOUSE, LootTable.lootTable()
                .withPool(cropPool(1.0F, 3.0F)
                        .add(item(ModItems.BLUEBERRY_SEEDS, 3, 1, 3))
                        .add(item(ModItems.BLUEBERRIES, 3, 1, 3))
                        .add(item(ModItems.STRAWBERRY_SEEDS, 2, 1, 3))
                        .add(item(ModItems.STRAWBERRIES, 2, 1, 3))
                        .add(item(ModItems.TEA_SEEDS, 2, 1, 3))
                        .add(item(ModItems.PEANUTS, 1, 1, 3))));

        // 屠宰店：这里是"店里卖的货"，所以给耐放的作物与种子。
        consumer.accept(ModChestLootTables.VILLAGE_BUTCHER, LootTable.lootTable()
                .withPool(cropPool(1.0F, 2.0F)
                        .add(item(ModItems.PEANUTS, 2, 1, 3))
                        .add(item(ModItems.SOYBEANS, 2, 1, 3))
                        .add(item(ModItems.CHILI_SEEDS, 2, 1, 3))
                        .add(item(ModItems.EGGPLANT_SEEDS, 1, 1, 3))
                        .add(item(ModItems.CORN_KERNELS, 1, 1, 3))));

        // ==============================================================
        //  沉船补给
        // ==============================================================
        // 船上装的耐放干货：一池能种的东西 + 一池能吃的作物。
        consumer.accept(ModChestLootTables.SHIPWRECK_SUPPLY, LootTable.lootTable()
                .withPool(cropPool(1.0F, 3.0F)
                        .add(item(ModItems.RAW_COFFEE_BEANS, 3, 1, 3))
                        .add(item(ModItems.COFFEE_CHERRIES, 2, 1, 3))
                        .add(item(ModItems.TEA_SEEDS, 3, 1, 3))
                        .add(item(ModItems.SOYBEANS, 2, 1, 3))
                        .add(item(ModItems.PEANUTS, 2, 1, 3))
                        .add(item(ModItems.CHILI_SEEDS, 1, 1, 3)))
                .withPool(cropPool(1.0F, 2.0F)
                        .add(item(ModItems.TEA_LEAVES, 3, 1, 3))
                        .add(item(ModItems.COFFEE_CHERRIES, 2, 1, 3))
                        .add(item(ModItems.PEANUTS, 2, 1, 3))
                        .add(item(ModItems.SOYBEANS, 2, 1, 3))));

        // ==============================================================
        //  简单地牢 / 废弃矿道
        // ==============================================================
        // 冒险箱：种子种类给全。
        consumer.accept(ModChestLootTables.SIMPLE_DUNGEON, LootTable.lootTable()
                .withPool(cropPool(1.0F, 3.0F)
                        .add(item(ModItems.CORN_KERNELS, 2, 1, 4))
                        .add(item(ModItems.PINEAPPLE_CROWN, 2, 1, 3))
                        .add(item(ModItems.CHILI_SEEDS, 2, 1, 4))
                        .add(item(ModItems.EGGPLANT_SEEDS, 1, 1, 3))
                        .add(item(ModItems.PEANUTS, 2, 1, 4))
                        .add(item(ModItems.SOYBEANS, 2, 1, 4))
                        .add(item(ModItems.CUCUMBER_SEEDS, 1, 1, 3))
                        .add(item(ModItems.WHITE_GRAPE_SEEDS, 1, 1, 3))
                        .add(item(ModItems.RED_GRAPE_SEEDS, 1, 1, 3))
                        .add(item(ModItems.BLUEBERRY_SEEDS, 1, 1, 3))
                        .add(item(ModItems.STRAWBERRY_SEEDS, 1, 1, 3))));

        consumer.accept(ModChestLootTables.ABANDONED_MINESHAFT, LootTable.lootTable()
                .withPool(cropPool(1.0F, 2.0F)
                        .add(item(ModItems.EGGPLANT_SEEDS, 2, 1, 4))
                        .add(item(ModItems.CHILI_SEEDS, 2, 1, 4))
                        .add(item(ModItems.CORN_KERNELS, 2, 1, 4))
                        .add(item(ModItems.CUCUMBER_SEEDS, 2, 1, 4))
                        .add(item(ModItems.BLUEBERRY_SEEDS, 1, 1, 4))
                        .add(item(ModItems.STRAWBERRY_SEEDS, 1, 1, 4))
                        .add(item(ModItems.TEA_SEEDS, 1, 1, 4))
                        .add(item(ModItems.SUNFLOWER_SEEDS, 1, 1, 4)))
                .withPool(cropPool(1.0F, 2.0F)
                        .add(item(ModItems.PEANUTS, 2, 1, 4))
                        .add(item(ModItems.SOYBEANS, 2, 1, 4))
                        .add(item(ModItems.COFFEE_CHERRIES, 1, 1, 3))));

        // ==============================================================
        //  掠夺者前哨站
        // ==============================================================
        // 掠夺者抢来的战利品：成堆的辣椒与茄子（对应原版"抢粮"的印象）。
        consumer.accept(ModChestLootTables.PILLAGER_OUTPOST, LootTable.lootTable()
                .withPool(cropPool(1.0F, 2.0F)
                        .add(item(ModItems.CHILI_PEPPER, 3, 4, 10))
                        .add(item(ModItems.EGGPLANT, 2, 4, 10))
                        .add(item(ModItems.PEANUTS, 2, 2, 6)))
                .withPool(cropPool(1.0F, 2.0F)
                        .add(item(ModItems.CHILI_SEEDS, 3, 2, 4))
                        .add(item(ModItems.EGGPLANT_SEEDS, 2, 2, 4))
                        .add(item(ModItems.SOYBEANS, 2, 2, 4))));

        // ==============================================================
        //  废墟传送门
        // ==============================================================
        // 下界风味：辣椒（对应下界疣那种红果）+ 能种回去的种子。
        consumer.accept(ModChestLootTables.RUINED_PORTAL, LootTable.lootTable()
                .withPool(cropPool(1.0F, 2.0F)
                        .add(item(ModItems.CHILI_SEEDS, 3, 1, 4))
                        .add(item(ModItems.CHILI_PEPPER, 3, 1, 3))
                        .add(item(ModItems.EGGPLANT_SEEDS, 1, 1, 3))));
    }

    // ==================================================================
    //  池构造工具
    // ==================================================================

    /** 作物/种子池：随机抽 minRolls~maxRolls 次，末尾垫一个空条目避免塞爆。 */
    private static LootPool.Builder cropPool(float minRolls, float maxRolls) {
        return LootPool.lootPool()
                .setRolls(UniformGenerator.between(minRolls, maxRolls))
                .add(EmptyLootItem.emptyItem().setWeight(WEIGHT_NOTHING));
    }

    /** 单个条目：权重 + 数量区间（min == max 就是定数）。 */
    private static LootPoolSingletonContainer.Builder<?> item(DeferredItem<? extends Item> item, int weight,
                                                              int minCount, int maxCount) {
        return LootItem.lootTableItem(item.get()).setWeight(weight)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(minCount, maxCount)));
    }
}
