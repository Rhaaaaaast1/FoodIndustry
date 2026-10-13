package dev.rhaaaaaast1.foodindustry.common.event;

import dev.rhaaaaaast1.foodindustry.common.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

import java.util.List;

/**
 * 把我们的作物接进**原版村民**的交易里。
 *
 * <p>做法完全照农夫乐事（{@code vectorwing.farmersdelight.common.event.VillagerEvents}）：
 * **不新增职业、不新增村民实体** ✓ —— 只往原版就已经生成的交易列表里追加条目。</p>
 *
 * <ul>
 *   <li>{@link VillagerTradesEvent}：只认原版**农民**（判据是注册名 {@code minecraft:farmer}，
 *       而不是枚举比较，这样别的模组重注册农民也照样命中）。农民**收购**我们的作物给绿宝石，
 *       也就是玩家把作物卖给农民 ✓；</li>
 *   <li>{@link WandererTradesEvent}：流浪商人**卖种子**给玩家（1 颗绿宝石 1 份）✓。</li>
 * </ul>
 *
 * <p><b>为什么不用 {@code @EventBusSubscriber}</b>：本项目其他事件处理器
 * （{@code GoatMilkingHandler} / {@code ManureHandler}）都走
 * {@link NeoForge#EVENT_BUS}{@code .addListener(...)} 的显式注册，
 * 这里保持一致，也方便在 {@code FoodIndustry} 构造器里一眼看到挂载点。</p>
 *
 * <p><b>为什么种子只放 {@code getRareTrades()}</b>：流浪商人默认每个"报价池"会随机抽若干条，
 * 通用池条目很多，种子塞进去经常抽不到；放进稀有池才能稳定以交易形式出现 ✓。</p>
 */
public final class VillagerTradesHandler {

    private VillagerTradesHandler() {
    }

    /** 在 {@code FoodIndustry} 构造器里调用一次。 */
    public static void register() {
        NeoForge.EVENT_BUS.addListener(VillagerTradesHandler::onVillagerTrades);
        NeoForge.EVENT_BUS.addListener(VillagerTradesHandler::onWandererTrades);
    }

    /**
     * 农民收购我们的作物。
     *
     * <p>数量与农夫乐事对齐（它给自己的洋葱/番茄是 26 个换 1 绿宝石），
     * 所以我们的作物不会比同类模组"更值钱"或"更不值钱" ✓。</p>
     */
    private static void onVillagerTrades(VillagerTradesEvent event) {
        VillagerProfession profession = event.getType();
        ResourceLocation key = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
        if (key == null || !"farmer".equals(key.getPath())) {
            return;
        }

        var trades = event.getTrades();
        // 一级农民：常见作物
        trades.get(1).add(emeraldForItems(ModItems.CHILI_PEPPER.get(), 26, 16, 2));
        trades.get(1).add(emeraldForItems(ModItems.EGGPLANT.get(), 26, 16, 2));
        // 二级农民：产量低一些 / 需要攀藤或树丛的作物，给的经验多一点
        trades.get(2).add(emeraldForItems(ModItems.CUCUMBER.get(), 16, 16, 5));
        trades.get(2).add(emeraldForItems(ModItems.BLUEBERRIES.get(), 16, 16, 5));
        trades.get(2).add(emeraldForItems(ModItems.STRAWBERRIES.get(), 16, 16, 5));
        trades.get(2).add(emeraldForItems(ModItems.WHITE_GRAPES.get(), 16, 16, 5));
        trades.get(2).add(emeraldForItems(ModItems.RED_GRAPES.get(), 16, 16, 5));
        trades.get(2).add(emeraldForItems(ModItems.TEA_LEAVES.get(), 16, 16, 5));
    }

    /**
     * 流浪商人卖我们的种子。
     *
     * <p>只列**我们自己独有的种子** ✓ —— 不再加卷心菜之类的其它模组作物（那些由那个模组自己负责），
     * 也不加花生/大豆/咖啡（它们的"种子"就是产物本身，农民那条线已经能收，没必要重复）。</p>
     */
    private static void onWandererTrades(WandererTradesEvent event) {
        List<VillagerTrades.ItemListing> rare = event.getRareTrades();
        rare.add(emeraldForItem(ModItems.CHILI_SEEDS.get(), 12, 1));
        rare.add(emeraldForItem(ModItems.EGGPLANT_SEEDS.get(), 12, 1));
        rare.add(emeraldForItem(ModItems.CUCUMBER_SEEDS.get(), 12, 1));
        rare.add(emeraldForItem(ModItems.WHITE_GRAPE_SEEDS.get(), 12, 1));
        rare.add(emeraldForItem(ModItems.RED_GRAPE_SEEDS.get(), 12, 1));
        rare.add(emeraldForItem(ModItems.BLUEBERRY_SEEDS.get(), 12, 1));
        rare.add(emeraldForItem(ModItems.STRAWBERRY_SEEDS.get(), 12, 1));
        rare.add(emeraldForItem(ModItems.TEA_SEEDS.get(), 12, 1));
    }

    /** 玩家拿 {@code count} 个 {@code item} 换 1 颗绿宝石（村民收购）。 */
    private static BasicItemListing emeraldForItems(ItemLike item, int count, int maxTrades, int xp) {
        return new BasicItemListing(new ItemStack(item, count), new ItemStack(Items.EMERALD), maxTrades, xp, 0.05F);
    }

    /** 玩家拿 1 颗绿宝石买 1 个 {@code item}（村民出售）。 */
    private static BasicItemListing emeraldForItem(ItemLike item, int maxTrades, int xp) {
        return new BasicItemListing(1, new ItemStack(item), maxTrades, xp, 0.05F);
    }
}
