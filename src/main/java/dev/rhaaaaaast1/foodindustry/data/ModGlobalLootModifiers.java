package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import dev.rhaaaaaast1.foodindustry.common.registry.ModChestLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

/**
 * 全局战利品修改器（global loot modifier）生成。
 *
 * <p>作用：原版箱子（村庄民房、沉船、地牢、矿道、前哨站、废墟传送门）在被打开、
 * 原版战利品结算完之后，额外把我们在 {@link ModChestLoot} 里定义的子表追加进去。
 * 这么做<b>不改原版数据文件</b>，也不会覆盖别的模组对同一个箱子的注入
 * —— 各家的修改器会依次执行、互相叠加。</p>
 *
 * <p>这里用的是 NeoForge 自带的 {@code neoforge:add_table} 修改器类型
 * （见 {@code NeoForgeMod.ADD_TABLE_LOOT_MODIFIER_TYPE}），
 * 所以<b>不需要自己注册 codec</b>，也不需要像农夫乐事那样额外写一个子类
 * （它是为了挂自己的配置开关才子类化的，本模组只有无条件生效一种行为）。</p>
 *
 * <p>生成结果：</p>
 * <ul>
 *   <li>{@code data/foodindustry/loot_modifiers/add_loot_<箱子名>.json} —— 每个箱子一个</li>
 *   <li>{@code data/neoforge/loot_modifiers/global_loot_modifiers.json} —— 上面这些的索引</li>
 * </ul>
 */
public class ModGlobalLootModifiers extends GlobalLootModifierProvider {

    public ModGlobalLootModifiers(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, FoodIndustry.MODID);
    }

    @Override
    protected void start() {
        // ---------------- 村庄民房（5 群系）+ 屠宰店 ----------------
        add(ModChestLootTables.VILLAGE_PLAINS_HOUSE, BuiltInLootTables.VILLAGE_PLAINS_HOUSE);
        add(ModChestLootTables.VILLAGE_DESERT_HOUSE, BuiltInLootTables.VILLAGE_DESERT_HOUSE);
        add(ModChestLootTables.VILLAGE_SAVANNA_HOUSE, BuiltInLootTables.VILLAGE_SAVANNA_HOUSE);
        add(ModChestLootTables.VILLAGE_SNOWY_HOUSE, BuiltInLootTables.VILLAGE_SNOWY_HOUSE);
        add(ModChestLootTables.VILLAGE_TAIGA_HOUSE, BuiltInLootTables.VILLAGE_TAIGA_HOUSE);
        add(ModChestLootTables.VILLAGE_BUTCHER, BuiltInLootTables.VILLAGE_BUTCHER);

        // ---------------- 冒险/补给类箱子 ----------------
        add(ModChestLootTables.SHIPWRECK_SUPPLY, BuiltInLootTables.SHIPWRECK_SUPPLY);
        add(ModChestLootTables.SIMPLE_DUNGEON, BuiltInLootTables.SIMPLE_DUNGEON);
        add(ModChestLootTables.ABANDONED_MINESHAFT, BuiltInLootTables.ABANDONED_MINESHAFT);
        add(ModChestLootTables.PILLAGER_OUTPOST, BuiltInLootTables.PILLAGER_OUTPOST);
        add(ModChestLootTables.RUINED_PORTAL, BuiltInLootTables.RUINED_PORTAL);
    }

    /**
     * 注册一条"把 {@code ourTable} 追加到 {@code vanillaTable} 上"的修改器。
     *
     * <p>修改器名字取目标箱子路径的最后一段（{@code minecraft:chests/village/village_plains_house}
     * → {@code add_loot_village_plains_house}），与农夫乐事的命名一致 ——
     * 不用 {@code getPath()} 全路径是因为那会生成 {@code add_loot_chests_village_village_plains_house}
     * 这种把 {@code chests/} 和村庄目录重复两遍的长名字。</p>
     */
    private void add(ResourceKey<LootTable> ourTable, ResourceKey<LootTable> vanillaTable) {
        String path = vanillaTable.location().getPath();
        String shortName = path.substring(path.lastIndexOf('/') + 1);
        this.add("add_loot_" + shortName, new AddTableLootModifier(
                new LootItemCondition[]{LootTableIdCondition.builder(vanillaTable.location()).build()},
                ourTable));
    }
}
