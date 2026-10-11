package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.registry.ModFluids;
import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import dev.rhaaaaaast1.foodindustry.common.tag.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 物品标签生成。
 *
 * <p>通用标签（{@code c:seeds}、{@code c:crops/<名字>}、{@code c:fruits} 等）是模组之间互相识别的
 * 公共语言；野生作物还会挂进农夫乐事的 {@code #farmersdelight:wild_crops}（物品标签）。</p>
 */
public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, FoodIndustry.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // ---------------- 种子 ----------------
        tag(ModTags.Items.SEEDS).add(
                ModItems.CORN_KERNELS.get(),
                ModItems.CHILI_SEEDS.get(),
                ModItems.STRAWBERRY_SEEDS.get(),
                ModItems.CUCUMBER_SEEDS.get(),
                ModItems.WHITE_GRAPE_SEEDS.get(),
                ModItems.RED_GRAPE_SEEDS.get(),
                ModItems.BLUEBERRY_SEEDS.get(),
                ModItems.TEA_SEEDS.get(),
                ModItems.SUNFLOWER_SEEDS.get(),
                ModItems.PINEAPPLE_CROWN.get(),
                ModItems.PINEAPPLE_SAPLING.get(),
                // 产物即种子
                ModItems.PEANUTS.get(),
                ModItems.SOYBEANS.get(),
                ModItems.RAW_COFFEE_BEANS.get());

        tag(ModTags.Items.seed("corn")).add(ModItems.CORN_KERNELS.get());
        tag(ModTags.Items.seed("peanut")).add(ModItems.PEANUTS.get());
        tag(ModTags.Items.seed("chili")).add(ModItems.CHILI_SEEDS.get());
        tag(ModTags.Items.seed("strawberry")).add(ModItems.STRAWBERRY_SEEDS.get());
        tag(ModTags.Items.seed("pineapple")).add(ModItems.PINEAPPLE_CROWN.get(), ModItems.PINEAPPLE_SAPLING.get());
        tag(ModTags.Items.seed("soybean")).add(ModItems.SOYBEANS.get());
        tag(ModTags.Items.seed("cucumber")).add(ModItems.CUCUMBER_SEEDS.get());
        tag(ModTags.Items.seed("white_grape")).add(ModItems.WHITE_GRAPE_SEEDS.get());
        tag(ModTags.Items.seed("red_grape")).add(ModItems.RED_GRAPE_SEEDS.get());
        tag(ModTags.Items.seed("blueberry")).add(ModItems.BLUEBERRY_SEEDS.get());
        tag(ModTags.Items.seed("tea")).add(ModItems.TEA_SEEDS.get());
        tag(ModTags.Items.seed("sunflower")).add(ModItems.SUNFLOWER_SEEDS.get());
        tag(ModTags.Items.seed("coffee")).add(ModItems.RAW_COFFEE_BEANS.get());

        // ---------------- 作物产物 ----------------
        tag(ModTags.Items.CROPS).add(
                ModItems.CORN.get(),
                ModItems.PEANUTS.get(),
                ModItems.CHILI_PEPPER.get(),
                ModItems.STRAWBERRIES.get(),
                ModItems.PINEAPPLE.get(),
                ModItems.SOYBEANS.get(),
                ModItems.CUCUMBER.get(),
                ModItems.TEA_LEAVES.get(),
                ModItems.COFFEE_CHERRIES.get(),
                ModItems.BLUEBERRIES.get(),
                ModItems.WHITE_GRAPES.get(),
                ModItems.RED_GRAPES.get());

        tag(ModTags.Items.crop("corn")).add(ModItems.CORN.get());
        tag(ModTags.Items.crop("peanut")).add(ModItems.PEANUTS.get());
        tag(ModTags.Items.crop("chili")).add(ModItems.CHILI_PEPPER.get());
        tag(ModTags.Items.crop("strawberry")).add(ModItems.STRAWBERRIES.get());
        tag(ModTags.Items.crop("pineapple")).add(ModItems.PINEAPPLE.get());
        tag(ModTags.Items.crop("soybean")).add(ModItems.SOYBEANS.get());
        tag(ModTags.Items.crop("cucumber")).add(ModItems.CUCUMBER.get());
        tag(ModTags.Items.crop("blueberry")).add(ModItems.BLUEBERRIES.get());
        tag(ModTags.Items.crop("white_grape")).add(ModItems.WHITE_GRAPES.get());
        tag(ModTags.Items.crop("red_grape")).add(ModItems.RED_GRAPES.get());
        tag(ModTags.Items.crop("tea")).add(ModItems.TEA_LEAVES.get());
        tag(ModTags.Items.crop("coffee")).add(ModItems.COFFEE_CHERRIES.get());

        // ---------------- 水果 / 蔬菜 ----------------
        tag(ModTags.Items.FRUITS).add(
                ModItems.STRAWBERRIES.get(),
                ModItems.PINEAPPLE.get(),
                ModItems.PINEAPPLE_SLICE.get(),
                ModItems.BLUEBERRIES.get(),
                ModItems.WHITE_GRAPES.get(),
                ModItems.RED_GRAPES.get());
        tag(ModTags.Items.fruit("strawberry")).add(ModItems.STRAWBERRIES.get());
        tag(ModTags.Items.fruit("pineapple")).add(ModItems.PINEAPPLE.get(), ModItems.PINEAPPLE_SLICE.get());
        tag(ModTags.Items.fruit("blueberry")).add(ModItems.BLUEBERRIES.get());
        tag(ModTags.Items.fruit("grape")).add(ModItems.WHITE_GRAPES.get(), ModItems.RED_GRAPES.get());

        tag(ModTags.Items.VEGETABLES).add(
                ModItems.CORN.get(),
                ModItems.PEANUTS.get(),
                ModItems.CHILI_PEPPER.get(),
                ModItems.SOYBEANS.get(),
                ModItems.CUCUMBER.get());

        // ---------------- 流体的容器（桶 / 瓶 / 碗）----------------
        for (ModFluids.MilkLike milkLike : ModFluids.allMilkLike()) {
            if (milkLike.bucket() != null) {
                tag(ModTags.Items.BUCKETS).add(milkLike.bucket().get());
            }
            if (milkLike.bowl() != null) {
                // 碗装的酱算食物
                tag(ModTags.Items.FOODS).add(milkLike.bowl().get());
            }
            if (milkLike.drinkable().isDrink()) {
                // 汁类 / 豆浆 / 山羊奶算饮品；酱类和食用油不算
                for (var container : milkLike.containers()) {
                    tag(ModTags.Items.DRINKS).add(container.get());
                }
            }
        }
        // 山羊奶桶额外挂进"奶桶 / 奶类饮品"标签（和原版牛奶桶一个待遇）
        tag(ModTags.Items.DRINKS_MILK).add(ModFluids.GOAT_MILK.bucket().get());
        tag(ModTags.Items.BUCKETS_MILK).add(ModFluids.GOAT_MILK.bucket().get());

        // ---------------- 野生作物（与农夫乐事的野生作物同一标签）----------------
        tag(ModTags.Items.WILD_CROPS).add(
                ModItems.WILD_CORN.get(),
                ModItems.WILD_PEANUT.get(),
                ModItems.WILD_CHILI.get(),
                ModItems.WILD_STRAWBERRY.get(),
                ModItems.WILD_PINEAPPLE.get(),
                ModItems.WILD_SOYBEAN.get(),
                ModItems.WILD_CUCUMBER.get(),
                ModItems.WILD_WHITE_GRAPE.get(),
                ModItems.WILD_RED_GRAPE.get(),
                ModItems.WILD_TEA.get(),
                ModItems.WILD_BLUEBERRY.get(),
                ModItems.WILD_COFFEE.get());
    }
}
