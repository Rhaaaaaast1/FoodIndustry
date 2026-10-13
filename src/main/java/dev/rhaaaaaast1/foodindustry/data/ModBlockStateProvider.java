package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.block.crop.BerryBushBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.RegrowingCropBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.RopeCropBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.RopeVineBlock;
import dev.rhaaaaaast1.foodindustry.common.block.crop.TallFlowerCropBlock;
import dev.rhaaaaaast1.foodindustry.common.registry.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
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
 * <p>所有作物都用"十字"模型 + 分档贴图，生长阶段统一是**原版作物的 8 阶**
 * （{@code age 0..7}，同原版小麦/胡萝卜）：**每个作物只画 4 张贴图**
 * （{@code <名字>_stage0..3}），8 个 age 按比例映射到这 4 档
 * —— 原版胡萝卜/马铃薯正是这么省的 ✓。</p>
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
        // ---- 普通作物（原版 8 阶，映射到 4 档贴图）----
        crop(ModBlocks.CORN.get(), CropBlock.AGE);
        crop(ModBlocks.PEANUT.get(), CropBlock.AGE);
        crop(ModBlocks.CHILI.get(), CropBlock.AGE);   // 辣椒：原版作物（8 阶映射到 4 档贴图）
        crop(ModBlocks.EGGPLANT.get(), CropBlock.AGE);   // 茄子：与辣椒同规则
        pineapple();   // 菠萝：成熟阶是"植株 + 顶上菠萝"的自定义模型
        crop(ModBlocks.SOYBEAN.get(), CropBlock.AGE);
        crop(ModBlocks.SUNFLOWER_CROP.get(), TallFlowerCropBlock.AGE);   // 向日葵：原版 8 阶，成熟后开花成原版植物

        // ---- 攀藤作物：底部作物 + 藤（原版 8 阶，映射到 4 档贴图）----
        crop(ModBlocks.CUCUMBER.get(), RopeCropBlock.VINE_AGE);
        ropeVine(ModBlocks.CUCUMBER_VINE.get(), RopeVineBlock.VINE_AGE);
        crop(ModBlocks.WHITE_GRAPE.get(), RopeCropBlock.VINE_AGE);
        ropeVine(ModBlocks.WHITE_GRAPE_VINE.get(), RopeVineBlock.VINE_AGE);
        crop(ModBlocks.RED_GRAPE.get(), RopeCropBlock.VINE_AGE);
        ropeVine(ModBlocks.RED_GRAPE_VINE.get(), RopeVineBlock.VINE_AGE);

        // ---- 灌木（原版 8 阶，映射到 4 档贴图）----
        crop(ModBlocks.TEA_BUSH.get(), BerryBushBlock.AGE);
        crop(ModBlocks.BLUEBERRY_BUSH.get(), BerryBushBlock.AGE);
        crop(ModBlocks.STRAWBERRY_BUSH.get(), BerryBushBlock.AGE);
        crop(ModBlocks.COFFEE_BUSH.get(), BerryBushBlock.AGE);

        // ---- 发酵木桶 ----

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
    // ⚠️ 发酵木桶的 blockstate 生成（fermentationBarrel()）已随木桶一起暂时移除。
    //    它的模型曾复用原版木桶贴图（barrel_bottom/side/top + barrel_top_open），
    //    并用 rotationX(x).rotationY(y) 照抄原版木桶的 6 向旋转 —— 那些代码在
    //    E:\dev\foodindustry-tools\barrel-backup\ 里没有单独留（在 git 历史里）。

    private void wildCrop(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        simpleBlock(block, models().cross(name, modLoc("block/" + name)).renderType("cutout"));
    }

    /**
     * 菠萝专用：未成熟的阶是普通十字模型；**成熟阶（{@code age 7}）**用**手写模型**
     * {@code models/block/pineapple_mature.json} —— 十字叶面 + 顶上挂一个菠萝方块。
     * 收获后回退一阶（到 age 6），正好就是"把顶上那个菠萝摘走、植株留着"。
     *
     * <p>为什么未成熟阶是 {@code 0..6} 而不是 {@code 0..7}：菠萝同其他作物一样是
     * **原版 8 阶**（{@code age 0..7}），而最高那一阶要用"顶上挂果实"的手写模型，
     * 所以普通贴图只覆盖前 7 阶 ✓。贴图只有 {@code pineapple_stage0..3} 四张，
     * 所以按 {@code age * 4 / 8} 映射上去 —— **除数必须是 8（总阶数）而不是 7**，
     * 否则 {@code age=6} 会算出 stage 3 而永远不会被赋值 ✗。</p>
     *
     * <p><b>成熟模型的文件名不能叫 {@code pineapple_stage3}</b> ✗：那样会和本方法
     * 生成的 {@code pineapple_stage3} 十字模型**撞名**，Gradle 的 {@code processResources}
     * 会因为"main 与 generated 出现重复条目"直接构建失败 ✗（实测踩过）。
     * 所以手写模型改名为 {@code pineapple_mature} ✓。</p>
     */
    private void pineapple() {
        int maxAge = RegrowingCropBlock.MAX_AGE;   // 7
        ModelFile[] stages = new ModelFile[STAGE_COUNT];
        for (int stage = 0; stage < STAGE_COUNT; stage++) {
            stages[stage] = models()
                    .cross("pineapple_stage" + stage, modLoc("block/pineapple_stage" + stage))
                    .renderType("cutout");
        }
        ModelFile mature = models().getExistingFile(modLoc("block/pineapple_mature"));

        var builder = getVariantBuilder(ModBlocks.PINEAPPLE.get());
        for (int age = 0; age <= maxAge; age++) {
            ModelFile model = age == maxAge ? mature : stages[age * STAGE_COUNT / (maxAge + 1)];
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
