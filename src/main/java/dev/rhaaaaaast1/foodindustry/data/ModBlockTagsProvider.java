package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.registry.ModBlocks;
import dev.rhaaaaaast1.foodindustry.common.tag.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 方块标签生成。
 *
 * <p>把作物方块挂进原版的 {@code minecraft:crops}，这样别的模组按统一规则就能识别它们
 * （例如某些模组的"自动收割"功能）。</p>
 */
public class ModBlockTagsProvider extends BlockTagsProvider {

    // ------------------------------------------------------------------
    // 各家 Farmer's Delight 系模组的"沃土"方块 id。
    // 一律用 required=false 写进标签：没装那个模组时条目会被自动忽略，不会报错。
    // 以后要支持新的"乐事"只在这里加一行。
    // ------------------------------------------------------------------
    /** 农夫乐事：沃土（未耕地）。 */
    private static final ResourceLocation FD_RICH_SOIL =
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "rich_soil");
    /** 农夫乐事：沃土农田。 */
    private static final ResourceLocation FD_RICH_SOIL_FARMLAND =
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "rich_soil_farmland");
    /** 我的下界乐事：复活沃土。 */
    private static final ResourceLocation MND_RESURGENT_SOIL =
            ResourceLocation.fromNamespaceAndPath("mynethersdelight", "resurgent_soil");
    /** 我的下界乐事：复活沃土农田。 */
    private static final ResourceLocation MND_RESURGENT_SOIL_FARMLAND =
            ResourceLocation.fromNamespaceAndPath("mynethersdelight", "resurgent_soil_farmland");
    /** 下界乐事（本整合包未安装，先挂上，装了自动生效）。 */
    private static final ResourceLocation NETHERSDELIGHT_RICH_SOUL_SOIL =
            ResourceLocation.fromNamespaceAndPath("nethersdelight", "rich_soul_soil");

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, FoodIndustry.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Blocks.CROPS).add(
                ModBlocks.CORN.get(),
                ModBlocks.PEANUT.get(),
                ModBlocks.CHILI.get(),
                ModBlocks.PINEAPPLE.get(),
                ModBlocks.SOYBEAN.get(),
                ModBlocks.CUCUMBER.get(),
                ModBlocks.CUCUMBER_VINE.get(),
                ModBlocks.WHITE_GRAPE.get(),
                ModBlocks.WHITE_GRAPE_VINE.get(),
                ModBlocks.RED_GRAPE.get(),
                ModBlocks.RED_GRAPE_VINE.get(),
                ModBlocks.TEA_BUSH.get(),
                ModBlocks.BLUEBERRY_BUSH.get(),
                ModBlocks.COFFEE_BUSH.get());

        // 中间形态的作物也算"作物"（原版方块标签）
        tag(ModTags.Blocks.CROPS).add(ModBlocks.SUNFLOWER_CROP.get());
        // 注意：草莓现在和蓝莓/茶一样是**树丛**，不算"作物"，所以不挂这个标签

        // 野生作物：挂进农夫乐事的 #farmersdelight:wild_crops，别的模组（含 FD 自己）就能识别
        tag(ModTags.Blocks.WILD_CROPS).add(
                ModBlocks.WILD_CORN.get(),
                ModBlocks.WILD_PEANUT.get(),
                ModBlocks.WILD_CHILI.get(),
                ModBlocks.WILD_STRAWBERRY.get(),
                ModBlocks.WILD_PINEAPPLE.get(),
                ModBlocks.WILD_SOYBEAN.get(),
                ModBlocks.WILD_CUCUMBER.get(),
                ModBlocks.WILD_WHITE_GRAPE.get(),
                ModBlocks.WILD_RED_GRAPE.get(),
                ModBlocks.WILD_TEA.get(),
                ModBlocks.WILD_BLUEBERRY.get(),
                ModBlocks.WILD_COFFEE.get());

        // 咖啡的土壤：灰化土（原版必备）+ 各家乐事的沃土（装了才生效）
        tag(ModTags.Blocks.COFFEE_SOIL)
                .add(Blocks.PODZOL)
                .addOptional(FD_RICH_SOIL)
                .addOptional(FD_RICH_SOIL_FARMLAND)
                .addOptional(MND_RESURGENT_SOIL)
                .addOptional(MND_RESURGENT_SOIL_FARMLAND)
                .addOptional(NETHERSDELIGHT_RICH_SOUL_SOIL);
    }
}
