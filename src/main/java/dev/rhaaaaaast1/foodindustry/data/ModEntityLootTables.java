package dev.rhaaaaaast1.foodindustry.data;

import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.stream.Stream;

/**
 * 实体掉落表。目前<b>只有海龟</b>一张 —— 给原版海龟补上"杀了掉海龟肉"。
 *
 * <p>为什么要覆盖原版：原版 {@code minecraft:entities/turtle} 掉的是海草
 * （权重 3，0..2 个）和被雷劈死时的碗，<b>没有肉</b>。我们注册了海龟肉，如果没人掉它
 * 就只能靠创造模式拿。所以这里重写整张表。</p>
 *
 * <p>数量与附魔行为<b>照抄原版猪</b>（猪只有猪排一池，结构最干净）：</p>
 * <ul>
 *   <li>{@code set_count} 1..3 个；</li>
 *   <li>{@code enchanted_count_increase}：抢夺每级 +0..1 个；</li>
 *   <li>{@code furnace_smelt}：被火烧死、或凶手主手拿着带 {@code minecraft:smelts_loot}
 *       标签附魔的武器时，直接掉熟肉（{@code shouldSmeltLoot()} 就是那个 any_of 条件）。</li>
 * </ul>
 *
 * <p><b>为什么不用 {@code ApplyBonusCount.addOreBonusCount}（时运那种）</b>：
 * 它的 {@code getReferencedContextParams()} 返回 {@code {TOOL}}，而实体战利品上下文
 * （{@code LootContextParamSets.ENTITY}）里<b>没有 TOOL</b> —— 会直接报
 * {@code Found validation problem ... Parameters [<parameter minecraft:tool>] are not provided in this context}。
 * 实体掉落要"抢夺加成"就得用 {@link EnchantedCountIncreaseFunction}（原版所有生物都这么写）。</p>
 *
 * <p>注意继承 {@link EntityLootSubProvider} 会在 1.21 上带一个 {@code generate()} 的
 * 弃用告警 —— 那是给"只给已有实体补一层战利品修改"的 NeoForge 新 API 让路，
 * 而本表是整张覆盖，正是原版 {@code VanillaEntityLoot} 的用法，所以忽略该告警。</p>
 */
@SuppressWarnings("deprecation")
public final class ModEntityLootTables extends EntityLootSubProvider {

    public ModEntityLootTables(HolderLookup.Provider registries) {
        // 用 VANILLA_SET（只开原版功能旗标）：海龟不是实验性实体，
        // 用 DEFAULT_FLAGS 会把 trade_rebalance 数据包也算进去，没必要。
        super(FeatureFlags.VANILLA_SET, registries);
    }

    @Override
    public void generate() {
        this.add(EntityType.TURTLE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ModItems.TURTLE_MEAT.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                                        this.registries,
                                        UniformGenerator.between(0.0F, 1.0F)))
                                .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())))));
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        // 只声明我们真正覆盖的那一个，免得 NeoForge 把"模组注册的实体没给表"也算成缺漏。
        return Stream.of(EntityType.TURTLE);
    }
}
