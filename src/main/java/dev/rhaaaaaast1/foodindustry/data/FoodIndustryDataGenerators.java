package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 数据生成入口。由主类挂到 mod 事件总线：
 * {@code modEventBus.addListener(FoodIndustryDataGenerators::gatherData)}。
 *
 * <p>运行方式：{@code gradlew runData}，产物落在 {@code src/generated/resources}。
 * {@code --existing src/main/resources} 表示生成时会参考手写资源（纹理/模型等）。</p>
 */
public final class FoodIndustryDataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // ---------------- 客户端资源：blockstate / 模型 ----------------
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, existingFileHelper));

        // ---------------- 服务端数据 ----------------
        generator.addProvider(event.includeServer(), new ModRecipeProvider(output, lookupProvider));

        // 进度（成就）：本模组版的"野生作物"（FD 同名进度的复制品，不碰 FD 文件）
        generator.addProvider(event.includeServer(),
                new net.neoforged.neoforge.common.data.AdvancementProvider(
                        output, lookupProvider, existingFileHelper,
                        java.util.List.of(new ModAdvancementProvider())));

        // Create 的注液/排空配方（create:filling / create:emptying）
        // 属于"compat"：配方类型是 Create 的、有些产物是别的模组的物品，
        // 所以 provider 放在 compat.create 包、数据也生成到 recipe/compat/create/ 下，
        // 并带 neoforge:mod_loaded 条件 —— 没装 Create 时不会被加载。
        generator.addProvider(event.includeServer(),
                new dev.rhaaaaaast1.foodindustry.compat.create.ModCreateRecipeProvider(output));

        // 流体标签（目录是单数 fluid）
        generator.addProvider(event.includeServer(),
                new ModFluidTagsProvider(output, lookupProvider, existingFileHelper));

        ModBlockTagsProvider blockTags = new ModBlockTagsProvider(output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        // 实体类型标签（粪便生产者的动物清单）
        generator.addProvider(event.includeServer(),
                new ModEntityTagsProvider(output, lookupProvider, existingFileHelper));
        // 物品标签要能读方块标签（用于 copy(blockTag, itemTag)）
        generator.addProvider(event.includeServer(),
                new ModItemTagsProvider(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));

        generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK),
                        // 注入到原版结构箱子里的子表（内容）—— 参数集用 CHEST，
                        // 与农夫乐事的 FDChestLoot 一致
                        new LootTableProvider.SubProviderEntry(ModChestLoot::new, LootContextParamSets.CHEST),
                        // 实体掉落表（目前只有海龟）
                        new LootTableProvider.SubProviderEntry(ModEntityLootTables::new,
                                LootContextParamSets.ENTITY)),
                lookupProvider));

        // 全局战利品修改器：把上面那些子表挂到原版箱子上
        // （生成 data/foodindustry/loot_modifiers/*.json 与 data/neoforge/loot_modifiers/global_loot_modifiers.json）
        generator.addProvider(event.includeServer(), new ModGlobalLootModifiers(output, lookupProvider));

        // 原版/NeoForge 数据表：堆肥几率（data/neoforge/data_maps/item/compostables.json）
        generator.addProvider(event.includeServer(), new ModDataMaps(output, lookupProvider));

        FoodIndustry.LOGGER.debug("[{}] 数据生成器已注册", FoodIndustry.MODID);
    }

    private FoodIndustryDataGenerators() {
    }
}
