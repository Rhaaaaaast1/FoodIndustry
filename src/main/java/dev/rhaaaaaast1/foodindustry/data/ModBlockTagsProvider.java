package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.registry.ModBlocks;
import dev.rhaaaaaast1.foodindustry.common.tag.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
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
                ModBlocks.EGGPLANT.get(),
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

        // 野生作物同时也是"小黄花"级别的花：蜜蜂会去采、能被种进花盆、
        // 各类"附近有花"的判定（骨粉催树、蜜蜂繁殖）也认。
        // 这是原版标签，所以路径写成 minecraft:small_flowers。
        tag(net.minecraft.tags.BlockTags.SMALL_FLOWERS).add(
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
    }
}
