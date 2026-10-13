package dev.rhaaaaaast1.foodindustry.common.registry;

import dev.rhaaaaaast1.foodindustry.FoodIndustry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * 本模组额外注入到结构箱子里的战利品表键。
 *
 * <p>这些表本身**不挂在任何方块或实体上**，而是通过全局战利品修改器
 * （{@code neoforge:add_table}，见 {@code data.ModGlobalLootModifiers}）在
 * 原版箱子表结算时被追加进来。表名统一带 {@code chests/} 前缀，
 * 与农夫乐事的 {@code chests/fd_xxx} 命名习惯保持一致。</p>
 *
 * <p>命名里的 {@code fi_} 前缀 = foodindustry，避免和别的模组注入同一箱子时撞名。</p>
 */
public final class ModChestLootTables {

    public static final ResourceKey<LootTable> VILLAGE_PLAINS_HOUSE = register("chests/fi_village_plains_house");
    public static final ResourceKey<LootTable> VILLAGE_DESERT_HOUSE = register("chests/fi_village_desert_house");
    public static final ResourceKey<LootTable> VILLAGE_SAVANNA_HOUSE = register("chests/fi_village_savanna_house");
    public static final ResourceKey<LootTable> VILLAGE_SNOWY_HOUSE = register("chests/fi_village_snowy_house");
    public static final ResourceKey<LootTable> VILLAGE_TAIGA_HOUSE = register("chests/fi_village_taiga_house");
    public static final ResourceKey<LootTable> VILLAGE_BUTCHER = register("chests/fi_village_butcher");

    public static final ResourceKey<LootTable> SHIPWRECK_SUPPLY = register("chests/fi_shipwreck_supply");
    public static final ResourceKey<LootTable> SIMPLE_DUNGEON = register("chests/fi_simple_dungeon");
    public static final ResourceKey<LootTable> ABANDONED_MINESHAFT = register("chests/fi_abandoned_mineshaft");
    public static final ResourceKey<LootTable> PILLAGER_OUTPOST = register("chests/fi_pillager_outpost");
    public static final ResourceKey<LootTable> RUINED_PORTAL = register("chests/fi_ruined_portal");

    private static ResourceKey<LootTable> register(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, FoodIndustry.id(path));
    }

    private ModChestLootTables() {
    }
}
