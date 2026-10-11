package dev.rhaaaaaast1.foodindustry.common.registry;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.block.crop.BerryBushBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.RegrowingCropBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.RopeCropBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.RopeVineBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.SeedCropBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.TallFlowerCropBlock;
import dev.rhaaaaaast1.foodindustry.common.tag.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.block.WildCropBlock;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 方块注册表。
 *
 * <p>作物按**生长行为**分三组，每组共用一个方块类，差异只用构造参数表达：</p>
 * <ul>
 *   <li>普通作物 → {@link SeedCropBlock}（玉米、花生、辣椒、草莓、菠萝、大豆）</li>
 *   <li>攀藤作物 → {@link RopeCropBlock}（底部作物）+ {@link RopeVineBlock}（沿绳子长的藤）
 *       —— 黄瓜、白葡萄、红葡萄</li>
 *   <li>灌木 → {@link BerryBushBlock}（甜浆果式生长）—— 茶、咖啡、蓝莓</li>
 * </ul>
 *
 * <p>野生作物直接复用 Farmer's Delight 的 {@link WildCropBlock} —— 掉落规则、生长判定、
 * 世界生成都跟它一致，我们只提供方块与掉落表。</p>
 */
public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FoodIndustry.MODID);

    // ==================================================================
    //  土壤谓词与外部引用（必须声明在使用它们的方块之前：Java 不允许静态字段前向引用）
    // ==================================================================
    /**
     * 普通土壤（茶、蓝莓）：泥土类 + **任何 {@code FarmBlock}**。
     *
     * <p>用 {@code instanceof FarmBlock} 而不是 {@code state.is(Blocks.FARMLAND)} 很关键：
     * 原版 {@code CropBlock.mayPlaceOn} 就是这么写的，因此它同时接受原版耕地、
     * **Farmer's Delight 的沃土农田**、以及别的模组自定义的耕地。</p>
     */
    private static final Predicate<BlockState> DIRT_SOIL =
            state -> state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock;

    /**
     * 咖啡的土壤：**灰化土 + 各家乐事的沃土** —— 走我们自己的标签
     * {@code foodindustry:coffee_soil}（数据驱动，清单见 {@code ModBlockTagsProvider}）。
     */
    private static final Predicate<BlockState> COFFEE_SOIL = state -> state.is(ModTags.Blocks.COFFEE_SOIL);

    /** Farmer's Delight 的绳子：藤被破坏/失去支撑时还原成它。 */
    private static final Supplier<Block> FD_ROPE =
            () -> vectorwing.farmersdelight.common.registry.ModBlocks.ROPE.get();

    // ==================================================================
    //  普通作物（6）
    // ==================================================================
    public static final DeferredBlock<SeedCropBlock> CORN = seedCrop("corn", () -> ModItems.CORN_KERNELS.get());
    public static final DeferredBlock<SeedCropBlock> PEANUT = seedCrop("peanut", () -> ModItems.PEANUTS.get());
    /**
     * 辣椒：成熟后**右键采摘**，植株保留并继续结果（和菠萝同一套 {@link RegrowingCropBlock}）。
     * 采摘只退一阶，所以看上去不会缩回小苗。
     */
    public static final DeferredBlock<RegrowingCropBlock> CHILI =
            BLOCKS.registerBlock("chili",
                    props -> new RegrowingCropBlock(props, () -> ModItems.CHILI_SEEDS.get(),
                            () -> ModItems.CHILI_PEPPER.get(), 1, 2),
                    cropProperties());
    /** 草莓：和蓝莓一样的**树丛**（右键收获、果实会再生），不再做成普通作物。 */
    public static final DeferredBlock<BerryBushBlock> STRAWBERRY_BUSH =
            berryBush("strawberry_bush", () -> ModItems.STRAWBERRIES.get(), DIRT_SOIL);
    public static final DeferredBlock<SeedCropBlock> SOYBEAN = seedCrop("soybean", () -> ModItems.SOYBEANS.get());
    /** 菠萝：可反复收获（右键摘果，植株保留），所以用 {@link RegrowingCropBlock}。 */
    public static final DeferredBlock<RegrowingCropBlock> PINEAPPLE =
            BLOCKS.registerBlock("pineapple",
                    props -> new RegrowingCropBlock(props, () -> ModItems.PINEAPPLE_CROWN.get(),
                            () -> ModItems.PINEAPPLE.get(), 1, 1),
                    cropProperties());

    /**
     * 向日葵作物：**中间形态** —— 成熟后再过一个随机刻就开花成**原版向日葵**
     * （原版方块不重复注册，见 {@link TallFlowerCropBlock}）。
     */
    public static final DeferredBlock<TallFlowerCropBlock> SUNFLOWER_CROP =
            BLOCKS.registerBlock("sunflower_crop",
                    props -> new TallFlowerCropBlock(props, () -> ModItems.SUNFLOWER_SEEDS.get(),
                            () -> Blocks.SUNFLOWER),
                    cropProperties());

    // ==================================================================
    //  攀藤作物（3）：底部作物 + 沿绳子生长的藤
    // ==================================================================
    public static final DeferredBlock<RopeCropBlock> CUCUMBER =
            ropeCrop("cucumber", () -> ModItems.CUCUMBER_SEEDS.get(), () -> ModItems.CUCUMBER.get(),
                    () -> ModBlocks.CUCUMBER_VINE.get());
    public static final DeferredBlock<RopeVineBlock> CUCUMBER_VINE =
            ropeVine("cucumber_vine", () -> ModItems.CUCUMBER_SEEDS.get(), () -> ModItems.CUCUMBER.get(),
                    () -> ModBlocks.CUCUMBER.get());

    public static final DeferredBlock<RopeCropBlock> WHITE_GRAPE =
            ropeCrop("white_grape", () -> ModItems.WHITE_GRAPE_SEEDS.get(), () -> ModItems.WHITE_GRAPES.get(),
                    () -> ModBlocks.WHITE_GRAPE_VINE.get());
    public static final DeferredBlock<RopeVineBlock> WHITE_GRAPE_VINE =
            ropeVine("white_grape_vine", () -> ModItems.WHITE_GRAPE_SEEDS.get(), () -> ModItems.WHITE_GRAPES.get(),
                    () -> ModBlocks.WHITE_GRAPE.get());

    public static final DeferredBlock<RopeCropBlock> RED_GRAPE =
            ropeCrop("red_grape", () -> ModItems.RED_GRAPE_SEEDS.get(), () -> ModItems.RED_GRAPES.get(),
                    () -> ModBlocks.RED_GRAPE_VINE.get());
    public static final DeferredBlock<RopeVineBlock> RED_GRAPE_VINE =
            ropeVine("red_grape_vine", () -> ModItems.RED_GRAPE_SEEDS.get(), () -> ModItems.RED_GRAPES.get(),
                    () -> ModBlocks.RED_GRAPE.get());

    // ==================================================================
    //  灌木（3）：甜浆果式生长
    // ==================================================================
    public static final DeferredBlock<BerryBushBlock> TEA_BUSH =
            berryBush("tea_bush", () -> ModItems.TEA_LEAVES.get(), DIRT_SOIL);
    public static final DeferredBlock<BerryBushBlock> BLUEBERRY_BUSH =
            berryBush("blueberry_bush", () -> ModItems.BLUEBERRIES.get(), DIRT_SOIL);
    /** 咖啡：只能种在灰化土或各家乐事的沃土上（见 {@link #COFFEE_SOIL}）；收获物是咖啡果。 */
    public static final DeferredBlock<BerryBushBlock> COFFEE_BUSH =
            berryBush("coffee_bush", () -> ModItems.COFFEE_CHERRIES.get(), COFFEE_SOIL);

    // ==================================================================
    //  野生作物（12）—— 复用 Farmer's Delight 的 WildCropBlock
    //    第二个参数是"迷之炖菜"效果，第三个是时长（秒），取值风格与 FD 的野生作物一致。
    // ==================================================================
    public static final DeferredBlock<WildCropBlock> WILD_CORN = wildCrop("wild_corn", MobEffects.DIG_SPEED, 6);
    public static final DeferredBlock<WildCropBlock> WILD_PEANUT = wildCrop("wild_peanut", MobEffects.JUMP, 6);
    public static final DeferredBlock<WildCropBlock> WILD_CHILI = wildCrop("wild_chili", MobEffects.FIRE_RESISTANCE, 8);
    public static final DeferredBlock<WildCropBlock> WILD_STRAWBERRY = wildCrop("wild_strawberry", MobEffects.REGENERATION, 6);
    public static final DeferredBlock<WildCropBlock> WILD_PINEAPPLE = wildCrop("wild_pineapple", MobEffects.ABSORPTION, 6);
    public static final DeferredBlock<WildCropBlock> WILD_SOYBEAN = wildCrop("wild_soybean", MobEffects.SATURATION, 6);
    public static final DeferredBlock<WildCropBlock> WILD_CUCUMBER = wildCrop("wild_cucumber", MobEffects.WATER_BREATHING, 8);
    public static final DeferredBlock<WildCropBlock> WILD_WHITE_GRAPE = wildCrop("wild_white_grape", MobEffects.NIGHT_VISION, 8);
    public static final DeferredBlock<WildCropBlock> WILD_RED_GRAPE = wildCrop("wild_red_grape", MobEffects.DAMAGE_BOOST, 8);
    public static final DeferredBlock<WildCropBlock> WILD_TEA = wildCrop("wild_tea", MobEffects.DAMAGE_RESISTANCE, 8);
    public static final DeferredBlock<WildCropBlock> WILD_BLUEBERRY = wildCrop("wild_blueberry", MobEffects.HEALTH_BOOST, 6);
    public static final DeferredBlock<WildCropBlock> WILD_COFFEE = wildCrop("wild_coffee", MobEffects.MOVEMENT_SPEED, 10);

    // ==================================================================
    //  注册辅助
    // ==================================================================

    private static DeferredBlock<SeedCropBlock> seedCrop(String name, Supplier<? extends Item> seeds) {
        return BLOCKS.registerBlock(name, props -> new SeedCropBlock(props, seeds), cropProperties());
    }

    private static DeferredBlock<RopeCropBlock> ropeCrop(String name, Supplier<? extends Item> seeds,
                                                         Supplier<? extends Item> produce,
                                                         Supplier<? extends Block> vine) {
        return BLOCKS.registerBlock(name, props -> new RopeCropBlock(props, seeds, produce, vine), cropProperties());
    }

    private static DeferredBlock<RopeVineBlock> ropeVine(String name, Supplier<? extends Item> seeds,
                                                         Supplier<? extends Item> produce,
                                                         Supplier<? extends Block> baseCrop) {
        return BLOCKS.registerBlock(name,
                props -> new RopeVineBlock(props, seeds, produce, baseCrop, FD_ROPE), cropProperties());
    }

    private static DeferredBlock<BerryBushBlock> berryBush(String name, Supplier<? extends Item> fruit,
                                                           Predicate<BlockState> soil) {
        return BLOCKS.registerBlock(name, props -> new BerryBushBlock(props, fruit, soil), bushProperties());
    }

    /** 野生作物：属性照抄 FD（{@code ofFullCopy(Blocks.TALL_GRASS)}）。 */
    private static DeferredBlock<WildCropBlock> wildCrop(String name, Holder<MobEffect> stewEffect, int duration) {
        return BLOCKS.registerBlock(name,
                props -> new WildCropBlock(stewEffect, duration, props),
                BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS));
    }

    /** 作物方块：无碰撞、随机刻、瞬间破坏、作物音效。 */
    private static BlockBehaviour.Properties cropProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion();
    }

    /** 灌木方块：与作物相同，只是换成浆果丛的音效。 */
    private static BlockBehaviour.Properties bushProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.SWEET_BERRY_BUSH)
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion();
    }

    private ModBlocks() {
    }
}
