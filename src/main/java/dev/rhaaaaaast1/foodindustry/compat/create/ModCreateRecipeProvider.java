package dev.rhaaaaaast1.foodindustry.compat.create;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.item.FluidContainerItemHandler;
import dev.rhaaaaaast1.foodindustry.common.registry.ModFluids;
import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import dev.rhaaaaaast1.foodindustry.compat.registry.CompatFluids;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 生成 **Create 的注液（filling）/ 排空（emptying）配方**。
 *
 * <p>这些配方用的是 **Create 的配方类型**，谈不上"本模组自己的配方"，
 * 所以代码放在 {@code compat/create} 包、数据生成到
 * {@code data/foodindustry/recipe/compat/create/…} 下，和本模组自己的配方分开管理
 * （Farmer's Delight 本体也用 {@code recipe/integration/create/…} 这种分法）。</p>
 *
 * <p><b>格式来源</b>：不是 Create 的源码 —— 而是**别的 Create 附属已发布的数据文件**
 * （CreateDragonsPlus 的 {@code recipe/filling/*.json}）以及 Farmer's Delight 自己的
 * Create 集成配方，再加上 NeoForge 源码里的条件定义。</p>
 *
 * <pre>{@code
 * 注液: { "type": "create:filling",
 *        "ingredients": [ {"item": 空容器}, {"type": "neoforge:single", "amount": 250, "fluid": 流体} ],
 *        "results": [ {"id": 成品} ] }
 * 排空: { "type": "create:emptying",
 *        "ingredients": [ {"item": 成品} ],
 *        "results": [ {"id": 空容器}, {"amount": 250, "id": 流体} ] }
 * }</pre>
 *
 * <p>所有配方都带 {@code neoforge:mod_loaded: create} 条件；涉及别的模组物品的（番茄酱 / 紫颂酱）
 * 再额外加那个模组的条件 —— 没装时配方不会被加载，也不会刷"未知物品/未知配方"的报错。</p>
 */
public class ModCreateRecipeProvider implements DataProvider {

    /** 注液/排空一次的量，与容器容量共用同一个来源。 */
    private static final int AMOUNT = FluidContainerItemHandler.CAPACITY;

    /** compat：FD 的番茄酱（碗装食品）。 */
    private static final ResourceLocation FD_TOMATO_SAUCE =
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "tomato_sauce");
    /** compat：End's Delight 的紫颂酱（同样按 FD 的碗装食品惯例）。 */
    private static final ResourceLocation ED_CHORUS_SAUCE =
            ResourceLocation.fromNamespaceAndPath("ends_delight", "chorus_sauce");

    private final PackOutput output;

    public ModCreateRecipeProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        PackOutput.PathProvider filling =
                output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe/compat/create/filling");
        PackOutput.PathProvider emptying =
                output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe/compat/create/emptying");
        PackOutput.PathProvider milling =
                output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe/compat/create/milling");
        PackOutput.PathProvider mixing =
                output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe/compat/create/mixing");

        List<CompletableFuture<?>> tasks = new ArrayList<>();

        // ---------------- 本模组自己的容器：空玻璃瓶/空碗 ⇄ 我们的成品 ----------------
        for (ModFluids.MilkLike milkLike : ModFluids.allMilkLike()) {
            ResourceLocation fluid = id(milkLike.source().get());
            ResourceLocation name = FoodIndustry.id(milkLike.name());
            if (milkLike.bottle() != null) {
                pair(tasks, cache, filling, emptying, name, fluid,
                        id(Items.GLASS_BOTTLE), id(milkLike.bottle().get()));
            }
            if (milkLike.bowl() != null) {
                pair(tasks, cache, filling, emptying, name, fluid,
                        id(Items.BOWL), id(milkLike.bowl().get()));
            }
        }

        // ---------------- compat：我们自己没有物品，产出给对方模组的物品 ----------------
        // 番茄酱：FD 有物品 farmersdelight:tomato_sauce（碗装，见 FD 自己的 create:mixing 集成配方）
        pair(tasks, cache, filling, emptying, FoodIndustry.id("tomato_sauce"),
                id(CompatFluids.TOMATO_SAUCE.source().get()), id(Items.BOWL), FD_TOMATO_SAUCE, "farmersdelight");
        // 紫颂酱：End's Delight 有物品 ends_delight:chorus_sauce —— 没装 ED 时整条配方不加载
        pair(tasks, cache, filling, emptying, FoodIndustry.id("chorus_sauce"),
                id(CompatFluids.CHORUS_SAUCE.source().get()), id(Items.BOWL), ED_CHORUS_SAUCE, "ends_delight");

        // ---------------- Create 加工链：石磨磨粉 → 加热混合成咖啡 ----------------
        // 烘焙豆 --(石磨, 1:1)--> 咖啡粉 --(混合盆 + 1000mB 水, 需加热)--> 1000mB 咖啡
        ResourceLocation roasted = id(ModItems.ROASTED_COFFEE_BEANS.get());
        ResourceLocation powder = id(ModItems.COFFEE_POWDER.get());
        tasks.add(milling(cache, milling.json(FoodIndustry.id("coffee_powder")), roasted, powder, 50));
        tasks.add(mixing(cache, mixing.json(FoodIndustry.id("coffee")),
                List.of(itemIngredient(powder), fluidIngredient(id(Fluids.WATER), 1000)),
                List.of(fluidResult(id(ModFluids.COFFEE.source().get()), 1000))));

        return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Create 注液/排空配方（" + FoodIndustry.MODID + "，放在 compat/create 下）";
    }

    // ==================================================================
    //  json 组装
    // ==================================================================

    /**
     * 一对"注液 + 排空"配方（同一流体、同一空容器）。
     *
     * <p>原先四处各写两行 ✗，抽到这里之后每处只写一行 ✓，
     * 而且两个方向**永远成对生成** ✓ —— 不会再出现"加了注液却忘了排空" ✓。</p>
     */
    private static void pair(List<CompletableFuture<?>> tasks, CachedOutput cache,
                             PackOutput.PathProvider fillingPaths, PackOutput.PathProvider emptyingPaths,
                             ResourceLocation id, ResourceLocation fluid, ResourceLocation emptyContainer,
                             ResourceLocation filledItem, String... requiredMods) {
        tasks.add(filling(cache, fillingPaths.json(id), fluid, emptyContainer, filledItem, requiredMods));
        tasks.add(emptying(cache, emptyingPaths.json(id), fluid, filledItem, emptyContainer, requiredMods));
    }

    private static CompletableFuture<?> filling(CachedOutput cache, Path path, ResourceLocation fluid,
                                                ResourceLocation container, ResourceLocation result,
                                                String... requiredMods) {
        JsonObject json = base("create:filling", requiredMods);
        JsonArray ingredients = new JsonArray();
        ingredients.add(itemIngredient(container));
        ingredients.add(fluidIngredient(fluid, AMOUNT));
        json.add("ingredients", ingredients);
        JsonArray results = new JsonArray();
        results.add(itemResult(result));
        json.add("results", results);
        return DataProvider.saveStable(cache, json, path);
    }

    /** 石磨：1 个物品 → 1 个产物，{@code processing_time} 是刻。 */
    private static CompletableFuture<?> milling(CachedOutput cache, Path path, ResourceLocation input,
                                                ResourceLocation output, int processingTime) {
        JsonObject json = base("create:milling");
        JsonArray ingredients = new JsonArray();
        ingredients.add(itemIngredient(input));
        json.add("ingredients", ingredients);
        JsonArray results = new JsonArray();
        results.add(itemResultWithCount(output, 1));
        json.add("results", results);
        json.addProperty("processing_time", processingTime);
        return DataProvider.saveStable(cache, json, path);
    }

    /** 混合盆：{@code heat_requirement: heated} 表示必须**加热**（烈焰炉/燃烧的加热器）。 */
    private static CompletableFuture<?> mixing(CachedOutput cache, Path path, List<JsonObject> ingredients,
                                               List<JsonObject> results) {
        JsonObject json = base("create:mixing");
        json.addProperty("heat_requirement", "heated");
        JsonArray in = new JsonArray();
        ingredients.forEach(in::add);
        json.add("ingredients", in);
        JsonArray out = new JsonArray();
        results.forEach(out::add);
        json.add("results", out);
        return DataProvider.saveStable(cache, json, path);
    }

    private static CompletableFuture<?> emptying(CachedOutput cache, Path path, ResourceLocation fluid,
                                                 ResourceLocation filled, ResourceLocation empty,
                                                 String... requiredMods) {
        JsonObject json = base("create:emptying", requiredMods);
        JsonArray ingredients = new JsonArray();
        ingredients.add(itemIngredient(filled));
        json.add("ingredients", ingredients);
        JsonArray results = new JsonArray();
        results.add(itemResult(empty));
        results.add(fluidResult(fluid, AMOUNT));
        json.add("results", results);
        return DataProvider.saveStable(cache, json, path);
    }

    /**
     * 公共外壳：条件 + 类型。
     *
     * <p>第一个条件永远是 {@code create} —— 没有 Create 就没有这些配方类型；
     * 之后是各条配方额外要求的模组（比如紫颂酱要 {@code ends_delight}）。</p>
     */
    private static JsonObject base(String type, String... requiredMods) {
        JsonObject json = new JsonObject();
        JsonArray conditions = new JsonArray();
        conditions.add(modLoaded("create"));
        for (String mod : requiredMods) {
            conditions.add(modLoaded(mod));
        }
        json.add("neoforge:conditions", conditions);
        json.addProperty("type", type);
        return json;
    }

    private static JsonObject modLoaded(String modid) {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "neoforge:mod_loaded");
        condition.addProperty("modid", modid);
        return condition;
    }

    private static JsonObject itemIngredient(ResourceLocation item) {
        JsonObject json = new JsonObject();
        json.addProperty("item", item.toString());
        return json;
    }

    /** 流体原料：NeoForge 的流体原料格式（{@code neoforge:single} = 指定流体 + 数量）。 */
    private static JsonObject fluidIngredient(ResourceLocation fluid, int amount) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "neoforge:single");
        json.addProperty("amount", amount);
        json.addProperty("fluid", fluid.toString());
        return json;
    }

    private static JsonObject itemResult(ResourceLocation item) {
        JsonObject json = new JsonObject();
        json.addProperty("id", item.toString());
        return json;
    }

    private static JsonObject itemResultWithCount(ResourceLocation item, int count) {
        JsonObject json = itemResult(item);
        json.addProperty("count", count);
        return json;
    }

    private static JsonObject fluidResult(ResourceLocation fluid, int amount) {
        JsonObject json = new JsonObject();
        json.addProperty("amount", amount);
        json.addProperty("id", fluid.toString());
        return json;
    }

    private static ResourceLocation id(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    private static ResourceLocation id(Fluid fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid);
    }
}
