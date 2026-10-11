package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.block.crop.BerryBushBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.RegrowingCropBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.RopeCropBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.RopeVineBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.TallFlowerCropBlock;
import dev.rhaaaaaast1.foodindustry.common.registry.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 方块状态与方块模型生成。
 *
 * <p>所有作物都用"十字"模型 + 分档贴图：**每个作物只画 4 张贴图**（{@code <名字>_stage0..3}），
 * 各个 age 按比例映射到这 4 档（原版作物也是这么省的）。</p>
 *
 * <p>作物没有 BlockItem（种下去的是种子物品），所以这里不生成方块物品模型。</p>
 */
public class ModBlockStateProvider extends BlockStateProvider {

    private static final int STAGE_COUNT = 4;

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, FoodIndustry.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // ---- 普通作物（8 个生长阶段，映射到 4 档贴图）----
        crop(ModBlocks.CORN.get(), CropBlock.AGE);
        crop(ModBlocks.PEANUT.get(), CropBlock.AGE);
        crop(ModBlocks.CHILI.get(), RegrowingCropBlock.AGE);   // 辣椒：4 阶段，成熟后右键采摘
        pineapple();   // 菠萝：成熟阶是"植株 + 顶上菠萝"的自定义模型
        crop(ModBlocks.SOYBEAN.get(), CropBlock.AGE);
        crop(ModBlocks.SUNFLOWER_CROP.get(), TallFlowerCropBlock.AGE);   // 向日葵：4 阶段，成熟后开花成原版植物

        // ---- 攀藤作物：底部作物 + 藤（4 个生长阶段）----
        crop(ModBlocks.CUCUMBER.get(), RopeCropBlock.VINE_AGE);
        ropeVine(ModBlocks.CUCUMBER_VINE.get(), RopeVineBlock.VINE_AGE);
        crop(ModBlocks.WHITE_GRAPE.get(), RopeCropBlock.VINE_AGE);
        ropeVine(ModBlocks.WHITE_GRAPE_VINE.get(), RopeVineBlock.VINE_AGE);
        crop(ModBlocks.RED_GRAPE.get(), RopeCropBlock.VINE_AGE);
        ropeVine(ModBlocks.RED_GRAPE_VINE.get(), RopeVineBlock.VINE_AGE);

        // ---- 灌木（4 个生长阶段）----
        crop(ModBlocks.TEA_BUSH.get(), BerryBushBlock.AGE);
        crop(ModBlocks.BLUEBERRY_BUSH.get(), BerryBushBlock.AGE);
        crop(ModBlocks.STRAWBERRY_BUSH.get(), BerryBushBlock.AGE);
        crop(ModBlocks.COFFEE_BUSH.get(), BerryBushBlock.AGE);

        // ---- 野生作物（单变体十字模型，与 FD 的野生作物一致）----
        wildCrop(ModBlocks.WILD_CORN.get());
        wildCrop(ModBlocks.WILD_PEANUT.get());
        wildCrop(ModBlocks.WILD_CHILI.get());
        wildCrop(ModBlocks.WILD_STRAWBERRY.get());
        wildCrop(ModBlocks.WILD_PINEAPPLE.get());
        wildCrop(ModBlocks.WILD_SOYBEAN.get());
        wildCrop(ModBlocks.WILD_CUCUMBER.get());
        wildCrop(ModBlocks.WILD_WHITE_GRAPE.get());
        wildCrop(ModBlocks.WILD_RED_GRAPE.get());
        wildCrop(ModBlocks.WILD_TEA.get());
        wildCrop(ModBlocks.WILD_BLUEBERRY.get());
        wildCrop(ModBlocks.WILD_COFFEE.get());
    }

    /** 野生作物：一张贴图的十字模型。 */
    private void wildCrop(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        simpleBlock(block, models().cross(name, modLoc("block/" + name)).renderType("cutout"));
    }

    /**
     * 菠萝专用：前 3 阶是普通十字模型；成熟阶（age 3）用**手写模型**
     * {@code models/block/pineapple_stage3.json} —— 十字叶面 + 顶上挂一个菠萝方块。
     * 收获后回到 age 2，正好就是"把顶上那个菠萝摘走、植株留着"。
     */
    private void pineapple() {
        ModelFile[] stages = new ModelFile[RegrowingCropBlock.MAX_AGE];
        for (int stage = 0; stage < stages.length; stage++) {
            stages[stage] = models()
                    .cross("pineapple_stage" + stage, modLoc("block/pineapple_stage" + stage))
                    .renderType("cutout");
        }
        ModelFile mature = models().getExistingFile(modLoc("block/pineapple_stage3"));

        var builder = getVariantBuilder(ModBlocks.PINEAPPLE.get());
        for (int age = 0; age <= RegrowingCropBlock.MAX_AGE; age++) {
            ModelFile model = age == RegrowingCropBlock.MAX_AGE ? mature : stages[age];
            builder.partialState()
                    .with(RegrowingCropBlock.AGE, age)
                    .modelForState()
                    .modelFile(model)
                    .addModel();
        }
    }

    /**
     * 绳上的藤：blockstate 指向**手写模型** {@code models/block/<藤>_stage<n>.json}，
     * 那些模型用父模板 {@code foodindustry:block/template_crop_on_rope}
     * （= 绳子方盒 + 两片绕 Y 轴 45° 的作物十字面）。
     *
     * <p><b>为什么需要它</b>：藤方块是**取代**那一格绳子生成的 ✓，如果模型里不画绳子 ✗，
     * 绳子就会在视觉上凭空消失 ✗。Farmer's Delight 的番茄藤正是这么处理的 ✓ ——
     * 它的 {@code template_crop_with_rope} 里就有一个 2×16×2 的绳子方盒 ✓。</p>
     *
     * <p><b>为什么模型要手写、不在这里生成</b>：datagen 写 texture 时会**校验贴图必须存在于已知资源包** ✗，
     * 而绳子贴图是 Farmer's Delight 的（不在我们 datagen 的资源里）→ 生成会直接抛
     * {@code IllegalArgumentException: Texture farmersdelight:block/rope does not exist} ✓。
     * 所以照**菠萝成熟阶**同一个套路 ✓：模型手写，这里只用 {@code getExistingFile} 引用 ✓。</p>
     */
    private void ropeVine(Block block, IntegerProperty ageProperty) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile[] stages = new ModelFile[STAGE_COUNT];
        for (int stage = 0; stage < STAGE_COUNT; stage++) {
            stages[stage] = models().getExistingFile(modLoc("block/" + name + "_stage" + stage));
        }

        int maxAge = ageProperty.getPossibleValues().stream().max(Integer::compareTo).orElse(0);
        var builder = getVariantBuilder(block);
        for (int age = 0; age <= maxAge; age++) {
            int stage = Math.min(STAGE_COUNT - 1, age * STAGE_COUNT / (maxAge + 1));
            builder.partialState()
                    .with(ageProperty, age)
                    .modelForState()
                    .modelFile(stages[stage])
                    .addModel();
        }
    }

    /** 为作物类方块生成"按 age 切换 4 档十字模型"的 blockstate。 */
    private void crop(Block block, IntegerProperty ageProperty) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile[] stages = new ModelFile[STAGE_COUNT];
        for (int stage = 0; stage < STAGE_COUNT; stage++) {
            stages[stage] = models()
                    .cross(name + "_stage" + stage, modLoc("block/" + name + "_stage" + stage))
                    .renderType("cutout");
        }

        int maxAge = ageProperty.getPossibleValues().stream().max(Integer::compareTo).orElse(0);
        var builder = getVariantBuilder(block);
        for (int age = 0; age <= maxAge; age++) {
            int stage = Math.min(STAGE_COUNT - 1, age * STAGE_COUNT / (maxAge + 1));
            builder.partialState()
                    .with(ageProperty, age)
                    .modelForState()
                    .modelFile(stages[stage])
                    .addModel();
        }
    }
}
