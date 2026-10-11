package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.common.block.crop.RegrowingCropBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.RopeCropBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.RopeVineBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.TallFlowerCropBlock;
import dev.rhaaaaaast1.foodindustry.common.registry.ModBlocks;
import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.loot.CanItemPerformAbility;

import java.util.Set;

/**
 * 方块战利品表生成。
 *
 * <p>三类方块的掉落规则：</p>
 * <ul>
 *   <li><b>作物</b>：只有长到最大 age 才掉产物；种子始终掉落并带时运加成（同原版小麦）。</li>
 *   <li><b>灌木</b>：破坏时掉果实（主要来源是右键收获）。</li>
 *   <li><b>野生作物</b>：**规则与农夫乐事一致** —— 用剪刀采集掉方块本身；否则掉落物享受时运加成，
 *       其中野生茶是"茶籽必定 + 茶叶 20%"。20% 这个概率就是 FD 野生作物的概率。</li>
 * </ul>
 */
public class ModBlockLootTables extends BlockLootSubProvider {

    private final HolderLookup.Provider registries;

    protected ModBlockLootTables(HolderLookup.Provider registries) {
        super(Set.<Item>of(), FeatureFlags.REGISTRY.allFlags(), registries);
        this.registries = registries;
    }

    @Override
    protected void generate() {
        // ---------------- 普通作物 ----------------
        // 玉米：成熟只掉**玉米棒**，不掉玉米粒（玉米粒靠砧板切玉米棒、以及野生玉米获得），
        // 但玉米棒本身按原版胡萝卜那样多给一份池（成熟 2~5 根）
        cropWithCobOnly(ModBlocks.CORN.get(), CropBlock.AGE, CropBlock.MAX_AGE,
                ModItems.CORN.get(), ModItems.CORN_KERNELS.get());
        // 辣椒：可反复右键采摘；挖掉植株时按普通作物给（没熟还 1 颗种子，成熟 1 个辣椒 + 1~4 颗种子）
        crop(ModBlocks.CHILI.get(), RegrowingCropBlock.AGE, ModItems.CHILI_PEPPER.get(), ModItems.CHILI_SEEDS.get());
        // 花生与大豆的产物就是种子：按**原版胡萝卜/马铃薯**的掉落（未成熟还 1 个，成熟 2~5 个）
        matureCropLikeCarrot(ModBlocks.PEANUT.get(), CropBlock.AGE, CropBlock.MAX_AGE, ModItems.PEANUTS.get());
        matureCropLikeCarrot(ModBlocks.SOYBEAN.get(), CropBlock.AGE, CropBlock.MAX_AGE, ModItems.SOYBEANS.get());
        // 菠萝：成熟后**恰好掉 1 个**（无论时运多高都只长一个）；也**不掉菠萝头**
        // （菠萝头靠砧板切菠萝得到）。植株本身是可反复收获的，这里是"把植株挖掉"的情况。
        matureCropExactOne(ModBlocks.PINEAPPLE.get(), RegrowingCropBlock.AGE, RegrowingCropBlock.MAX_AGE,
                ModItems.PINEAPPLE.get());
        // 向日葵作物：**未成熟也还你 1 颗种子**，成熟再多掉 1 颗（原版小麦就是这个规则）
        add(ModBlocks.SUNFLOWER_CROP.get(), createCropDrops(ModBlocks.SUNFLOWER_CROP.get(),
                ModItems.SUNFLOWER_SEEDS.get(), ModItems.SUNFLOWER_SEEDS.get(),
                matureCondition(ModBlocks.SUNFLOWER_CROP.get(), TallFlowerCropBlock.AGE,
                        TallFlowerCropBlock.MAX_AGE)));

        // ---------------- 攀藤作物：底部作物与藤 ----------------
        crop(ModBlocks.CUCUMBER.get(), RopeCropBlock.VINE_AGE, ModItems.CUCUMBER.get(), ModItems.CUCUMBER_SEEDS.get());
        crop(ModBlocks.CUCUMBER_VINE.get(), RopeVineBlock.VINE_AGE, ModItems.CUCUMBER.get(), ModItems.CUCUMBER_SEEDS.get());
        crop(ModBlocks.WHITE_GRAPE.get(), RopeCropBlock.VINE_AGE, ModItems.WHITE_GRAPES.get(), ModItems.WHITE_GRAPE_SEEDS.get());
        crop(ModBlocks.WHITE_GRAPE_VINE.get(), RopeVineBlock.VINE_AGE, ModItems.WHITE_GRAPES.get(), ModItems.WHITE_GRAPE_SEEDS.get());
        crop(ModBlocks.RED_GRAPE.get(), RopeCropBlock.VINE_AGE, ModItems.RED_GRAPES.get(), ModItems.RED_GRAPE_SEEDS.get());
        crop(ModBlocks.RED_GRAPE_VINE.get(), RopeVineBlock.VINE_AGE, ModItems.RED_GRAPES.get(), ModItems.RED_GRAPE_SEEDS.get());

        // ---------------- 灌木：只掉果实 ----------------
        add(ModBlocks.TEA_BUSH.get(), createSingleItemTable(ModItems.TEA_LEAVES.get()));
        add(ModBlocks.BLUEBERRY_BUSH.get(), createSingleItemTable(ModItems.BLUEBERRIES.get()));
        add(ModBlocks.STRAWBERRY_BUSH.get(), createSingleItemTable(ModItems.STRAWBERRIES.get()));
        add(ModBlocks.COFFEE_BUSH.get(), createSingleItemTable(ModItems.COFFEE_CHERRIES.get()));

        // ---------------- 野生作物 ----------------
        // 「其他的野生作物打掉都掉落本身」
        add(ModBlocks.WILD_PEANUT.get(), block -> wildDrop(block, ModItems.PEANUTS.get()));
        add(ModBlocks.WILD_CHILI.get(), block -> wildDrop(block, ModItems.CHILI_PEPPER.get()));
        add(ModBlocks.WILD_STRAWBERRY.get(), block -> wildDrop(block, ModItems.STRAWBERRIES.get()));
        add(ModBlocks.WILD_SOYBEAN.get(), block -> wildDrop(block, ModItems.SOYBEANS.get()));
        add(ModBlocks.WILD_CUCUMBER.get(), block -> wildDrop(block, ModItems.CUCUMBER.get()));
        add(ModBlocks.WILD_WHITE_GRAPE.get(), block -> wildDrop(block, ModItems.WHITE_GRAPES.get()));
        add(ModBlocks.WILD_RED_GRAPE.get(), block -> wildDrop(block, ModItems.RED_GRAPES.get()));
        add(ModBlocks.WILD_BLUEBERRY.get(), block -> wildDrop(block, ModItems.BLUEBERRIES.get()));
        add(ModBlocks.WILD_COFFEE.get(), block -> wildDrop(block, ModItems.COFFEE_CHERRIES.get()));

        // 特例：野生玉米只掉玉米粒；野生菠萝掉菠萝苗（与菠萝头是不同的物品）
        add(ModBlocks.WILD_CORN.get(), block -> wildDrop(block, ModItems.CORN_KERNELS.get()));
        add(ModBlocks.WILD_PINEAPPLE.get(), block -> wildDrop(block, ModItems.PINEAPPLE_SAPLING.get()));

        // 特例：野生茶树同时掉茶籽（必定）与茶叶（20%）
        add(ModBlocks.WILD_TEA.get(), block -> wildDropsWithSeeds(block, ModItems.TEA_LEAVES.get(), ModItems.TEA_SEEDS.get()));

        // ---------------- 蛋糕（14 种）----------------
        // 同原版蛋糕：破坏时掉回自己；切片只能靠"吃"或"用刀切"得到。
        // 必须在这里生成 —— src/generated/resources 是 datagen 的输出目录，
        // 手写进去的文件下一次 runData 会被当陈旧文件删掉（removed stale: N）。
        for (var cake : dev.rhaaaaaast1.foodindustry.common.registry.ModPastries.CAKES.values()) {
            dropSelf(cake.get());
        }

        // ---------------- 派（8 种）----------------
        for (var pie : dev.rhaaaaaast1.foodindustry.common.registry.ModPastries.PIES.values()) {
            dropSelf(pie.get());
        }
    }

    // ==================================================================
    //  掉落规则
    // ==================================================================

    /** 作物掉落：只有长到最大 age 才掉产物，种子则始终会掉（含时运）。 */
    private void crop(Block block, IntegerProperty ageProperty, Item produce, Item seeds) {
        add(block, createCropDrops(block, produce, seeds, matureCondition(block, ageProperty, CropBlock.MAX_AGE)));
    }

    /**
     * 产物即种子、且掉落规则**对齐原版胡萝卜/马铃薯**的作物（花生、大豆）。
     *
     * <p>原版胡萝卜（数据实测）是两池：</p>
     * <ul>
     *   <li>池1：产物 ×1，**没有 age 条件** —— 所以没长熟也还你 1 个 ✓；</li>
     *   <li>池2：产物 ×1，**成熟才掉** + 时运二项加成（extra 3 / p 0.571）→ 成熟共 **2~5 个** ✓。</li>
     * </ul>
     * <p>这正是 {@code createCropDrops(block, item, item, 成熟条件)} 的形状 ✓。</p>
     */
    private void matureCropLikeCarrot(Block block, IntegerProperty ageProperty, int maxAge, Item item) {
        add(block, createCropDrops(block, item, item, matureCondition(block, ageProperty, maxAge)));
    }

    /**
     * 玉米这种"成熟只给玉米棒、**不给玉米粒**"的作物。
     *
     * <p>为什么要专门写一个：玉米粒是独立的种植物品，但如果作物成熟后直接掉玉米粒，
     * 就**绕过了砧板**（砧板切玉米棒才是设计上的取粒方式）✗。所以这里：</p>
     * <ul>
     *   <li>成熟 → 只掉**玉米棒** ✓（而且按原版胡萝卜多给一份池：成熟 2~5 根 ✓）；</li>
     *   <li>没长熟 → 还你 **1 颗玉米粒** ✓（原版"未成熟还种子"的行为，避免亏掉种子 ✓）。</li>
     * </ul>
     * <p>玉米粒的正式来源：**砧板切玉米棒**（1 根 → 32 颗）与**野生玉米**掉落 ✓。</p>
     */
    private void cropWithCobOnly(Block block, IntegerProperty ageProperty, int maxAge, Item cob, Item kernels) {
        LootItemCondition.Builder mature = matureCondition(block, ageProperty, maxAge);
        add(block, LootTable.lootTable()
                // 池1：成熟掉玉米棒；没熟还 1 颗玉米粒
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(AlternativesEntry.alternatives(
                                LootItem.lootTableItem(cob).when(mature),
                                LootItem.lootTableItem(kernels))))
                // 池2：胡萝卜式的额外一份玉米棒（成熟 + 时运二项加成，原版同款公式）
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(mature)
                        .add(LootItem.lootTableItem(cob)
                                .apply(ApplyExplosionDecay.explosionDecay())
                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(
                                        enchantments().getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3)))));
    }

    /**
     * 成熟后**恰好掉 1 个**的作物（菠萝：无论如何只长一个）。
     *
     * <p>和 {@link #matureCropLikeCarrot} 的区别是**不加任何时运加成** ✓ —— 附魔再好也只掉 1 个。</p>
     */
    private void matureCropExactOne(Block block, IntegerProperty ageProperty, int maxAge, Item item) {
        add(block, LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(item)
                        .apply(ApplyExplosionDecay.explosionDecay()))
                .when(matureCondition(block, ageProperty, maxAge))));
    }

    /**
     * 野生作物的"只掉本身"规则：用剪刀采集掉方块本身，否则掉落物受时运加成。
     * 与农夫乐事的野生作物行为一致。
     */
    private LootTable.Builder wildDrop(Block block, Item drop) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(AlternativesEntry.alternatives(
                        LootItem.lootTableItem(block).when(shearsHarvest()),
                        LootItem.lootTableItem(drop)
                                .apply(ApplyExplosionDecay.explosionDecay())
                                .apply(fortuneBonus()))));
    }

    /**
     * 野生作物的"掉落物 + 额外产物"规则：掉落物必定（受时运），产物有 **20%** 概率额外掉落；
     * 用剪刀采集时改为掉方块本身。20% 与农夫乐事一致。
     */
    private LootTable.Builder wildDropsWithSeeds(Block block, Item extra, Item drop) {
        return wildDrop(block, drop).withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(extra))
                .when(shearsHarvest().invert())
                .when(LootItemRandomChanceCondition.randomChance(0.2F)));
    }

    /** "长到最大 age"的条件。 */
    private static LootItemCondition.Builder matureCondition(Block block, IntegerProperty ageProperty, int maxAge) {
        return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ageProperty, maxAge));
    }

    /** "手上是剪刀"的条件。 */
    private static LootItemCondition.Builder shearsHarvest() {
        return CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_HARVEST);
    }

    /** 时运加成（额外 0~2 个），与农夫乐事的作物/野生作物一致。 */
    private LootItemFunction.Builder fortuneBonus() {
        return ApplyBonusCount.addUniformBonusCount(enchantments().getOrThrow(Enchantments.FORTUNE), 2);
    }

    private HolderLookup.RegistryLookup<Enchantment> enchantments() {
        return registries.lookupOrThrow(Registries.ENCHANTMENT);
    }

    /**
     * 必须列出本模组所有方块：数据生成会校验"每个方块都有战利品表"，漏掉会直接报错。
     */
    @Override
    protected Iterable<Block> getKnownBlocks() {
        java.util.List<Block> all = new java.util.ArrayList<>();
        ModBlocks.BLOCKS.getEntries().forEach(holder -> all.add(holder.get()));
        // 蛋糕注册在另一个 DeferredRegister 里，别漏掉（漏了 datagen 会直接报错）
        dev.rhaaaaaast1.foodindustry.common.registry.ModPastries.CAKES.values()
                .forEach(cake -> all.add(cake.get()));
        // 派同理
        dev.rhaaaaaast1.foodindustry.common.registry.ModPastries.PIES.values()
                .forEach(pie -> all.add(pie.get()));
        return all;
    }
}
