package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

/**
 * 配方生成。
 *
 * <p>这里只生成**工作台**配方（从产物里取出各自的种子）。
 * 砧板配方是手写 JSON（在 {@code src/main/resources/data/foodindustry/recipe/cutting/}），
 * 因为农夫乐事的砧板格式字段较多，直接照它的数据格式写更直观。</p>
 */
public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // 蓝莓、辣椒、草莓、黄瓜、白葡萄、红葡萄：一份产物 → 一份种子
        seedFrom(recipeOutput, ModItems.BLUEBERRIES.get(), ModItems.BLUEBERRY_SEEDS.get());
        seedFrom(recipeOutput, ModItems.CHILI_PEPPER.get(), ModItems.CHILI_SEEDS.get());
        seedFrom(recipeOutput, ModItems.STRAWBERRIES.get(), ModItems.STRAWBERRY_SEEDS.get());
        seedFrom(recipeOutput, ModItems.CUCUMBER.get(), ModItems.CUCUMBER_SEEDS.get());
        seedFrom(recipeOutput, ModItems.WHITE_GRAPES.get(), ModItems.WHITE_GRAPE_SEEDS.get());
        seedFrom(recipeOutput, ModItems.RED_GRAPES.get(), ModItems.RED_GRAPE_SEEDS.get());
        // 咖啡不在这里：咖啡果是**砧板切**成 8 颗生咖啡豆（手写 cutting 配方），
        // 玉米也不在这里：玉米棒是**砧板切**成 32 颗玉米粒（手写 cutting 配方）。
        // 生豆烘焙 → Create 石磨磨粉 → Create 混合盆加热成咖啡流体（见 compat/create 的 provider）。
        roastCoffeeBeans(recipeOutput);

        FoodIndustry.LOGGER.debug("[{}] 工作台配方生成完毕", FoodIndustry.MODID);
    }

    /** 无序合成：一份产物取出一份种子。 */
    private void seedFrom(RecipeOutput output, Item produce, Item seeds) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, seeds)
                .requires(produce)
                .unlockedBy("has_" + path(produce), has(produce))
                .save(output, FoodIndustry.id(path(seeds)));
    }

    /**
     * 生咖啡豆 → 烘焙咖啡豆：**熔炉 / 烟熏炉 / 营火**三种都做。
     *
     * <p>数值与原版"做熟食物"完全一致（实测原版数据 + Farmer's Delight 自己的熟食配方）：
     * 经验都是 {@code 0.35}；时间分别是熔炉 {@code 200}、烟熏炉 {@code 100}、营火 {@code 600} 刻。</p>
     */
    private void roastCoffeeBeans(RecipeOutput output) {
        Ingredient raw = Ingredient.of(ModItems.RAW_COFFEE_BEANS.get());
        Item roasted = ModItems.ROASTED_COFFEE_BEANS.get();

        SimpleCookingRecipeBuilder.smelting(raw, RecipeCategory.FOOD, roasted, 0.35F, 200)
                .unlockedBy("has_raw_coffee_beans", has(ModItems.RAW_COFFEE_BEANS.get()))
                .save(output, FoodIndustry.id("roasted_coffee_beans_from_smelting"));
        SimpleCookingRecipeBuilder.smoking(raw, RecipeCategory.FOOD, roasted, 0.35F, 100)
                .unlockedBy("has_raw_coffee_beans", has(ModItems.RAW_COFFEE_BEANS.get()))
                .save(output, FoodIndustry.id("roasted_coffee_beans_from_smoking"));
        SimpleCookingRecipeBuilder.campfireCooking(raw, RecipeCategory.FOOD, roasted, 0.35F, 600)
                .unlockedBy("has_raw_coffee_beans", has(ModItems.RAW_COFFEE_BEANS.get()))
                .save(output, FoodIndustry.id("roasted_coffee_beans_from_campfire_cooking"));
    }

    private static String path(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }
}
